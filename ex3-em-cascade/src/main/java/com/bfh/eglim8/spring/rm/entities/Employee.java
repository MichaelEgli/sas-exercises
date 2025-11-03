package com.bfh.eglim8.spring.rm.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Employee {

    @Id
    private Integer id;
    private String name;
    private long salary;
    @ManyToOne
    private Department department;
    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "employee")
    private List<Phone> phones = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "employees")
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

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phone) {
        this.phones = phone;
    }

    public void addPhone(Phone phone) {
        phone.setEmployee(this);
        phones.add(phone);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
