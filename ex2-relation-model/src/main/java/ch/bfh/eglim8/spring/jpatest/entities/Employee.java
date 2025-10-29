package ch.bfh.eglim8.spring.jpatest.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Integer id;

    private String name;
    private Long salary;

    @ManyToOne
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne
    private Employee boss;

    @ManyToOne
    private Department department;

    @OneToMany(mappedBy = "boss")
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Phone> phones = new ArrayList<>();

    @ManyToMany(mappedBy = "employees")
    private List<Project> projects = new ArrayList<>();

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

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Employee getBoss() {
        return boss;
    }

    public void setBoss(Employee boss) {
        this.boss = boss;
    }
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void addEmployee(Employee employee) {
        employee.setBoss(this);
        employees.add(employee);
    }

    public void addPhone(Phone phone) {
        phone.setEmployee(this);
        phones.add(phone);
    }

}
