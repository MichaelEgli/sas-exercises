package ch.bfh.eglim8.spring.hello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ConfigExerciseApplication.class)
public class HelloServiceTest {

    @Autowired
    private HelloService helloService;

    @Test
    public void testHelloService() {
        assertThat(helloService).isNotNull();

        String resultNewie = helloService.sayHello("Michael", 79);
        System.out.println(resultNewie);
        assert resultNewie.contains("Newie");

        String resultOldie = helloService.sayHello("Roman", 80);
        System.out.println(resultOldie);
        assert resultNewie.contains("Oldie");
    }

    @Test
    void contextLoads() {
    }
}
