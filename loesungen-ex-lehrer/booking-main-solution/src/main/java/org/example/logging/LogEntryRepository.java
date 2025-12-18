package org.example.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

	@Query("SELECT e FROM LogEntry e WHERE e.message LIKE CONCAT('%', :keyword, '%')")
	List<LogEntry> findByKeyword(String keyword);
}
