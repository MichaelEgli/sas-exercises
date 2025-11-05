package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.dto.DepartmentSalaryStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<DepartmentSalaryStatistics> avgSalary();
}
