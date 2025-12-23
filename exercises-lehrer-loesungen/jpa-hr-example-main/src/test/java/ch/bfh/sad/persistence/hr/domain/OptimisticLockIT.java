package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class OptimisticLockIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockIT.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void lostUpdate() throws InterruptedException {
        Department department = new Department();
        department.setName("FC");
        department = departmentRepository.saveAndFlush(department);

        Updater u1 = new Updater(department.getId(), 1000);
        Updater u2 = new Updater(department.getId(), 0);

        u1.start();
        u2.start();

        u1.join();
        u2.join();
    }

    class Updater extends Thread {

        private final Integer id;
        private final int sleepTime;

        Updater(Integer id, int sleepTime) {
            this.id = id;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                Optional<Department> optionalDepartment = departmentRepository.findById(id);
                Department department = optionalDepartment.get();
                department.setName("Test");
                departmentRepository.saveAndFlush(department);

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }
}
