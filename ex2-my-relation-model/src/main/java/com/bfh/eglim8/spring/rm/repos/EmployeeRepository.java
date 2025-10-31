package com.bfh.eglim8.spring.rm.repos;

import com.bfh.eglim8.spring.rm.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
