package com.mycompany.newscrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
                //CrawlController controller = new CrawlController("./crawl");

                String crawlStorageFolder = "./crawl_info";
                int numberOfCrawlers = 7;

                CrawlConfig config = new CrawlConfig();
                config.setCrawlStorageFolder(crawlStorageFolder);
				config.setPolitenessDelay(600);

                /*
                 * Instantiate the controller for this crawl.
                 */
                PageFetcher pageFetcher = new PageFetcher(config);
                RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
                RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
                CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

                /*
                 * For each crawl, you need to add some seed urls. These are the first
                 * URLs that are fetched and then the crawler starts following links
                 * which are found in these pages
                 */

				addFocusedNewsToController(controller);
				
                /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
                 */
                controller.start(NewsCrawler.class, numberOfCrawlers);  
    }
	
	public static void addFocusedNewsToController(CrawlController controller) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("nasdaq.csv")));

		String line;
		
		while((line = br.readLine()) != null)
		{
			String splits[] = line.split(";");
			if(splits.length > 0)
			{
				String symbol = splits[0].replaceAll("\"", "");
				System.out.println("http://www.google.com/finance?q=" + symbol);
				controller.addSeed("http://www.google.com/finance?q=" + symbol);
			}
		}
		
	}
}
