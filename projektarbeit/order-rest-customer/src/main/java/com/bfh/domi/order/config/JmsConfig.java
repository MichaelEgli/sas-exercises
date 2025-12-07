package com.bfh.domi.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
public class JmsConfig {
    @Bean
    public MessageConverter jacksonJsonMessageConverter() {
        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("typeId");
        Map<String, Class<?>> mappings = Map.of(
                "ShippingOrder", com.bfh.domi.order.order.dto.ShippingOrder.class,
                "ShippingInfo", com.bfh.domi.order.order.dto.ShippingInfo.class
        );
        converter.setTypeIdMappings(mappings);
        return converter;
    }


}
