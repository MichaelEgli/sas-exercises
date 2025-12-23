package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import ch.bfh.sad.persistence.hr.dto.DepartmentSalaryStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CriteriaTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EntityManager em;

    /**
     * Ex1	Find all employees who live in the canton of Zurich
     */
    @Test
    void findAllZuercher() {
        List<Employee> zuercher = employeeRepository.findAll((employee, cq, cb) -> {
            Join<Employee, Address> address = employee.join(Employee_.address);
            return cb.equal(address.get(Address_.state), "ZH");
        });

        assertEquals(3, zuercher.size());
    }

    /**
     * Ex2	Calculate the average salary of employees per department
     */
    @Test
    void getAverageSalaryPerDepartment() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DepartmentSalaryStatistics> cq = cb.createQuery(DepartmentSalaryStatistics.class);

        var department = cq.from(Department.class);
        var employee = department.join(Department_.employees);

        var avg = cb.avg(employee.get(Employee_.salary));
        cq.groupBy(department.get(Department_.name));

        cq.select(
                cb.construct(DepartmentSalaryStatistics.class,
                        department.get(Department_.name), avg));

        TypedQuery<DepartmentSalaryStatistics> query = em.createQuery(cq);

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
     * Ex3	Find the employee with the lowest salary
     */
    @Test
    void findEmployeeWithSmallestSalary() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

        Root<Employee> employee = criteriaQuery.from(Employee.class);

        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<Employee> employeeSub = subquery.from(Employee.class);

        subquery.select(criteriaBuilder.min(employeeSub.get(Employee_.salary)));

        criteriaQuery.where(criteriaBuilder.equal(employee.get(Employee_.salary), subquery));
        criteriaQuery.select(employee);

        TypedQuery<Employee> query = em.createQuery(criteriaQuery);
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
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery();

        Root<Employee> employee = criteriaQuery.from(Employee.class);

        criteriaQuery.multiselect(employee.get(Employee_.name), employee.get(Employee_.address));

        Query query = em.createQuery(criteriaQuery);
        List<Object[]> list = query.getResultList();

        assertEquals(6, list.size());
    }

    /**
     * Ex5  Find employees who are not assigned to a project
     */
    @Test
    void findAllEmployeesWithoutProject() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

        Root<Employee> employee = criteriaQuery.from(Employee.class);

        criteriaQuery.where(criteriaBuilder.isEmpty(employee.get(Employee_.projects)));
        criteriaQuery.select(employee);

        TypedQuery<Employee> query = em.createQuery(criteriaQuery);
        List<Employee> list = query.getResultList();

        assertEquals(3, list.size());
    }

    /**
     * Ex6	Find all business phone numbers ordered by number
     */
    @Test
    void findAllWorkPhonesOrderedByNumber() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);

        Root<Phone> phone = criteriaQuery.from(Phone.class);

        criteriaQuery.where(criteriaBuilder.equal(phone.get(Phone_.type),
                criteriaBuilder.parameter(PhoneType.class, "type")));

        criteriaQuery.select(phone.get(Phone_.phoneNumber));

        TypedQuery<String> query = em.createQuery(criteriaQuery);
        query.setParameter("type", PhoneType.WORK);
        List<String> list = query.getResultList();

        assertEquals(5, list.size());
    }


    /**
     * Ex 7 Find employees who do not have a business phone number yet
     */
    @Test
    void findAllEmployeesWithoutWorkPhone2() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

        Root<Employee> employee = criteriaQuery.from(Employee.class);

        Subquery<Phone> subquery = criteriaQuery.subquery(Phone.class);
        Root<Phone> phoneSub = subquery.from(Phone.class);
        Join<Phone, Employee> employeeSub = phoneSub.join(Phone_.employee);

        subquery.where(criteriaBuilder.equal(
                phoneSub.get(Phone_.type),
                criteriaBuilder.parameter(PhoneType.class, "type")),
                criteriaBuilder.equal(employeeSub.get(Employee_.id), employee.get(Employee_.id)));

        subquery.select(phoneSub);

        criteriaQuery.where(
                criteriaBuilder.not(criteriaBuilder.exists(subquery)));

        criteriaQuery.select(employee);

        TypedQuery<Employee> query = em.createQuery(criteriaQuery);
        query.setParameter("type", PhoneType.WORK);
        List<Employee> list = query.getResultList();

        assertEquals(1, list.size());
    }

    @Test
    void leftJoinFetch() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

        Root<Employee> root = criteriaQuery.from(Employee.class);
        root.fetch(Employee_.projects, JoinType.LEFT);

        TypedQuery<Employee> query = em.createQuery(criteriaQuery);
        List<Employee> list = query.getResultList();

        assertEquals(6, list.size());
    }

}
