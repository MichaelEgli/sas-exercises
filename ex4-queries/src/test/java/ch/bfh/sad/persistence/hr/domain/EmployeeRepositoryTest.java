package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void createEmployee() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(10000L);
        Employee savedEmployee = employeeRepository.save(employee);

        Integer id = savedEmployee.getId();
        assertThat(id).isNotNull();

        employeeRepository.findById(id).ifPresent(e -> employeeRepository.delete(e));
        employeeRepository.deleteById(id);

        Optional<Employee> deletedEmployee = employeeRepository.findById(id);
        assertThat(deletedEmployee).isEmpty();

        // Sonst werden keine SQL Statements ausgeführt
        employeeRepository.flush();
    }
}