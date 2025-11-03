package com.bfh.eglim8.spring.rm;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bfh.eglim8.spring.rm.entities.Address;
import com.bfh.eglim8.spring.rm.entities.Department;
import com.bfh.eglim8.spring.rm.entities.Employee;
import com.bfh.eglim8.spring.rm.entities.Phone;
import com.bfh.eglim8.spring.rm.entities.Project;
import com.bfh.eglim8.spring.rm.repos.DepartmentRepository;
import com.bfh.eglim8.spring.rm.repos.EmployeeRepository;
import com.bfh.eglim8.spring.rm.repos.PhoneRepository;
import com.bfh.eglim8.spring.rm.repos.ProjectRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RmApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
	void testEmployeeRm() {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Michael");
        employee.setSalary(160000L);
        employee = employeeRepository.saveAndFlush(employee);

        Department department = new Department();
        department.setId(10);
        department.setName("Spedition");
        department.addEmployee(employee); // bidirectional
        department = departmentRepository.saveAndFlush(department);
        employee.setDepartment(department); // bidirectional

        Address address = new Address();
        address.setId(100);
        address.setStreet("Grabmattweg 16");
        address.setCity("Neuenegg");
        address.setState("BE");
        address.setZip("3176");

        Address employeeAddress = em.find(Address.class, 100);
        employee.setAddress(employeeAddress);

        Phone phone = new Phone();
        phone.setId(1000);
        phone.setPhonenumber("079 777 88 99");
        phone.setType("PRIVAT");
        phone.setEmployee(employee);
        phone = phoneRepository.saveAndFlush(phone);
        employee.addPhone(phone);

        Project project = new Project();
        project.setId(10000);
        project.setName("Java Spring Lernen");
        project.getEmployees().add(employee); // bidirectional
        project = projectRepository.saveAndFlush(project);
        employee.getProjects().add(project); // bidirectional

        // Final save employee
        employee = employeeRepository.saveAndFlush(employee);
        assertThat(employee.getId()).isEqualTo(1);
        employeeRepository.findById(employee.getId());

        em.clear();

        Employee employeeFromDb = em.find(Employee.class, employee.getId());

        assertThat(employeeFromDb.getId()).isNotNull();
        assertThat(employeeFromDb.getId()).isEqualTo(1);
        assertThat(employeeFromDb.getDepartment().getId()).isEqualTo(10);
        assertThat(employeeFromDb.getDepartment().getName()).isEqualTo("Spedition");
        // assertThat(employeeFromDb.getAddress().getCity()).isEqualTo("Neuenegg");
        assertThat(employeeFromDb.getPhones().getFirst().getPhonenumber()).isEqualTo("079 777 88 99");
        assertThat(employeeFromDb.getProjects().getFirst().getName()).isEqualTo("Java Spring Lernen");

        // Bidirectional - from department to employee
        Department departmentFromDb = em.find(Department.class, department.getId());
        assertThat(departmentFromDb.getEmployees().getFirst().getName()).isEqualTo("Michael");

        // Bidirectional - from project to employee
        Project projectFromDb = em.find(Project.class, project.getId());
        assertThat(projectFromDb.getEmployees().getFirst().getName()).isEqualTo("Michael");
    }

}
