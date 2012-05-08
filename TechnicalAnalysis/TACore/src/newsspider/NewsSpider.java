/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newsspider;

import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 *
 * @author gtri
 */
public class NewsSpider {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) 
	{
		try
		{
			CrawlController controller = new CrawlController("~/TechnicalAnalysis/crawldata/");
                controller.addSeed("http://news.google.com");
				controller.addSeed("http://www.google.com/finance");
				controller.addSeed("http://news.yahoo.com");
				
                controller.start(NewsCrawler.class, 10);  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
