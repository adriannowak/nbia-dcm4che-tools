package org.dcm4chex.wado.common;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
 
public class HibernateUtil {
 
	private static final SessionFactory sessionFactory = buildSessionFactory();
 
	private static SessionFactory buildSessionFactory() {
		try {
			Configuration config = new Configuration().configure(
					"hibernate.cfg.xml");
			
			Configuration config2 = new Configuration().configure();
			
			// load from different directory
			SessionFactory sessionFactory = new Configuration().configure(
					"hibernate.cfg.xml")
					.buildSessionFactory();
 
			return sessionFactory;
 
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
//			log.error("Initial SessionFactory creation failed." + ex);
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}
 
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
 
	public static void close() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
 
}