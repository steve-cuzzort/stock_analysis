/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import hibernate.HibernateUtil;
import hibernate.entities.Stock;
import java.util.ArrayList;
import java.util.List;
import technicalanalysis.TAQuery;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import technicalanalysis.TAOutput;
import weka.WekaComponent;
import weka.core.Instances;
/**
 *
 * @author gtri
 */
public class MainCalcIndicators 
{
	// SELECT * FROM stock JOIN stock_stockstats ON(stock.id = stock_stockstats.Stock_id) JOIN stockstats ON(stock_stockstats.stats_id = stockstats.id) WHERE 1
	public static void main(String args[]) throws Exception
	{
		Logger rootLogger = Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements()) 
		{
			rootLogger.setLevel(Level.INFO);
			try
			{
				rootLogger.addAppender(new FileAppender(
					 new PatternLayout("%-5p [%t]: %m%n"), "stocks.log"));
			}
			catch(Exception e){}
			// The TTCC_CONVERSION_PATTERN contains more info than
			// the pattern we used for the root logger
			Logger pkgLogger = rootLogger.getLoggerRepository().getLogger("robertmaldon.moneymachine");
			pkgLogger.setLevel(Level.DEBUG);
			pkgLogger.addAppender(new ConsoleAppender(
				 new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		}

		List<String> stocknames = new ArrayList<String>();
		
		HibernateUtil.startNewSession();
		for(Stock s : Stock.getAllStocks())
		{
			stocknames.add(s.getSymbol());
		}
		HibernateUtil.closeSession();
		
		for(String stockname : stocknames)
		{
			HibernateUtil.startNewSession();
			Stock stock = Stock.findStock(stockname);
			
			TAQuery query = new TAQuery(stock);

			ArrayList<TAOutput> taouts = new ArrayList<TAOutput>();
			for(String group : new String[] {"Cycle Indicators", "Momentum Indicators", "Overlap Studies", "Pattern Recognition", "Price Transform", "Statistic Functions", "Volatility Indicators", "Volume Indicators"})
			{
				for(String func : query.getFunctionsInGroup(group))
				{	
					TAOutput outs[] = query.runInidcator(func);

					for(TAOutput o : outs)
					{
						if(o.hasData())
						{
							taouts.add(o);
						}
					}
				}
			}
			Instances data = WekaComponent.CreateWekaInstances(stock, taouts, 3, .03);
			WekaComponent.makeModel(stock, data);
			//WekaComponent.CreateFileFromStockAndOutput("./arff/"+stock.getSymbol()+".arff", stock, taouts, 3, .03);
			rootLogger.info("Wrote " + stock.getSymbol() + " to file");
			HibernateUtil.closeSession();
		}
	}
}
