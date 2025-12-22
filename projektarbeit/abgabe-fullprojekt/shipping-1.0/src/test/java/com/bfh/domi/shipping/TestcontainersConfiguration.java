package com.bfh.domi.shipping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.activemq.ActiveMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    @Profile("!gitlab")
    ActiveMQContainer activeMQContainer() {
        return new ActiveMQContainer(DockerImageName.parse("apache/activemq-classic:6.2.0"));
    }
}
