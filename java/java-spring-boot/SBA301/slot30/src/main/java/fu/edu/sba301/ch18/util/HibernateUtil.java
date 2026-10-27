package fu.edu.sba301.ch18.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Map;
import java.util.Objects;

public final class HibernateUtil {
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory initialize(Map<String, String> runtimeConfiguration) {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                    runtimeConfiguration.forEach(configuration::setProperty);
                    sessionFactory = configuration.buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return Objects.requireNonNull(sessionFactory,
                "SessionFactory has not been initialized by the composition root");
    }

    public static void shutdown() {
        SessionFactory current = sessionFactory;
        if (current != null && !current.isClosed()) {
            current.close();
        }
    }
}
