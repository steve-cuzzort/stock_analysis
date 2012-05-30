/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate.entities;

import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author gtri
 */
@Entity
public class Stock 
{
	private transient static final org.joda.time.DateTime START_DATE = null;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="symbol")
	private String symbol;	
	@OneToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE})
	private List<StockEntry> entries = new ArrayList<StockEntry>();
	@OneToMany(fetch=FetchType.LAZY)
			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	//@Column(name="symbol")
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	//@OneToMany
	//@JoinColumn(name="stock_id")
	public List<StockEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<StockEntry> entries) {
		this.entries = entries;	
	}

	public org.joda.time.DateTime getMostRecentEntry()
	{
		if(entries.size() == 0)
		{
			return START_DATE;
		}
		
		org.joda.time.DateTime retdate = entries.get(0).getJodaDate();
		
		for(StockEntry entry : entries)
		{
			if(entry.getJodaDate().isAfter(retdate))
			{
				retdate = entry.getJodaDate();
			}
		}
		
		return retdate;
	}

	public org.joda.time.DateTime getOldestEntry()
	{
		if(entries == null || entries.size() == 0)
		{
			return START_DATE;
		}
		
		org.joda.time.DateTime retdate = entries.get(0).getJodaDate();
		
		for(StockEntry entry : entries)
		{
			if(entry.getJodaDate().isBefore(retdate))
			{
				retdate = entry.getJodaDate();
			}
		}
		
		return retdate;
	}
        
        public StockEntry getEntryForDay(org.joda.time.DateTime date)
        {
		java.sql.Date finddate = new java.sql.Date(date.year().get(), date.monthOfYear().get(), date.dayOfMonth().get());//year, month, day);
		
		for(StockEntry entry : entries)
		{
			if(entry.getDate().equals(finddate))
			{
				return entry;
			}
		}		
		
		return null;
        }
	
	public boolean hasDataForDay(org.joda.time.DateTime date)//int year, int month, int day)
	{
		java.sql.Date finddate = new java.sql.Date(date.year().get(), date.monthOfYear().get(), date.dayOfMonth().get());//year, month, day);
		
		for(StockEntry entry : entries)
		{
			if(entry.getDate().equals(finddate))
			{
				return true;
			}
		}		
		
		return false;
	}
	
	public boolean isInDB()
	{
		if(id.intValue() < 1)
			return false;
		return true;
	}
	
	public String toString()
	{
		if(getMostRecentEntry() == null)
		{
			return symbol + "\tHas no data";
		}
		else
		{
			return symbol + "\tNewest Entry:" + getMostRecentEntry().toString() + "\t Oldest Entry:" + getOldestEntry().toString();
		}
	}
	
	public static Stock findStock(String symbol)
	{
		Session session = HibernateUtil.getCurrentSession();		
		Query query = session.createQuery("FROM " + Stock.class.getName() + " WHERE symbol = :symbol");			
		query.setString("symbol", symbol);
		List<Stock> list = query.list();
		if(list == null || list.size() == 0)
		{
			return null;
		}
		return  (Stock)list.get(0);
	}
	
	public static boolean isInDB(String symbol)
	{
		if(findStock(symbol) == null)
			return false;
		else 
			return true;
	}
		
	public static List<Stock> getAllStocks()
	{
		ArrayList<Stock> ret = new ArrayList<Stock>();
		Query query = HibernateUtil.getCurrentSession().createQuery("FROM " + Stock.class.getName());			

		Iterator it = query.iterate();
		while(it.hasNext())
		{
			Stock s = (Stock)it.next();
			ret.add(s);
		}
		return ret;
	}
}
