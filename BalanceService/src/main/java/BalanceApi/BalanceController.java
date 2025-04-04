package BalanceApi;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.SessionFactory;
/**
 *
 * @author omar
 */
@Path("/balance")
public class BalanceController {
    private SessionFactory sessionFactory;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Balance getBalanceByMsisdn(@QueryParam("msisdn") String msisdn){
        Balance userBalance;
        sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            userBalance = session.createSelectionQuery("from Balance where msisdn = :msisdn", Balance.class)
                               .setParameter("msisdn", msisdn).uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new HibernateException("Error getting balance", e);
        }
        return userBalance;
    }
}