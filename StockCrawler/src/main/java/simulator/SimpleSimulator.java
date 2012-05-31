/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import hibernate.HibernateUtil;
import hibernate.entities.Stock;
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
    
    public void runSimulaton() throws Exception
    {
        List<String> stocknames = new ArrayList<String>();
        HibernateUtil.startNewSession();
        for(Stock s : Stock.getAllStocks())
        {
            stocknames.add(s.getSymbol());
        }
        HibernateUtil.closeSession();

        for(int stockday = 0;stockday<m_daysBack;stockday++)
        {
            for(String stockname : stocknames)
            {
                HibernateUtil.startNewSession();

                //System.out.println(stockname);
                StockStats ss = StockStats.findStockStat(stockname, 4, .04);
                if(ss != null)
                {
                    Stock stock = Stock.findStock(stockname);
                    int lastday = stock.getEntries().size()-1;
                    //System.out.println(stockname + " =>" + hasGoodFundamentals(ss));
                    if(hasGoodFundamentals(ss))
                    {
                        Instance inst = generateQueryInstance(stock, lastday - m_daysBack + stockday);
                        Classifier model = ss.loadModel();

                        double classification = model.classifyInstance(inst);
                        logger.warn("classifiaction for " + stockname + " = " + classification);
                    }
                }

                HibernateUtil.closeSession();
            }
        }
    }
    
    protected boolean hasGoodFundamentals(StockStats stat)
    {
        // make sure we got decent accuracy
        if(stat.getCorrect() >= .75f && stat.getConfusionMatrixAt(0, 0) > 50)
        {
            return true;
        }
        
        return false;
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
