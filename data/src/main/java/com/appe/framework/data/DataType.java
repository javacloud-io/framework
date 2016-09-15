package com.appe.framework.data;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

import com.appe.framework.util.Objects;

/**
 * Define basic data types and it mapping from primary type. UTF8/BYTEB is limit to 4k at the moment.
 * 
 * @author tobi
 *
 */
public enum DataType {
	BOOLEAN,
	CHAR,
	INTEGER,
	LONG,
	FLOAT,
	DOUBLE,
	UTF8,
	DATE,
	
	COUNTER,	//MAP to double counter
	BYTEB;		//RAW BYTEs or BASE64 ENCODED.
	
	//MAP PRIMARY DATA TYPE
	public static final Map<Class<?>, DataType> PRIMITIVES = Objects.asMap(
			boolean.class, 	BOOLEAN,
			Boolean.class,	BOOLEAN,
			char.class, 	CHAR,
			Character.class,CHAR,
			int.class,		INTEGER,
			Integer.class,	INTEGER,
			long.class,		LONG,
			Long.class,		LONG,
			float.class,	FLOAT,
			Float.class,	FLOAT,
			double.class,	DOUBLE,
			Double.class,	DOUBLE,
			String.class,	UTF8,
			Date.class,		DATE,
			byte[].class,	BYTEB,
			ByteBuffer.class,BYTEB);
}