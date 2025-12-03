package org.example;

import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;
import java.util.Map;

@Configuration
public class JmsConfig {

	@Bean
	@Profile("csv")
	public MessageConverter chatMessageCsvConverter() {
		return new ChatMessageCsvConverter();
	}

	@Bean
	@Profile("default")
	public MessageConverter jacksonJsonMessageConverter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("typeId");
		converter.setTypeIdMappings(Map.of("chat", ChatMessage.class));
		return converter;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
			DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setErrorHandler(throwable -> System.out.println("--- Error: " + throwable.getCause().getMessage()));
		return factory;
	}
}
