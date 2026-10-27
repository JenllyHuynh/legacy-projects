package fu.edu.sba301.ch18.app;

import fu.edu.sba301.ch18.dao.StudentDAO;
import fu.edu.sba301.ch18.entity.Student;
import fu.edu.sba301.ch18.exception.DuplicateEmailException;
import fu.edu.sba301.ch18.exception.StudentNotFoundException;
import fu.edu.sba301.ch18.repository.IStudentRepository;
import fu.edu.sba301.ch18.repository.StudentRepository;
import fu.edu.sba301.ch18.service.IStudentService;
import fu.edu.sba301.ch18.service.StudentService;
import fu.edu.sba301.ch18.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "fu.edu.sba301.ch18")
public class Main {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext ignored = SpringApplication.run(Main.class, args)) {
            // ApplicationRunner executes before run() returns; closing here closes SessionFactory once.
        }
    }

    @Bean(destroyMethod = "close")
    SessionFactory sessionFactory(Environment environment) {
        String defaultUrl = "jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;"
                + "encrypt=true;trustServerCertificate=true";
        Map<String, String> overrides = Map.of(
                "hibernate.connection.url", environment.getProperty("DB_URL", defaultUrl),
                "hibernate.connection.username", required(environment, "DB_USER"),
                "hibernate.connection.password", required(environment, "DB_PASSWORD")
        );
        return HibernateUtil.initialize(overrides);
    }

    @Bean
    StudentDAO studentDAO(SessionFactory sessionFactory) {
        return new StudentDAO(sessionFactory);
    }

    @Bean
    IStudentRepository studentRepository(StudentDAO dao) {
        return new StudentRepository(dao);
    }

    @Bean
    IStudentService studentService(IStudentRepository repository) {
        return new StudentService(repository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
    ApplicationRunner crudDemo(IStudentService service,
                               @Value("${app.demo.email-prefix:slot30}") String emailPrefix) {
        return arguments -> {
            String token = Long.toString(Instant.now().toEpochMilli());
            Student created = service.create(new Student(
                    emailPrefix + "+" + token + "@fu.edu.vn", "Slot", "Thirty", 8.5));
            System.out.println("CREATE  -> " + created);
            System.out.println("READ    -> " + service.getById(created.getId()));
            System.out.println("SEARCH  -> " + service.getByEmail(created.getEmail()));
            System.out.println("LIST    -> " + service.getAll());
            System.out.println("PAGE    -> " + service.getPage(0, 5));

            Student managedPathResult = service.updateMarks(created.getId(), 9.0);
            System.out.println("DIRTY   -> " + managedPathResult);
            managedPathResult.setFirstName("Updated");
            System.out.println("MERGE   -> " + service.update(managedPathResult));

            demonstrateNegativeCases(service, token);
            service.delete(created.getId());
            System.out.println("DELETE  -> id=" + created.getId());
        };
    }

    private static void demonstrateNegativeCases(IStudentService service, String token) {
        String rollbackEmail = "rollback+" + token + "@fu.edu.vn";
        try {
            service.createBatchAtomically(List.of(
                    new Student(rollbackEmail, "A", "One", 8),
                    new Student(rollbackEmail, "B", "Two", 9)));
        } catch (DuplicateEmailException exception) {
            System.out.println("ROLLBACK -> " + exception.getMessage());
        }
        try {
            service.getById(999_999L);
        } catch (StudentNotFoundException exception) {
            System.out.println("NOTFOUND -> " + exception.getMessage());
        }
    }

    private static String required(Environment environment, String key) {
        String value = environment.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(key + " is required; copy .env.example and set it in your shell");
        }
        return value;
    }
}
