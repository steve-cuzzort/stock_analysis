/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate.entities;

import hibernate.HibernateUtil;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author gtri
 */
@Entity
public class StockEntry 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="open")
	private Double open;
	@Column(name="close")
	private Double close;
	@Column(name="high")
	private Double high;
	@Column(name="low")
	private Double low;
	@Column(name="volume")
	private Integer volume;
	@Column(name="trade_date")
	private java.sql.Date date;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setDate(org.joda.time.DateTime date) {
		this.date = new java.sql.Date(date.getMillis());
	}

	public org.joda.time.DateTime getJodaDate()
	{
		org.joda.time.DateTime dt =  new org.joda.time.DateTime(date.getTime());
		return dt;
	}
	
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}

	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}

	public Integer getVolume() {
		return volume;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

}
