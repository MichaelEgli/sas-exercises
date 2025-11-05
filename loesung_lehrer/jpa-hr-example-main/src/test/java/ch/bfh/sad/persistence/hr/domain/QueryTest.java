package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import ch.bfh.sad.persistence.hr.dto.DepartmentSalaryStatistics;
import ch.bfh.sad.persistence.hr.dto.EmployeeDTO;
import ch.bfh.sad.persistence.hr.dto.EmployeeNameWithAddress;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QueryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Ex1	Find all employees who live in the canton of Zurich
     */
    @Test
    void findAllZuercher() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where e.address.state = 'ZH'", Employee.class);
        List<Employee> zuercher = query.getResultList();

        assertEquals(3, zuercher.size());
    }

    /**
     * Ex1	Find all employees who live in the canton of Zurich
     */
    @Test
    void findAllByAddressState() {
        List<Employee> zuercher = employeeRepository.findByAddressState("ZH");

        assertEquals(3, zuercher.size());
    }

    /**
     * Ex2	Calculate the average salary of employees per department
     */
    @Test
    void getAverageSalaryPerDepartment() {
        TypedQuery<DepartmentSalaryStatistics> query = em.createNamedQuery(Department.AVG_SALARY, DepartmentSalaryStatistics.class);
        List<DepartmentSalaryStatistics> list = query.getResultList();

        assertEquals(2, list.size());

        for (DepartmentSalaryStatistics statistics : list) {
            if (statistics.departmentName().equals("IT")) {
                assertEquals(97200.0, statistics.avgSalary(), 0);
            }
            if (statistics.departmentName().equals("HR")) {
                assertEquals(95000.0, statistics.avgSalary(), 0);
            }
        }
    }

    /**
     * Ex2	Calculate the average salary of employees per department
     */
    @Test
    void getAverageSalaryPerDepartmentAsNamedQueryWithSpringData() {
        List<DepartmentSalaryStatistics> list = departmentRepository.avgSalary();

        assertEquals(2, list.size());

        for (DepartmentSalaryStatistics statistics : list) {
            if (statistics.departmentName().equals("IT")) {
                assertEquals(97200.0, statistics.avgSalary(), 0);
            }
            if (statistics.departmentName().equals("HR")) {
                assertEquals(95000.0, statistics.avgSalary(), 0);
            }
        }
    }

    /**
     * Ex3	Find the employee with the lowest salary
     */
    @Test
    void findEmployeeWithLowestSalary() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where e.salary = (select min(e.salary) from Employee e)", Employee.class);
        List<Employee> list = query.getResultList();

        assertEquals(2, list.size());

        assertEquals("Luca Traugott", list.get(0).getName());
        assertEquals("Lea Schulze", list.get(1).getName());
    }

    /**
     * Ex3	Find the employee with the lowest salary
     */
    @Test
    void findEmployeeWithSmallestSalaryWithSql() {
        Query query = em.createNativeQuery(
                "select * from Employee e where e.salary = (select min(e.salary) from Employee e)", Employee.class);
        List<Employee> list = query.getResultList();

        assertEquals(2, list.size());

        assertEquals("Luca Traugott", list.get(0).getName());
        assertEquals("Lea Schulze", list.get(1).getName());
    }

    /**
     * Ex4 	Create a query that returns the employee name and the complete address, ordered by the employee’s name
     */
    @Test
    void findAllEmployeeNameWithAddress() {
        Query query = em.createQuery("select e.name, e.address from Employee e order by e.name");
        List list = query.getResultList();

        assertEquals(6, list.size());
    }

    /**
     * Ex4 	Create a query that returns the employee name and the complete address, ordered by the employee’s name
     */
    @Test
    void findEmployeeNameWithAddress() {
        List<EmployeeNameWithAddress> list = employeeRepository.findNameWithAddress();

        assertEquals(6, list.size());

        list.forEach(e ->
                assertAll(() -> {
                    assertNotNull(e.getName());
                    assertNotNull(e.getAddress().getCity());
                })
        );
    }

    @Test
    void findEmployeeNameWithAddressJoinFetch() {
        List<EmployeeNameWithAddress> list = employeeRepository.findNameWithAddressJoinFetch();

        assertEquals(6, list.size());

        list.forEach(e ->
                assertAll(() -> {
                    assertNotNull(e.getName());
                    assertNotNull(e.getAddress().getCity());
                })
        );
    }

    /**
     * Ex5  Find employees who are not assigned to a project
     */
    @Test
    void findAllEmployeesWithoutProject() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where e.projects is empty", Employee.class);
        List<Employee> list = query.getResultList();

        assertEquals(3, list.size());
    }

    /**
     * Ex6	Find all business phone numbers ordered by number
     */
    @Test
    void findAllWorkPhonesOrderedByNumber() {
        TypedQuery<String> query = em.createQuery(
                "select p.phoneNumber from Phone p where p.type = :type order by p.type", String.class);
        query.setParameter("type", PhoneType.WORK);
        List<String> list = query.getResultList();

        assertEquals(5, list.size());
    }


    /**
     * Ex 7 Find employees who do not have a business phone number yet
     */
    @Test
    void findAllEmployeesWithoutWorkPhone() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where e not in (select distinct e from Employee e join e.phones p where p.type = :type)", Employee.class);
        query.setParameter("type", PhoneType.WORK);
        List<Employee> list = query.getResultList();

        assertEquals(1, list.size());
    }

    /**
     * Ex 7 Find employees who do not have a business phone number yet
     */
    @Test
    void findAllEmployeesWithoutWorkPhone2() {
        TypedQuery<Employee> query = em.createQuery(
                "select e from Employee e where not exists (select p from Phone p where p.employee = e and p.type = :type)", Employee.class);
        query.setParameter("type", PhoneType.WORK);
        List<Employee> list = query.getResultList();

        assertEquals(1, list.size());
    }

    @Test
    void sqlInjection() {
        // Parameter from UI
        String name = "'x'";
        // SQL Injection
        name += " OR 1 = 1";

        String sqlString = "select e from Employee e where e.name = " + name;

        TypedQuery<Employee> query = em.createQuery(sqlString, Employee.class);

        List<Employee> list = query.getResultList();

        // Returns all instead of only one Employee!!!
        assertEquals(6, list.size());
    }

    @Test
    void preventSqlInjection() {
        // Parameter from UI
        String name = "x";
        // SQL Injection
        name += " OR 1 = 1";

        String sqlString = "select e from Employee e where e.name = :name";

        TypedQuery<Employee> query = em.createQuery(sqlString, Employee.class);
        query.setParameter("name", name);

        List<Employee> list = query.getResultList();

        // Returns all instead of only one Employee!!!
        assertEquals(0, list.size());
    }

    @Test
    void inheritanceOnBaseClass() {
        List<Project> list = em.createQuery("select p from Project p", Project.class).getResultList();

        assertEquals(2, list.size());

        assertInstanceOf(DesignProject.class, list.get(0));
        assertInstanceOf(QualityProject.class, list.get(1));
    }

    @Test
    void inheritanceOnSubClass() {
        List<DesignProject> list = em.createQuery("select p from DesignProject p", DesignProject.class).getResultList();

        assertEquals(1, list.size());

        assertInstanceOf(DesignProject.class, list.get(0));
    }

    @Test
    void employeeIncludePhonesJpql() {
        TypedQuery<Employee> query = em.createQuery("select distinct e from Employee e left outer join fetch e.phones p", Employee.class);
        List<Employee> list = query.getResultList();

        assertEquals(6, list.size());
    }

    @Test
    void employeeIncludePhonesQueryHintEntityManager() {
        EntityGraph<?> entityGraph = em.getEntityGraph(Employee.INCLUDE_PHONES);
        TypedQuery<Employee> query = em.createQuery("select distinct e from Employee e", Employee.class);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        List<Employee> list = query.getResultList();

        assertEquals(6, list.size());
    }

    @Test
    void employeeIncludePhonesInterfaceMethod() {
        List<Employee> list = employeeRepository.findAll();

        assertEquals(6, list.size());
    }

    @Test
    void employeeIncludePhonesByName() {
        List<Employee> list = employeeRepository.findDistinctByNameLike("%");

        assertEquals(6, list.size());
    }

    @Test
    void findAllDtos() {
        List<EmployeeDTO> list = employeeRepository.findAllDtos();

        System.out.println(list);

        assertEquals(6, list.size());
    }

    @Test
    void findAllNative() {
        List<Employee> list = employeeRepository.findAllNative();

        System.out.println(list);

        assertEquals(6, list.size());
    }


    @Test
    void selectForUpdate() {
        employeeRepository.selectForUpdate(1);
    }
}
