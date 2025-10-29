package ch.bfh.eglim8.spring.jpatest.repo;

import ch.bfh.eglim8.spring.jpatest.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
