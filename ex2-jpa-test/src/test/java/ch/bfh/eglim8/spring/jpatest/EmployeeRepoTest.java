package ch.bfh.eglim8.spring.jpatest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class EmployeeRepoTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void createEmployee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John");
        employee.setSalary(10000);
        Employee savedEmployee = employeeRepository.save(employee);

        assertThat(savedEmployee.getId()).isEqualTo(1);

        employeeRepository.findById(1).ifPresent(e -> employeeRepository.delete(e));

        Optional<Employee> deletedEmployee = employeeRepository.findById(1);
        assertThat(deletedEmployee).isEmpty();
    }
}
