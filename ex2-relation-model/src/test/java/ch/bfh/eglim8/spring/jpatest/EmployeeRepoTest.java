package ch.bfh.eglim8.spring.jpatest;

import ch.bfh.eglim8.spring.jpatest.entities.Employee;
import ch.bfh.eglim8.spring.jpatest.repo.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepoTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void createEmployee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Michael");
        employee.setSalary(12000L);
        Employee savedEmployee = employeeRepository.save(employee);

        assertThat(savedEmployee.getId()).isEqualTo(1);

        employeeRepository.findById(1).ifPresent(e -> employeeRepository.delete(e));

        Optional<Employee> deletedEmployee = employeeRepository.findById(1);
        assertThat(deletedEmployee).isEmpty();
    }
}
