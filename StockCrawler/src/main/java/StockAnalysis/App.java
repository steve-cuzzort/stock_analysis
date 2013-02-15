package StockAnalysis;

import hibernate.HibernateUtil;
import yahooscraper.LoadFromYahoo;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.hibernate.Session;

/**
 *
 * @author gtri
 */
public class App 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Logger rootLogger = Logger.getRootLogger();

        LoadFromYahoo.loadInAllStocksFromNASDAQFile();
        LoadFromYahoo.updateAllStocksFromYahoo();
    }
}