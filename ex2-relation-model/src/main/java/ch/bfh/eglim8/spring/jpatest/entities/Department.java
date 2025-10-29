package ch.bfh.eglim8.spring.jpatest.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Department {

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

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Employee employee) {
        employee.setDepartment(this);
        employees.add(employee);
    }

}
