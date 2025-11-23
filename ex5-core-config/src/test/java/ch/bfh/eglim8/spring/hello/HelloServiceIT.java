package ch.bfh.eglim8.spring.hello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ConfigExerciseApplication.class)
@TestPropertySource("classpath:test.properties")
public class HelloServiceIT {

    @Autowired
    private HelloService helloService;

    @Value("${times-of-day.morning}")
    String morning;

    @Test
    public void testHelloService() {
        assertThat(helloService).isNotNull();

        String resultNewie = helloService.sayHello("Michael", 79);
        System.out.println(resultNewie);
        assert resultNewie.contains("evening");
        System.out.println(morning);
    }

    @Test
    public void contextLoads() {}


}
