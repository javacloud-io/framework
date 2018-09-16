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
package io.javacloud.framework.i18n.internal;

import io.javacloud.framework.config.internal.ConfigInvocationHandler;
import io.javacloud.framework.i18n.LocaleContext;

import java.text.MessageFormat;

/**
 * Dynamic config handler to ensure property value is always invalidate at run time
 * 
 * @author ho
 *
 */
public abstract class MessageInvocationHandler extends ConfigInvocationHandler {
	protected final LocaleContext contextLocale;
	protected MessageInvocationHandler(LocaleContext contextLocale) {
		this.contextLocale = contextLocale;
	}
	
	/**
	 * Format value with arguments and locale
	 * 
	 * @param value
	 * @param args
	 * @return
	 */
	@Override
	protected String formatValue(String value, Object[] args) {
		MessageFormat mf = new MessageFormat(value, contextLocale.get());
		return mf.format(args);
	}
}
