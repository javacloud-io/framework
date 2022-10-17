package javacloud.framework.json;

public class CustomSerde implements JacksonSerde {
	@Override
	public void configure(com.fasterxml.jackson.databind.Module module) {
		System.out.println("register something here!!!");
	}
}
