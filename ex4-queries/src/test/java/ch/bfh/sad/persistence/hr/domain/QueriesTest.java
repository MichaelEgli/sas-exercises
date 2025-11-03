package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueriesTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testFindAllEmployeesByName() {
        List<Employee> employees = employeeRepository.findByName("Hans Boss");
        assertNotNull(employees);
        assertThat(employees.size()).isEqualTo(1);
    }

    @Test
    void testFindAllEmployeesOfState() {
        List<Employee> employees = employeeRepository.findAllEmployeesOfState("ZH");
        assertNotNull(employees);
        assertThat(employees.size()).isEqualTo(3);
    }

   //@Test
    void testFindEmployeeWithLowestSalary() {
        Employee employee = employeeRepository.findEmployeeWithLowestSalary();
        assertNotNull(employee);
    }
}
