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
package com.appe.registry.internal;

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