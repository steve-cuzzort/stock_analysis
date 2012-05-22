/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author gtri
 */
public class HibernateUtil 
{
	private static SessionFactory sessionFactory;
	private static final AnnotationConfiguration m_cfg;
	private static final int BATCH_SIZE = 20;
	
	private static Session m_session;
	private static Transaction m_transaction;
	
	static 
	{
		try 
		{
			boolean createSchema = false;

			m_cfg = new AnnotationConfiguration();
			m_cfg.setProperty("hibernate.show_sql", "false");
			m_cfg.setProperty("hibernate.format_sql", "false");

			m_cfg.setProperty("hibernate.dialect", org.hibernate.dialect.PostgreSQLDialect.class.getName());
			m_cfg.setProperty("hibernate.connection.driver_class", org.postgresql.Driver.class.getName());
			m_cfg.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost/stockdb");
			m_cfg.setProperty("hibernate.connection.username", "postgres");
			m_cfg.setProperty("hibernate.connection.password", "gtri2011");
			m_cfg.setProperty("hibernate.current_session_context_class", "thread");
			m_cfg.setProperty("hibernate.jdbc.batch_size", "" + BATCH_SIZE);
                        m_cfg.setProperty("hibernate.hbm2ddl.auto", "update");
			// Create the SessionFactory from standard (hibernate.cfg.xml) 
			// config file.
			
			//SQLite properityes
			/*
			m_cfg.setProperty("hibernate.dialect", hibernate.SQLiteDialect.class.getName());
			m_cfg.setProperty("hibernate.connection.driver_class", org.sqlite.JDBC.class.getName());
			m_cfg.setProperty("hibernate.connection.url", "jdbc:sqlite:technicalanalysis.db");
			m_cfg.setProperty("hibernate.current_session_context_class", "thread");
			m_cfg.setProperty("hibernate.jdbc.batch_size", "" + BATCH_SIZE);				 
			 */
			
			m_cfg.addAnnotatedClass(hibernate.entities.StockStats.class);
			m_cfg.addAnnotatedClass(hibernate.entities.StockEntry.class);
			m_cfg.addAnnotatedClass(hibernate.entities.Stock.class);
			
			if(createSchema)
				generateSchema();
			
			sessionFactory = m_cfg.buildSessionFactory();
		} 
		catch (Throwable ex) 
		{
			// Log the exception. 
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Session startNewSession()
	{
		if(m_session != null)
		{
			m_session.close();
			m_session = null;
		}
		
		m_session = sessionFactory.openSession();
		return m_session;
	}
	
	public static Session getCurrentSession()
	{
		if(m_session != null)
		{
			return m_session;
		}		
		else
		{
			return startNewSession();
		}
	}	
	
	public static void beginTransaction()
	{
		if(m_transaction != null)
		{
			m_transaction.rollback();
		}
		Session session = getCurrentSession();
		m_transaction = session.beginTransaction();
	}
	
	public static void rollBack()
	{
		if(m_transaction != null)
		{
			m_transaction.rollback();
		}
		m_transaction = null;
	}
	
	public static void commit()
	{
		if(m_transaction != null)
		{
			m_transaction.commit();
		}
		m_transaction = null;		
	}
		
	public static void closeSession()
	{
		m_session.flush();
		m_session.clear();
		m_session.close();
		m_session = null;
	}
		
	public static void generateSchema()
	{
		// make sure the session factory is not live while we regenerate the schema
		// regenerating the schema with a session factory open (using the old schema)
		// is like crossing the beams in ghost busters
		if(sessionFactory != null)
		{
			sessionFactory.close();
			new org.hibernate.tool.hbm2ddl.SchemaExport(m_cfg).create(false, true);
			sessionFactory = m_cfg.buildSessionFactory();
		}
		else
		{
			new org.hibernate.tool.hbm2ddl.SchemaExport(m_cfg).create(false, true);
		}
	}		
}
