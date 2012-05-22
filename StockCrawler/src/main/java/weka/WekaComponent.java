/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weka;

import hibernate.HibernateUtil;
import hibernate.entities.Stock;
import hibernate.entities.StockStats;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technicalanalysis.TAOutput;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;


/**
 *
 * @author gtri
 */
public class WekaComponent 
{
	static final Logger logger = LoggerFactory.getLogger(WekaComponent.class);

	public static Instances CreateWekaInstances(Stock stock, List<TAOutput> outs, int look_ahead, double increase) throws Exception
	{
		FastVector      atts = new FastVector();
		FastVector      attsAnswer = new FastVector();

		atts.addElement(new Attribute("open"));
		atts.addElement(new Attribute("close"));
		atts.addElement(new Attribute("high"));
		atts.addElement(new Attribute("low"));
		atts.addElement(new Attribute("volume"));
		
		for(TAOutput out : outs)
		{
			atts.addElement(new Attribute(out.name));
		}

		attsAnswer.addElement("up");
		attsAnswer.addElement("unchanged");
		attsAnswer.addElement("down");
		
		atts.addElement(new Attribute("answer", attsAnswer));

		Instances data = new Instances(stock.getSymbol(), atts, 0);
		int stockEntryCount = stock.getEntries().size();
		for(int i=0;i<stockEntryCount - look_ahead;i++)
		{
			int attrindex=0;
			double vals[] = new double[data.numAttributes()];
			
			double current_price = stock.getEntries().get(i).getHigh().doubleValue();
			vals[attrindex++] = stock.getEntries().get(i).getOpen().doubleValue();
			vals[attrindex++] = stock.getEntries().get(i).getClose().doubleValue();
			vals[attrindex++] = stock.getEntries().get(i).getHigh().doubleValue();
			vals[attrindex++] = stock.getEntries().get(i).getLow().doubleValue();
			vals[attrindex++] = stock.getEntries().get(i).getVolume().doubleValue();
			
			for(TAOutput o : outs)
			{
				vals[attrindex++] = o.values[i];
			}
			
			double future_price = stock.getEntries().get(i+look_ahead).getHigh();
			double ratio = future_price / current_price;
			
			if(ratio >= 1.0f + increase)
			{
				vals[attrindex++] = attsAnswer.indexOf("up");
			}
			else if(ratio <= 1.0f - increase)
			{
				vals[attrindex++] = attsAnswer.indexOf("down");				
			}
			else
			{
				vals[attrindex++] = attsAnswer.indexOf("unchanged");								
			}
			
			
			data.add(new weka.core.Instance(1.0, vals));
		}
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	
	public static void makeModel(Stock stock, Instances data) throws Exception
	{
		if(data.numInstances() > 20)
		{
			// Create a naïve bayes classifier 
			Classifier model = (Classifier)new weka.classifiers.trees.J48();
			//model.buildClassifier(data);

			Evaluation eval = new Evaluation(data);
			Random rand = new Random(1);  // using seed = 1
			int folds = 10;
			eval.crossValidateModel(model, data, folds, rand);

			HibernateUtil.beginTransaction();
			StockStats ss = new StockStats();
			double total = eval.correct() + eval.incorrect();
			ss.setCorrect(new Double(eval.correct() / total));
			ss.setIncorrect(new Double(eval.incorrect() / total));
			ss.setSummary(eval.toSummaryString());
			ss.setClassifier_name(weka.classifiers.trees.J48.class.getName());
			ss.setConfusion_matrix(eval.confusionMatrix());
			logger.info("\n===" + stock.getSymbol() + "===" + ss.getSummary()+"\n"+ss.getConfusion_matrix());

			stock.addStat(ss);
			HibernateUtil.getCurrentSession().save(stock);
			HibernateUtil.commit();
		}
	}
}
