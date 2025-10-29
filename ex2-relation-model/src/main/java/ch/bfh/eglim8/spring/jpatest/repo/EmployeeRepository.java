package ch.bfh.eglim8.spring.jpatest.repo;

import ch.bfh.eglim8.spring.jpatest.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}

