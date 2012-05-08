/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.newscrawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author gtri
 */
public class NewsCrawler extends WebCrawler
{
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
													  + "|png|tiff?|mid|mp2|mp3|mp4"
													  + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
													  + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	/**
	 * You should implement this function to specify whether
	 * the given url should be crawled or not (based on your
	 * crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
			String href = url.getURL().toLowerCase();
			
			if(href.contains("http://www.google.com/finance"))
			{
				//System.out.println("should visit url: " + href);
				return true;
			}
		return false;
	}


	@Override
	public void visit(Page page) {          
			String url = page.getWebURL().getURL();
			System.out.println("URL: " + url);

			if (page.getParseData() instanceof HtmlParseData) {
					HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
					String text = htmlParseData.getText();
					String html = htmlParseData.getHtml();
					List<WebURL> links = htmlParseData.getOutgoingUrls();

					for(WebURL weblink : links)
					{
						System.out.println("Grabbing " + weblink.getURL());
						try
						{
							// only save a link if it won't be crawled (i.e. must be a news story)
							if(shouldVisit(weblink) == false)
							{
								String filename = "crawl_dir" + File.separator + computeSum(weblink.getURL())+".scrape";
								File f = new File(filename);
								if(f.exists() == false)
								{
									BufferedWriter br = new BufferedWriter(new FileWriter(filename));
									String str = readURL(weblink.getURL());
									br.write(str);
									br.close();
								}
							}
						}
						catch(Exception e){}
					}					
			}
	}
	
	public String readURL(String surl) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		URL url = new URL(surl);
		URLConnection yc = url.openConnection();
		BufferedReader in = new BufferedReader(
								new InputStreamReader(
								yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
		{
			sb.append(inputLine);
			sb.append("\n");
		}
		in.close();
		return sb.toString();
	}
	
	public static final String computeSum(String input)
		throws NoSuchAlgorithmException {

		if (input == null) {
		   throw new IllegalArgumentException("Input cannot be null!");
		}

		StringBuffer sbuf = new StringBuffer();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte [] raw = md.digest(input.getBytes());
		
		for (int i = 0; i < raw.length; i++) {
			int c = (int) raw[i];
			if (c < 0) {
				c = (Math.abs(c) - 1) ^ 255;
			}
			String block = toHex(c >>> 4) + toHex(c & 15);
			sbuf.append(block);
		}
		
		return sbuf.toString();
		
	}

	private static final String toHex(int s) {
		if (s < 10) {
		   return new StringBuffer().
                                append((char)('0' + s)).
                                toString();
		} else {
		   return new StringBuffer().
                                append((char)('A' + (s - 10))).
                                toString();
		}
	}	
}
