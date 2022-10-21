package javacloud.framework.json.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javacloud.framework.util.DateFormats;

public class JacksonSerde extends SimpleModule {
	private static final long serialVersionUID = 1204273612914943898L;
	private static final DateFormat DF_ISO8601_S3 = DateFormats.getUTC(DateFormats.ISO8601_S3);
	
	@SuppressWarnings("serial")
	JacksonSerde() {
		super("javacloud.json");
		addDeserializer(Date.class, new StdDeserializer<Date>(Date.class) {
			@Override
			public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				String sdate = p.getText();
				try {
					return parseDate(sdate);
				} catch (ParseException ex) {
					throw new JsonProcessingException("Not support date format " + sdate, ex) {};
				}
			}
		});
		
		addSerializer(Date.class, new StdSerializer<Date>(Date.class) {
			@Override
			public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				gen.writeObject(formatDate(value));
			}
		});
	}
	
	public static final Date parseDate(String sdate) throws ParseException {
		if (sdate.endsWith("Z") && sdate.length() >= 10) {
			if (sdate.charAt(sdate.length() - 5) == '.') {
				synchronized(DF_ISO8601_S3) {
					return DF_ISO8601_S3.parse(sdate);
				}
			}
			return DateFormats.getUTC(DateFormats.ISO8601).parse(sdate);
		}
		return DateFormats.get(DateFormats.LOCAL, TimeZone.getDefault()).parse(sdate);
	}
	
	public static final String formatDate(Date date) {
		synchronized(DF_ISO8601_S3) {
			return DF_ISO8601_S3.format(date);
		}
	}
}