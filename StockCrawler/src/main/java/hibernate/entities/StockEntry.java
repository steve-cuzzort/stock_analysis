/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    @Column(name="tags",  columnDefinition="TEXT")
    private String tags;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    
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
       
    public String getInfoByTag(String tagname)
    {        
        HashMap<String, String> map = getHashTableFromTags();
        return map.get(tagname);
    }
    
    public void setInfoByTag(String tag, String val)
    {
        HashMap<String, String> map = getHashTableFromTags();
        map.put(tag, val);
        setHashTableFromTags(map);
    }
    
    private HashMap<String, String> getHashTableFromTags()
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(tags.getBytes());
            ObjectInputStream ostream = new ObjectInputStream(bais);
            HashMap<String, String> map = (HashMap<String, String>)ostream.readObject();
            return map;
        }
        catch(Exception e)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            setHashTableFromTags(map);
            return map;
        }
    }
    
    private void setHashTableFromTags(HashMap<String, String> map)
    {
        try
        {           
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream ostream = new ObjectOutputStream(baos);
            ostream.writeObject(map);
            ostream.flush();
            baos.close();
            
            setTags(javax.xml.bind.DatatypeConverter.printBase64Binary(baos.toByteArray()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
