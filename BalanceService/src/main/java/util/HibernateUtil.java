package util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
// import twilioWebApp.model.User;
// import twilioWebApp.model.VerificationCode;
// import twilioWebApp.model.OutboundMsg;
// import twilioWebApp.model.InboundMsg;


public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure();
            // configuration.addAnnotatedClass(User.class);
            // configuration.addAnnotatedClass(VerificationCode.class);
            // configuration.addAnnotatedClass(OutboundMsg.class);
            // configuration.addAnnotatedClass(InboundMsg.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException e) { 
            System.out.println("Error creating session factory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }   

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
