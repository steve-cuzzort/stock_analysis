/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newsspider;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author gtri
 */
public class NewsCrawler extends WebCrawler
{
	        Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                + "|png|tiff?|mid|mp2|mp3|mp4"
                + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

			private static final String VISIT_BASE_URLS[] = {"http://news.google.com", 
															"http://www.google.com/finance", 
															"http://news.yahoo.com", 
															"http://www.yahoo.com"};
        /*
         * You should implement this function to specify
         * whether the given URL should be visited or not.
         */
        public boolean shouldVisit(WebURL url) {
                String href = url.getURL().toLowerCase();
                if (filters.matcher(href).matches()) {
                        return false;
                }
				for(String baseURL : VISIT_BASE_URLS)
				{
	                if (href.startsWith(baseURL)) 
					{
                        return true;
					}
				}
                return false;
        }

        /*
         * This function is called when a page is fetched
         * and ready to be processed by your program
         */
        public void visit(Page page) 
		{
                int docid = page.getWebURL().getDocid();
                String url = page.getWebURL().getURL();         
                String text = page.getText();
                List<WebURL> links = page.getURLs();
				
				for(WebURL link : links)
				{
					processPageLink(link);
				}
        }
		
		public void processPageLink(WebURL url)
		{
			
		}
}
