/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.appe.sec.Codecs;

/**
 * It's much more powerful to use XML Plist instead of java properties file.
 * 
 * Check out: http://en.wikipedia.org/wiki/Property_list for more details on the format itself.
 * 
 * @author aimee
 *
 */
public final class Dictionaries {
	private Dictionaries() {
	}
	
	/**
	 * Cast given object to dictionary if NEED SO MAKE SURE WE ALWAYS SUCCESS DOING SO.
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Dictionary cast(Object obj) throws IOException {
		if(obj instanceof Dictionary) {
			return	(Dictionary)obj;
		} else if(obj instanceof Map) {
			return new Dictionary((Map<String, Object>)obj);
		}
		//TODO: SHOULD DOING DEP CONVERSION
		return Objects.asDict("dict", obj);
	}
	
	/**
	 * Using default conveter
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String encodeURL(Dictionary params) throws IOException {
		return encodeURL(params, Converter.STRING);
	}
	
	/**
	 * TODO: 
	 * -need to take care of multiple value encoded.
	 * -consider replace '+' with '%20'
	 * 
	 * @param params
	 * @param converter
	 * @return
	 * @throws IOException
	 */
	public static String encodeURL(Dictionary params, Converter<String> converter) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(String name: params.keySet()) {
			String value = converter.convert(params.get(name));
			if(value == null) {
				continue;
			}
			
			//&NAME=VALUE
			sb.append("&");
			sb.append(URLEncoder.encode(name, Codecs.UTF8));
			sb.append("=");
			sb.append(URLEncoder.encode(value, Codecs.UTF8));
		}
		
		//SKIP THE FIRST &
		if(sb.length() > 0) {
			return sb.substring(1);
		}
		
		//DON'T HAVE ANYTHING
		return sb.toString();
	}
	
	/**
	 * Parsing simple NAME/VALUE
	 * 
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static Dictionary decodeURL(String params) throws IOException {
		return decodeURL(params, Converter.OBJECT);
	}
	
	/**
	 * 
	 * TODO: need to take care of multiple value decoded.
	 * @param params
	 * @param converter
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Dictionary decodeURL(String params, Converter<Object> converter) throws IOException {
		//NEW INSTANCE OBJECT
		Dictionary props = new Dictionary();
		
		//PARSE PROPERTIES
		String[] list = params.split("&");
		for(String p: list) {
			
			//IGNORE EMPTY ONE
			if(Objects.isEmpty(p)) {
				continue;
			}
			
			//JUST IGNORE ALL THE NAME/VALUE EMPTY
			//TODO: need to support multiple value
			int index = p.indexOf('=');
			String name, value;
			if(index > 0) {
				name = URLDecoder.decode(p.substring(0, index), Codecs.UTF8);
				value= URLDecoder.decode(p.substring(index + 1),Codecs.UTF8);
			} else {
				name  = URLDecoder.decode(p, Codecs.UTF8);
				value = "";
			}
			
			//SET VALUE
			props.put(name, converter.convert(value));
		}
		return props;
	}
	
	/**
	 * Read the XML plist file, just return Apper
	 * @param ins
	 * @return
	 * @throws IOException
	 */
	public static Dictionary readPlist(InputStream ins) throws IOException {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			//DISABLE DTD VALIDATION BY RETURN DUMMY SOURCE
			documentBuilder.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});
			Document xmlDoc = documentBuilder.parse(ins);
			
			//FIRST ELEMENT ALWAYS A PLIST
	        Node element = xmlDoc.getDocumentElement();
	        if(!"plist".equals(element.getNodeName())) {
	        	throw new IOException("Expected plist but was " + element.getNodeName());
	        }
	        
	        //FIRST FIRST CHILD (HAVE TO BE a DICT)
	        element = element.getFirstChild();
	        while(element != null && element.getNodeType() != Node.ELEMENT_NODE) {
	        	element = element.getNextSibling();
	        }
			return (element == null? new Dictionary() : (Dictionary)parseElement(element));
		} catch (ParserConfigurationException ex) {
			throw new IOException(ex);
		} catch (SAXException ex) {
			throw new IOException(ex);
		} catch (ParseException ex) {
			throw new IOException(ex);
		}   
	}
	
	/**
	 * Recursively read out all the Node ELEMENT.
	 * @param element
	 * @return
	 * @throws ParseException  
	 */
	private	static Object parseElement(Node element) throws ParseException {
		String type = element.getNodeName();
		if("true".equals(type)) {
			return	Boolean.TRUE;
		} else if("false".equals(type)) {
			return	Boolean.FALSE;
		} else if("integer".equals(type)) {
			return Long.valueOf(element.getTextContent());
		} else if("real".equals(type)) {
			return Double.valueOf(element.getTextContent());
		} else if("string".equals(type)) {
			return	element.getTextContent();
		} else if("date".equals(type)) {
			return DateFormats.get().parse(element.getTextContent());
		} else if("data".equals(type)) {
			return	Codecs.decodeBase64(element.getTextContent(), false);
		} else if("array".equals(type)) {
			ArrayList<Object> list = new ArrayList<Object>();
			NodeList elements = element.getChildNodes();
            for(int i = 0; i < elements.getLength(); i++) {
                Node node = elements.item(i);
                if(node.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                }                        
                list.add(parseElement(node));
            }
            return list;
		} else if("dict".equals(type)) {
			Dictionary dict = new Dictionary(new LinkedHashMap<String, Object>());
			element = element.getFirstChild();
			while(element != null) {
				Node key = element;
				if(key.getNodeType() != Node.ELEMENT_NODE) {
					element = element.getNextSibling();
					continue;
				}
				if(!"key".equals(key.getNodeName())) {
					throw new ParseException("Expected key but was: " + key.getNodeName(), -1);
				}
				
				Node value = key.getNextSibling();
				while(value.getNodeType() != Node.ELEMENT_NODE) {
					value = value.getNextSibling();
				}
				dict.put(key.getTextContent(), parseElement(value));
				
				//MOVE TO NEXT ONE
				element = value.getNextSibling();
			}
			return dict;
		} else {
			throw new ParseException("Unexpected type: " + type, -1);
		}
	}
	
	/**
	 * Default to NOT compact MODE.
	 * 
	 * @param plist
	 * @param out
	 * @throws IOException
	 */
	public static void writePlist(Dictionary plist, OutputStream out) throws IOException {
		writePlist(plist, out, false);
	}
	
	/**
	 * Recursively write the XML using NODE type. In compact mode, we just transfer every in raw format.
	 * @param plist
	 * @param out
	 * @param compact
	 * @throws IOException
	 */
	public static void writePlist(Dictionary plist, OutputStream out, boolean compact) throws IOException {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDoc = documentBuilder.newDocument();
			
			//ROOT PLIST
			Node element = xmlDoc.createElement("plist");
			xmlDoc.appendChild(element);
			
			element.appendChild(createElement(xmlDoc, plist, compact));
			
			//CREATE OUTPUT TO STREAM
			Transformer transformer = SAXTransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			//FORMAT IF NON COMPACT MODE
			if(!compact) {
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Apple//DTD PLIST 1.0//EN");
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.apple.com/DTDs/PropertyList-1.0.dtd");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			} else {
				//GET RID OF XML DECLARATION
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			transformer.transform(new DOMSource(xmlDoc), new StreamResult(out));
		} catch (ParserConfigurationException ex) {
			throw new IOException(ex);
		} catch (TransformerConfigurationException ex) {
			throw new IOException(ex);
		} catch (TransformerFactoryConfigurationError ex) {
			throw new IOException(ex);
		} catch (TransformerException ex) {
			throw new IOException(ex);
		}
	}
	
	/**
	 * Create an object element, with hint compact mode or NOT
	 * @param object
	 * @return
	 */
	private static Node createElement(Document xmlDoc, Object value, boolean compact) {
		if(value instanceof Boolean) {
			return	xmlDoc.createElement(value.toString());
		} else if(value instanceof Double || value instanceof Float) {
			Node element = xmlDoc.createElement("real");
			element.setTextContent(value.toString());
			return element;
		} else if(value instanceof Number) {
			Node element = xmlDoc.createElement("integer");
			element.setTextContent(value.toString());
			return element;
		} else if(value instanceof java.util.Date) {
			Node element = xmlDoc.createElement("date");
			element.setTextContent(DateFormats.get().format((java.util.Date)value));
			return element;
		} else if(value instanceof byte[]) {//BYTEs => ENCODE
			//broken down in 64 characters/line
			String base64;
			if(compact) {
				base64 = Codecs.encodeBase64((byte[])value, false);
			} else {
				base64 = Codecs.encodePEM((byte[])value);
			}
			
			Node element = xmlDoc.createElement("data");
			element.setTextContent(base64);
			return element;
		} else if(value instanceof Collection) {//COLLECTION LIST
			Node element = xmlDoc.createElement("array");
			for(Object ov: (Collection<?>)value) {
				element.appendChild(createElement(xmlDoc, ov, compact));
			}
			return element;
		} else if(value instanceof Object[]) {//OBJECT LIST
			Node element = xmlDoc.createElement("array");
			for(Object ov: (Object[])value) {
				element.appendChild(createElement(xmlDoc, ov, compact));
			}
			return element;
		} else if(value instanceof Dictionary) {
			Node element = xmlDoc.createElement("dict");
			Dictionary props = (Dictionary)value;
			for(String name: props.keySet()) {
				Node key = xmlDoc.createElement("key");
				key.setTextContent(name);
				element.appendChild(key);
				element.appendChild(createElement(xmlDoc, props.get(name), compact));
			}
			return element;
		} else {//EVRYTHING ELSE JUST STRING
			Node element = xmlDoc.createElement("string");
			if(value != null) {
				element.setTextContent(value.toString());
			}
			return element;
		}
	}
}
