/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import java.io.File;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author gtri
 */
public class HibernateUtilSQLite {

	private static SessionFactory sessionFactory;
	private static final AnnotationConfiguration m_cfg;
	private static final int BATCH_SIZE = 20;
	static 
	{
		try 
		{
			boolean createSchema = false;

			// check if the file (were using SQLite exist), if it doesn,t
			// set the flag to create the schema below (after we know what objects we need)
			if(new File("technicalanalysis.db").isFile() == false)
			{
				createSchema = true;
			}

			m_cfg = new AnnotationConfiguration();
			m_cfg.setProperty("hibernate.show_sql", "false");
			m_cfg.setProperty("hibernate.format_sql", "false");

			m_cfg.setProperty("hibernate.dialect", hibernate.SQLiteDialect.class.getName());
			m_cfg.setProperty("hibernate.connection.driver_class", org.sqlite.JDBC.class.getName());
			m_cfg.setProperty("hibernate.connection.url", "jdbc:sqlite:technicalanalysis.db");
			m_cfg.setProperty("hibernate.current_session_context_class", "thread");
			m_cfg.setProperty("hibernate.jdbc.batch_size", "" + BATCH_SIZE);					
			// Create the SessionFactory from standard (hibernate.cfg.xml) 
			// config file.
			
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
	/*
	public static Session getNewSession()
	{
		return sessionFactory.openSession();
	}
	
	public static Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}*/

	public static Session getSession()
	{
		return sessionFactory.getCurrentSession();
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
