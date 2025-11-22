package ch.bfh.eglim8.spring.hello;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ConfigExerciseApplication.class)
public class HelloServiceTest {

    @Autowired
    private HelloService helloService;

    @Mock
    private GreetingService greetingService;

    @Test
    public void testHelloService() {
        assertThat(helloService).isNotNull();

        String resultNewie = helloService.sayHello("Michael", 79);
        assert resultNewie.contains("Newie");

        String resultOldie = helloService.sayHello("Roman", 80);
        assert resultOldie.contains("Oldie");
    }

    @Test
    public void testGreetingService() {
        Mockito.when(greetingService.getGreeting()).thenReturn("gugus");
        String resultNewie = helloService.sayHello("Michael", 79);
        System.out.println(resultNewie);
    }

    @Test
    void contextLoads() {
    }
}
