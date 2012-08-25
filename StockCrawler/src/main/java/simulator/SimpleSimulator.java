/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import hibernate.HibernateUtil;
import hibernate.entities.Stock;
import hibernate.entities.StockEntry;
import hibernate.entities.StockStats;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technicalanalysis.TAOutput;
import technicalanalysis.TAQuery;
import weka.WekaComponent;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author gtri
 */
public class SimpleSimulator 
{
    static final Logger logger = LoggerFactory.getLogger(SimpleSimulator.class);
    
    int m_daysBack;
    public SimpleSimulator(int back)
    {
        m_daysBack = back;
    }
    
    public int runSimulaton(int startMoney) throws Exception
    {
        int money = startMoney;
        List<String> stocknames = new ArrayList<String>();
        HibernateUtil.startNewSession();
        for(Stock s : Stock.getAllStocks())
        {
            stocknames.add(s.getSymbol());
        }
        HibernateUtil.closeSession();

        int total=0, good=0;
        for(String stockname : stocknames)
        {
            HibernateUtil.startNewSession();

            total++;

            try
            {
                StockStats ss = StockStats.findStockStat(stockname, 3, .03);
                Stock stock = Stock.findStock(stockname);

                if(ss != null)
                {
                    generatePrediction(ss, stock, 3, "3_.03");                
                }

                ss = StockStats.findStockStat(stockname, 5, .05);
                if(ss != null)
                {
                    generatePrediction(ss, stock, 5, "5_.05");                
                }

                ss = StockStats.findStockStat(stockname, 10, .1);
                if(ss != null)
                {
                    generatePrediction(ss, stock, 10, "10_.1");                
                }
            }
            catch(Exception e){e.printStackTrace();}
            HibernateUtil.closeSession();
        }
        return money;
    }
    
    protected boolean hasGoodFundamentals(StockStats stat, double goldenRatio)
    {
        // make sure we got decent accuracy
        String cm = stat.getConfusion_matrix();
        double correct = stat.getConfusionMatrixAt(0, 0), falsepositives = stat.getConfusionMatrixAt(0, 1) + stat.getConfusionMatrixAt(0, 2);
        double ratio = correct/falsepositives;
        if(stat.getCorrect() >= .75f && stat.getConfusionMatrixAt(0, 0) > 100 && ratio > goldenRatio)
        {
            return true;
        }
        
        return false;
    }
    
    protected void generatePrediction(StockStats ss, Stock stock, int lookahead, String tag) throws Exception
    {            
        Classifier model = ss.loadModel();
        logger.info(ss.getStock_sym() + "\n" + ss.getSummary());
        int lastday = stock.getEntries().size()-1;
        
        for(int stockday = 0;stockday< m_daysBack-lookahead;stockday++)
        {
            HibernateUtil.beginTransaction();
            Instance inst = generateQueryInstance(stock, lastday - m_daysBack + stockday);

            double classification = model.classifyInstance(inst);                        
            //logger.info("classifiaction for " + stockname + " = " + classification);

            StockEntry se_now = stock.getEntries().get(lastday - m_daysBack + stockday);
            se_now.setInfoByTag(tag, classification + "");
            HibernateUtil.getCurrentSession().save(se_now);
            HibernateUtil.commit();
        }      
    }
    
    protected Instance generateQueryInstance(Stock stock, int index) throws Exception
    {
        HibernateUtil.startNewSession();

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

        Instances insts = WekaComponent.CreateWekaInstances(stock, taouts, 1, 1);
        Instance inst = insts.instance(index);
        
        inst.setMissing(inst.numAttributes()-1);
        
        HibernateUtil.closeSession();
        return inst;
    }
}
