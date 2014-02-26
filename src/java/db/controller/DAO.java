package db.controller;

import db.pojos.Cuenta;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

/**
 * Se encarga de entablar la comunicacion con la base de datos
 *
 * @author zorin
 */
public class DAO {

    /**
     * conexion con la base de datos
     */
    private static Session session;

    /**
     * ejecuta un query pasandole la clase sobre la cual se hara el query con
     * los criterios mencionados
     *
     * @param clase
     * @param criterios
     * @return
     */
    public static List createQuery(Class clase, Criterion[] criterios) {
        checkSession();
        Criteria createCriteria = session.createCriteria(clase);
        if (criterios != null) {
            for (Criterion cr : criterios) {
                createCriteria.add(cr);
            }
        }
        return createCriteria.list();
    }

    /**
     * verifica la sesion y la abre si es necesario
     */
    private static void checkSession() {
        if (session == null || !session.isOpen()) {
            session = HibernateUtil.getSessionFactory().openSession();
        }   
        session.reconnect();
    }

    
    public static void save(Object object) {
        session.save(object);
        session.flush();
        session.update(object);
    }
}
