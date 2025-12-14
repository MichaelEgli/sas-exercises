package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.dto.EmployeeDTO;
import ch.bfh.sad.persistence.hr.dto.EmployeeNameWithAddress;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByAddressState(String state);

    @Query("select e from Employee e")
    List<EmployeeNameWithAddress> findNameWithAddress();

    @Query("select e from Employee e join fetch e.address a")
    List<EmployeeNameWithAddress> findNameWithAddressJoinFetch();

    @EntityGraph(attributePaths = {"phones"})
    List<Employee> findDistinctByNameLike(String name);

    @EntityGraph(value = Employee.INCLUDE_PHONES)
    List<Employee> findAll();

    @EntityGraph(value = Employee.INCLUDE_PHONES)
    List<Employee> findAllByAddressState(String state);

    @Query("select e from Employee e")
    List<EmployeeDTO> findAllDtos();

    @Query(value = "select * from employee", nativeQuery = true)
    List<Employee> findAllNative();

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select e from Employee e where e.id = :id")
    Optional<Employee> selectForUpdate(Integer id);
}
