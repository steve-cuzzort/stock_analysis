/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package technicalanalysis;

import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.meta.CoreMetaData;
import com.tictactec.ta.lib.meta.annotation.*;
import hibernate.entities.Stock;
import java.lang.String;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gtri
 */
public class TAQuery implements com.tictactec.ta.lib.meta.TaFuncService, com.tictactec.ta.lib.meta.TaGrpService
{
    private ArrayList<FuncInfoAndMetaData> m_groupList;
    private ArrayList<FuncInfoAndMetaData> m_funcList;
    private Stock m_stock;
    private double m_open[], m_close[], m_high[], m_low[];
    private int m_volume[];
    private double m_doubleTempArr[][];
    private int m_intTempArr[][];
    private int m_lookUpToIndex = 0;

    public TAQuery(Stock stock)
    {
        m_stock = stock;
        generateGroupList();
        generateArraysFromStock();
        generateTempArrays();
    }

    public void execute(String group, Set<CoreMetaData> set) 
    {
        for (CoreMetaData mi : set) 
        {
            m_groupList.add(new FuncInfoAndMetaData(mi.getFuncInfo(), mi));
        }
    }

    public void execute(CoreMetaData mi) 
    {	
        m_funcList.add(new FuncInfoAndMetaData(mi.getFuncInfo(), mi));
    }

    protected void generateArraysFromStock()
    {
        int alloc_size = m_stock.getEntries().size();
        m_open = new double[alloc_size];
        m_close = new double[alloc_size];
        m_high = new double[alloc_size]; 
        m_low = new double[alloc_size];
        m_volume = new int[alloc_size];

        for(int i=0;i<m_lookUpToIndex;i++)
        {
            m_open[i] = m_stock.getEntries().get(i).getOpen().doubleValue();
            m_close[i] = m_stock.getEntries().get(i).getClose().doubleValue();
            m_high[i] = m_stock.getEntries().get(i).getHigh().doubleValue();
            m_low[i] = m_stock.getEntries().get(i).getLow().doubleValue();
            m_volume[i] = m_stock.getEntries().get(i).getVolume().intValue();
        }
    }

    public double[] getOpen(){return m_open;}
    public double[] getClose(){return m_close;}
    public double[] getHigh(){return m_high;}
    public double[] getLow(){return m_low;}
    public int[] getVolume(){return m_volume;}

    protected void generateGroupList()
    {
        try
        {
            m_groupList = new ArrayList<FuncInfoAndMetaData>();
            CoreMetaData.forEachGrp(this);	

            m_funcList =  new ArrayList<FuncInfoAndMetaData>();
            CoreMetaData.forEachFunc(this);
        }
        catch(Exception e)
        {

        }
    }

    protected void generateTempArrays()
    {
        int max = 0;
        for(FuncInfoAndMetaData fi : m_funcList)
        {
            if(fi.funcInfo.nbOutput() > max)
            {
                max = fi.funcInfo.nbOutput();
            }
        }		

        m_doubleTempArr = new double[max][m_stock.getEntries().size()];
        m_intTempArr = new int[max][m_stock.getEntries().size()];
    }

    public List<String> getGroupList()
    {
        ArrayList<String> ret = new ArrayList<String>();

        for(FuncInfoAndMetaData fi : m_groupList)
        {
            if(!ret.contains(fi.funcInfo.group()))
            {
                ret.add(fi.funcInfo.group());
            }
        }

        return ret;
    }

    public List<String> getFunctionsInGroup(String groupName)
    {
        ArrayList<String> ret = new ArrayList<String>();

        for(FuncInfoAndMetaData fi : m_groupList)
        {
            if(fi.funcInfo.group().equals(groupName))
            {
                ret.add(fi.funcInfo.name());
            }
        }	

        return ret;
    }

    public List<String> getFunctionList()
    {
        ArrayList<String> ret = new ArrayList<String>();

        for(FuncInfoAndMetaData fi : m_funcList)
        {
            ret.add(fi.funcInfo.name());
        }	

        return ret;
    }

    public String describeFunction(String funcName)
    {
        for(FuncInfoAndMetaData fi : m_funcList)
        {
            if(fi.funcInfo.name().equals(funcName))
            {
                    return describeFunction(fi);
            }
        }

        return "Cannot find function " + funcName;
    }

    protected String describeFunction(FuncInfoAndMetaData fi)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(fi.funcInfo.name() + " (" + fi.funcInfo.group() + ")\n");
        sb.append("    *** INPUTS ***\n");

        for (int i = 0; i < fi.funcInfo.nbInput(); i++) 
        {
            InputParameterInfo pinfo = fi.coreMetaData.getInputParameterInfo(i);
            sb.append("    " + pinfo.paramName() + " => " + pinfo.type() + "\n");
        }

        for (int i = 0; i < fi.coreMetaData.getFuncInfo().nbOptInput(); i++) 
        {
            OptInputParameterInfo pinfo = fi.coreMetaData.getOptInputParameterInfo(i);
            sb.append("    " + pinfo.paramName() + " => " + pinfo.type());

            switch (pinfo.type()) 
            {
                case TA_OptInput_RealRange:
                {
                    RealRange rrange = fi.coreMetaData.getOptInputRealRange(i);
                    sb.append("[min="+rrange.min());
                    sb.append(", max="+rrange.max());
                    sb.append(", precision="+rrange.precision());
                    sb.append(", default="+rrange.defaultValue() + "]\n");
                }break;
                case TA_OptInput_RealList:
                {
                    RealList rlist = fi.coreMetaData.getOptInputRealList(i);

                    sb.append("[value=");
                    for (double value : rlist.value()) {
                            sb.append(value + " ");
                    }
                    sb.append("");
                    sb.append(" string="+rlist.string());
                    for (String string : rlist.string()) {
                            sb.append(string + " ");
                    }
                    sb.append("Default value = " + rlist.defaultValue() + "\n");
                }break;
                case TA_OptInput_IntegerRange:
                {
                    IntegerRange irange = fi.coreMetaData.getOptInputIntegerRange(i);
                    sb.append("[min="+irange.min());
                    sb.append(", max="+irange.max());
                    sb.append(", default="+irange.defaultValue() + "]\n");
                }break;
                case TA_OptInput_IntegerList:
                {
                    IntegerList ilist = fi.coreMetaData.getOptInputIntegerList(i);
                    sb.append("\n");
                    sb.append("        [value=");
                    for (int value : ilist.value()) {
                            sb.append(value + " ");
                    }
                    sb.append("\n");
                    sb.append("        string=");
                    for (String string : ilist.string()) {
                            sb.append(string + " ");
                    }
                    sb.append("] Default value = " + ilist.defaultValue() + "\n");
                }break;
            }
        }
        sb.append("    *** OUTPUTS ***\n");
        for (int i = 0; i < fi.coreMetaData.getFuncInfo().nbOutput(); i++) 
        {
            OutputParameterInfo pinfo = fi.coreMetaData.getOutputParameterInfo(i);
            sb.append("    " + pinfo.paramName() + "=>" + pinfo.type());
        }	

        return sb.toString();
    }

    public int getOutputCount(String indicatorName)
    {
        for(FuncInfoAndMetaData fi : m_funcList)
        {
            if(fi.funcInfo.name().equals(indicatorName))
            {
                return fi.funcInfo.nbOutput();
            }
        }                        

        return 0;
    }

    protected FuncInfoAndMetaData getFunctionData(String indicatorName)
    {
        for(FuncInfoAndMetaData fi : m_funcList)
        {
            if(fi.funcInfo.name().equals(indicatorName))
            {
                return fi;
            }
        }                         
        return null;
    }

    public TAOutput[] runInidcator(String indicatorName) throws Exception
    {
        String funcname = indicatorName.toLowerCase().replaceAll("_", "");
        FuncInfoAndMetaData fi = getFunctionData(indicatorName);
        MInteger lOutIdx  = new MInteger();
        MInteger lOutSize = new MInteger();

        Class coreClazz = Class.forName("com.tictactec.ta.lib.Core");
        Object core = coreClazz.newInstance();

        int paramcount=0;
        Object params[] = new Object[4 + fi.funcInfo.nbInput() + fi.funcInfo.nbOptInput() + fi.funcInfo.nbOutput()];

        params[paramcount++] = 0;
        params[paramcount++] = m_open.length-1;

        for (int i = 0; i < fi.funcInfo.nbInput(); i++) 
        {
            InputParameterInfo pinfo = fi.coreMetaData.getInputParameterInfo(i);

            switch(pinfo.type())
            {
                case TA_Input_Integer:
                {
                    params[paramcount++] = m_volume;
                }break;
                case TA_Input_Price:
                {
                    params[paramcount++] = m_high;
                }break; 
                case TA_Input_Real:
                {
                    params[paramcount++] = m_close;
                }break;
            }
        }           

        for(int i=0;i<fi.funcInfo.nbOptInput();i++)
        {
            OptInputParameterInfo oipi = fi.coreMetaData.getOptInputParameterInfo(i);

            switch(oipi.type())
            {
                case TA_OptInput_IntegerList:
                {
                        IntegerList il = fi.coreMetaData.getOptInputIntegerList(i);
                        params[paramcount++] = il.defaultValue();
                }break;
                case TA_OptInput_IntegerRange:
                {
                        IntegerRange ir = fi.coreMetaData.getOptInputIntegerRange(i);
                        params[paramcount++] = ir.defaultValue();
                }break;
                case TA_OptInput_RealList:
                {
                        RealList rl = fi.coreMetaData.getOptInputRealList(i);
                        params[paramcount++] = rl.defaultValue();
                }break;
                case TA_OptInput_RealRange:
                {
                        RealRange rr = fi.coreMetaData.getOptInputRealRange(i);
                        params[paramcount++] = rr.defaultValue();
                }break;                
            }
        }

        params[paramcount++] = lOutIdx;
        params[paramcount++] = lOutSize;

        for (int i = 0; i < fi.coreMetaData.getFuncInfo().nbOutput(); i++) 
        {
            OutputParameterInfo pinfo = fi.coreMetaData.getOutputParameterInfo(i);

            switch(pinfo.type())
            {
                case TA_Output_Integer:
                {
                    params[paramcount++] = new int[m_open.length];
                }break;

                case TA_Output_Real:
                {
                    params[paramcount++] = new double[m_open.length];
                }break;
            }
        }            

        for(Method m : coreClazz.getMethods())
        {
            if(m.getParameterTypes().length == params.length)
            {
                if(m.getName().toLowerCase().equals(funcname))
                {
                    try
                    {
                        m.invoke(core, params);
                        break;
                    }
                    catch(Exception e)
                    {

                    }
                }
            }
        }

        TAOutput ret[] = new TAOutput[fi.funcInfo.nbOutput()];
        paramcount -= fi.funcInfo.nbOutput();
        for(int i=0;i<ret.length;i++)
        {
            OutputParameterInfo pinfo = fi.coreMetaData.getOutputParameterInfo(i);
            switch(pinfo.type())
            {
                case TA_Output_Integer:
                {
                    ret[i] = new TAOutput(funcname+"_"+pinfo.paramName(), (int[])params[paramcount+i]);
                }break;

                case TA_Output_Real:
                {
                    ret[i] = new TAOutput(funcname+"_"+pinfo.paramName(), (double[])params[paramcount+i]);
                }break;
            }
        }

        return ret;
    }

    class FuncInfoAndMetaData
    {
            public FuncInfo funcInfo;
            public CoreMetaData coreMetaData;

            public FuncInfoAndMetaData(FuncInfo fi, CoreMetaData cmd)
            {
                    funcInfo = fi;
                    coreMetaData = cmd;
            }
    }
}
