package javacloud.framework.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class CustomSerde extends SimpleModule {
	private static final long serialVersionUID = 1L;

	public CustomSerde() {
		super("custom");
		System.out.println("register something here!!!");
	}
}
