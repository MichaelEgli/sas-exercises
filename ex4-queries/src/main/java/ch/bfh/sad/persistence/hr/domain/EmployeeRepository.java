package ch.bfh.sad.persistence.hr.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e WHERE e.name = :name")
    List<Employee> findByName(@Param("name") String name);

    @Query("SELECT e FROM Employee e WHERE e.address.state = :state")
    List<Employee> findAllEmployeesOfState(@Param("state") String state);

    @Query("SELECT MIN(e.salary) FROM Employee e")
    Employee findEmployeeWithLowestSalary();
}
