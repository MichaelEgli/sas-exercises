package ch.bfh.sad.persistence.hr.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@NamedQuery(name = Department.AVG_SALARY,
        query = """
                select new ch.bfh.sad.persistence.hr.dto.DepartmentSalaryStatistics(e.department.name, avg(e.salary))
                from Employee e group by e.department.name
                """)
@Entity
public class Department extends BaseEntity {

    public static final String AVG_SALARY = "Department.avgSalary";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(name = "department_seq", sequenceName = "department_seq")
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employee.setDepartment(this);
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employee.setDepartment(null);
        employees.remove(employee);
    }
}
