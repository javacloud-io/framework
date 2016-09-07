package com.appe.framework.internal;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.inject.Named;

/**
 * Quick implement of java @Named so it can be use portably.
 * 
 * @author ho
 * 
 */
@SuppressWarnings("all")
public class AnnotatedName implements Named, Serializable {
	private static final long serialVersionUID = 8796749510361477021L;

	private String value;

	public AnnotatedName(String value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Named.class;
	}

	@Override
	public String value() {
		return value;
	}
	
	@Override
	public int hashCode() {
		// This is specified in java.lang.Annotation.
		return (127 * "value".hashCode()) ^ value.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Named)) {
			return false;
		}

		Named other = (Named) o;
		return value.equals(other.value());
	}
}
