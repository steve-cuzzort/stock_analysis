/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate.entities;

import hibernate.HibernateUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.Session;
import weka.classifiers.Classifier;

/**
 *
 * @author gtri
 */
@Entity
public class StockStats 
{   
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="correct")
    private Double correct;	

    @Column(name="incorrect")
    private Double incorrect;

    @Column(name="look_ahead")
    private Double look_ahead;

    @Column(name="change")
    private Double change;

    @Column(name="summary", columnDefinition="TEXT")
    private String summary;		

    @Column(name="classifier_name")
    private String classifier_name;

    @Column(name="confusion_matrix", columnDefinition="TEXT")
    private String confusion_matrix;

    @Column(name="stock_sym")
    private String stock_sym;

    @Column(name="model", columnDefinition="TEXT")
    private String model;

    public static StockStats findStockStat(String symbol, int lookAhead, double change)
    {
        Session session = HibernateUtil.getCurrentSession();		
        org.hibernate.Query query = session.createQuery("FROM " + StockStats.class.getName() + " WHERE stock_sym = :symbol AND look_ahead = :lookahead AND change = :change");			
        query.setString("symbol", symbol);
        query.setInteger("lookahead", lookAhead);
        query.setFloat("change", (float)change);

        List<StockStats> list = query.list();
        if(list == null || list.size() == 0)
        {
                return null;
        }
        return  (StockStats)list.get(0);
    }
        
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
       
    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getLook_ahead() {
        return look_ahead;
    }

    public void setLook_ahead(Double look_ahead) {
        this.look_ahead = look_ahead;
    }



    public String getStock_sym() {
        return stock_sym;
    }

    public void setStock_sym(String stock_sym) {
        this.stock_sym = stock_sym;
    }

    public Double getCorrect() {
        return correct;
    }

    public void setCorrect(Double correct) {
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(Double incorrect) {
        this.incorrect = incorrect;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getClassifier_name() {
        return classifier_name;
    }

    public void setClassifier_name(String classifier_name) {
        this.classifier_name = classifier_name;
    }

    public String getConfusion_matrix() {
        return confusion_matrix;
    }

    public void setConfusion_matrix(String confusion_matrix) {
        this.confusion_matrix = confusion_matrix;
    }

    public void setConfusion_matrix(double[][] matrix)
    {
        StringBuilder sb = new StringBuilder("\ta\tb\tc\n");

        sb.append("a\t"+	matrix[0][0] + "\t" +	matrix[1][0] + "\t" +	matrix[2][0] + "\t\n");
        sb.append("b\t"+	matrix[0][1] + "\t" +	matrix[1][1] + "\t" +	matrix[2][1] + "\t\n");
        sb.append("c\t"+	matrix[0][2] + "\t" +	matrix[1][2] + "\t" +	matrix[2][2] + "\t\n");

        setConfusion_matrix(sb.toString());
    }

    public int getConfusionMatrixAt(int row, int col)
    {
        String cm = getConfusion_matrix();

        if(cm != null)
        {
            String lines[] = cm.split("\n");

            if(lines.length==6)
            {
                return extractNumbersFromString(lines[col+3])[row];
            }
        }

        return -1;
    }

    public void saveModel(Classifier model) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        weka.core.SerializationHelper.write(baos, model);
        String modelString = javax.xml.bind.DatatypeConverter.printBase64Binary(baos.toByteArray());
        setModel(modelString);
    }

    public Classifier loadModel() throws Exception
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(javax.xml.bind.DatatypeConverter.parseBase64Binary(getModel()));
        return (Classifier)weka.core.SerializationHelper.read(bais);
    }

    private int[] extractNumbersFromString(String line)
    {
        ArrayList<Integer> nums = new ArrayList<Integer>();

        String elems[] = line.split(" ");
        for(int i=0;i<elems.length;i++)
        {
            if(isNumber(elems[i]))
            {
                nums.add(new Integer(convertToInt(elems[i])));
            }
        }

        int ret[] = new int[nums.size()];
        int count=0;
        for(Integer i : nums)
        {
            ret[count++] = i.intValue();
        }

        return ret;
    }

    private boolean isNumber(String txt)
    {
        try
        {
            Integer.parseInt(txt);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private int convertToInt(String txt)
    {
        try
        {
            return Integer.parseInt(txt);
        }
        catch(Exception e)
        {
            return -1;
        }            
    }
}
