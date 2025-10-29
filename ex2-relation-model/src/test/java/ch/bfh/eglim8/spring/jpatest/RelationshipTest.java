package ch.bfh.eglim8.spring.jpatest;

import ch.bfh.eglim8.spring.jpatest.TestcontainersConfiguration;
import ch.bfh.eglim8.spring.jpatest.entities.*;
import ch.bfh.eglim8.spring.jpatest.repo.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RelationshipTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private EntityManager em;

    @Test
    void createEmployeeWithRelationships() {
        Employee bigBoss = new Employee();
        bigBoss.setName("Big Boss");
        bigBoss.setSalary(120000L);

        // Immer den Rückgabewert von save() weiterverwenden!
        bigBoss = employeeRepository.saveAndFlush(bigBoss);


        Project project = new Project();
        project.setName("Bookstore");
        project = projectRepository.saveAndFlush(project);

        Department department = new Department();
        department.setName("IT");
        department = departmentRepository.saveAndFlush(department);

        Address address = new Address();
        address.setStreet("Bahnhofstrasse 42");
        address.setZip("8000");
        address.setCity("Zürich");
        address.setState("ZH");
        address = addressRepository.saveAndFlush(address);

        Employee peterMuster = new Employee();
        peterMuster.setName("Peter Muster");
        peterMuster.setSalary(80000L);

        peterMuster.setAddress(address);

        peterMuster.setBoss(bigBoss);
        // Rückbeziehung auch setzen beim Erzeugen
        bigBoss.addEmployee(peterMuster);
        department.addEmployee(peterMuster);

        // Beide Seiten der Beziehung abfüllen
        peterMuster.getProjects().add(project);
        project.getEmployees().add(peterMuster);

        // Immer den Rückgabewert von save() weiterverwenden!
        peterMuster = employeeRepository.saveAndFlush(peterMuster);

        Phone phone = new Phone();
        phone.setType(PhoneType.MOBILE);
        phone.setPhonenumber("079 123 45 67");
        phone.setEmployee(peterMuster);

        // Convenience Methode verwenden die auch die Rückbeziehung setzt
        peterMuster.addPhone(phone);

        phoneRepository.saveAndFlush(phone);

        // Immer den Rückgabewert von save() weiterverwenden!
        peterMuster = employeeRepository.saveAndFlush(peterMuster);

        em.clear();

        Employee peterMusterFromDb = em.find(Employee.class, peterMuster.getId());

        assertThat(peterMusterFromDb.getId()).isNotNull();
    }
}
