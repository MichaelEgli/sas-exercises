package ch.bfh.eglim8.spring.jpatest.repo;

import ch.bfh.eglim8.spring.jpatest.entities.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {
}
