package ch.bfh.eglim8.spring.jpatest.repo;

import ch.bfh.eglim8.spring.jpatest.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
