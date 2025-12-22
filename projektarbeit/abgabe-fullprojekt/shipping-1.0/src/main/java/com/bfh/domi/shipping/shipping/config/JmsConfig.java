package com.bfh.domi.shipping.shipping.config;

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
                "ShippingOrder", com.bfh.domi.shipping.shipping.dto.ShippingOrder.class,
                "ShippingInfo", com.bfh.domi.shipping.shipping.dto.ShippingInfo.class,
                "ShippingCancel", com.bfh.domi.shipping.shipping.dto.ShippingCancel.class
        );
        converter.setTypeIdMappings(mappings);
        return converter;
    }
}
