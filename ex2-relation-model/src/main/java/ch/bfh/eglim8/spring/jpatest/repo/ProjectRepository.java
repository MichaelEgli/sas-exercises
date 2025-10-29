package ch.bfh.eglim8.spring.jpatest.repo;

import ch.bfh.eglim8.spring.jpatest.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
