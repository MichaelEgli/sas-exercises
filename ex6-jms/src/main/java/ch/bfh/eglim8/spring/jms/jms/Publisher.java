package ch.bfh.eglim8.spring.jms.jms;

import jakarta.annotation.Resource;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Publisher implements CommandLineRunner {

    @Value("${chat.topic}")
    private String topic;
    @Value("${chat.user}")
    private String user;

    private final JmsTemplate jmsTemplate;

    public Publisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter messages to sent...");
        while (true) {
            String text = scanner.nextLine();
            if (text.isEmpty()) break;
            ChatMessage message = new ChatMessage(user, text);
            jmsTemplate.convertAndSend(topic, message);
 /*           jmsTemplate.send(topic, session -> {
                Message message = session.createTextMessage(text);
                message.setStringProperty("user", user);
                return message;
            });
 */       }
    }
}
