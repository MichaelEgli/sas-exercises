package ch.bfh.eglim8.spring.jms.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {

    // Beispiel mit Selector:
    // @JmsListener(destination = "ChatTopic", selector = "user <> '${chat.user}'")
    @JmsListener(destination = "ChatTopic")
    public void receiveMessage(String message) {
        System.out.println("Received Message: " + message);
    }
}
