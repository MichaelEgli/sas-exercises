package ch.bfh.sad.persistence.hr.domain;

import ch.bfh.sad.persistence.hr.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PessimisticLockIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(PessimisticLockIT.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void lostUpdate() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("Peter Muster");
        employee.setSalary(60_000L);
        employeeRepository.saveAndFlush(employee);

        Updater u1 = new Updater(employee.getId(), 5000);
        Updater u2 = new Updater(employee.getId(), 0);

        u1.start();
        u2.start();

        u1.join();
        u2.join();
    }

    class Updater extends Thread {

        private final Integer id;
        private final int sleepTime;

        public Updater(Integer id, int sleepTime) {
            this.id = id;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());

                employeeRepository.selectForUpdate(id).ifPresent(employee -> {
                    ;
                    employee.setName("Max Muster");

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                    }

                    employeeRepository.saveAndFlush(employee);
                });

                transactionManager.commit(ts);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }
}
