/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StockAnalysis;

import org.apache.log4j.*;
import simulator.SimpleSimulator;

/**
 *
 * @author gtri
 */
public class MainStockPicker 
{
    public static void main(String args[]) throws Exception
    {
        Logger rootLogger = Logger.getRootLogger();
        if (!rootLogger.getAllAppenders().hasMoreElements()) 
        {
                rootLogger.setLevel(Level.INFO);
                try
                {
                        rootLogger.addAppender(new FileAppender(
                                    new PatternLayout("%-5p [%t]: %m%n"), "stocks.log"));
                }
                catch(Exception e){}
                // The TTCC_CONVERSION_PATTERN contains more info than
                // the pattern we used for the root logger
                Logger pkgLogger = rootLogger.getLoggerRepository().getLogger("robertmaldon.moneymachine");
                pkgLogger.setLevel(Level.DEBUG);
                pkgLogger.addAppender(new ConsoleAppender(
                            new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        }

        SimpleSimulator sim = new SimpleSimulator(50);
        sim.runSimulaton();
    }
}
