package db.controller;

import db.pojos.Calificadora;
import db.pojos.Cuenta;
import db.pojos.Regcuenta;
import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;
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
        List list = createCriteria.list();

        return list;
    }

    /**
     * verifica la sesion y la abre si es necesario
     */
    private static void checkSession() {
        if (session == null || !session.isOpen()) {
            session = HibernateUtil.getSessionFactory().openSession();
        }
        session.reconnect();
        session.setCacheMode(CacheMode.IGNORE);
    }

    public static void save(Object object) {
        session.save(object);
        session.flush();
        session.clear();
    }

    public static void saveOrUpdate(Object obj) {
        session.saveOrUpdate(obj);
        session.flush();
        session.clear();
    }

    public static void update(Object obj) {
        session.update(obj);
        session.flush();
        session.clear();
    }

    public static void main(String[] args) {
        List<Calificadora> createQuery = DAO.createQuery(Calificadora.class, null);
        System.out.println(createQuery.get(0).getDesCalificadora());
    }

    public static void delete(Object object) {
        session.delete(object);
        session.flush();
        session.clear();
    
    }
}
