package ch.bfh.eglim8.spring.hello;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ConfigExerciseApplication.class)
public class HelloServiceTest {

    @Mock
    private GreetingService greetingService;

    @InjectMocks
    private HelloService helloService;

    @Test
    public void testHelloService() {
        assertThat(helloService).isNotNull();

        String resultNewie = helloService.sayHello("Michael", 79);
        assert resultNewie.contains("Newie");

        String resultOldie = helloService.sayHello("Roman", 80);
        assert resultOldie.contains("Oldie");
    }

    @Test
    public void testGreetingServiceWithMock() {
        when(greetingService.getGreeting()).thenReturn("mocked evening");

        String resultNewie = helloService.sayHello("Michael", 79);
        assertThat(resultNewie).contains("mocked evening");
    }

    @Test
    void contextLoads() {
    }
}
