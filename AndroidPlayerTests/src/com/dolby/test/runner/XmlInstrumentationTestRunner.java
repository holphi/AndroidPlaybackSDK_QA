/**
* @Title:			XmlInstrumentationTestRunner
* @Package:			com.dolby.test.runner
* @Description:		Employ XmlInstrumentationTestRunner for JUnit-like test result output, as native InstrumentationTestRunner doesn't support xml report output.   
* @author:			Alex LI
* @date:			2014/02/25
* @COPYRIGHT:		2014 Dolby Labs. All rights reserved
*/

package com.dolby.test.runner;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.test.InstrumentationTestRunner;
import android.os.Bundle;
import android.os.Environment;

import com.dolby.test.annotation.*;

public class XmlInstrumentationTestRunner extends InstrumentationTestRunner 
{
    private long mTestStarted;
    private static final String JUNIT_XML_FILE = "AutoTestResults.xml";
    
    private Document mDocument;
    private Element mRoot;
    
    private int mFailedTCNum;
    private int mSkippedTCNum;
    private int mErrorTCNum;
    private float mTotalTime;
    
    private Pattern mPattern;
    
    private TestLogCollector mLogCollector;
    
    @Override
    public void onStart() 
    {
    	this.mPattern = Pattern.compile("test(.*)");
    	
    	this.mFailedTCNum = 0;
    	this.mSkippedTCNum = 0;
    	this.mErrorTCNum = 0;
    	this.mTotalTime = 0;
    	
    	/*Create a new XML document and return the root element*/
    	try
    	{
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder = factory.newDocumentBuilder();
    		
    		this.mDocument = builder.newDocument();
    		this.mRoot = mDocument.createElement("testsuite");
    		this.mDocument.appendChild(this.mRoot);
    		
    	}catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    	mLogCollector = new TestLogCollector();
    	mLogCollector.start();
    	
    	super.onStart();
    }
   
    @Override
    public void sendStatus(int resultCode, Bundle results)
    {
        super.sendStatus(resultCode, results);
        switch(resultCode)
        {
        	case REPORT_VALUE_RESULT_ERROR:
            case REPORT_VALUE_RESULT_FAILURE:
            case REPORT_VALUE_RESULT_OK:
            	try
            	{
            		recordTestResult(resultCode, results);
                }catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            	if(mLogCollector!=null)
            	{
            		mLogCollector.stop(results.getString(REPORT_KEY_NAME_TEST));
            	}
                break;
            case REPORT_VALUE_RESULT_START:
                recordTestStart(results);
            default:
                break;
        }
    }
    
    //Return priority of test method
    private String getPriority(String className, String methodName)
    {
    	String priority = "High";
    	Method m;
    	try
    	{
    		m = Class.forName(className).getMethod(methodName, null);
    	}catch(Exception ex)
    	{
    		throw new RuntimeException(ex);
    	}
    	
    	if(m!=null)
    	{
    		if(m.isAnnotationPresent(High.class))
    			priority = "High";
    		else if(m.isAnnotationPresent(Medium.class))
    			priority = "Medium";
    		else if(m.isAnnotationPresent(Low.class))
    			priority = "Low";
    		else
    			priority = "High";
    	}
    	else
    		priority = "High";
    	
    	return priority;
    }
    
    //Return case title of test link
    private String getName(String className, String methodName)
    {
    	String name;
    	Method m;
    	
		try 
		{
			m = Class.forName(className).getMethod(methodName, null);
		}catch(Exception ex) 
		{
			throw new RuntimeException(ex);
		}
		
        //If customized annotation Testlink present in test method
        if((m!=null)&&(m.isAnnotationPresent(Testlink.class)))
        {
        	Testlink tl = m.getAnnotation(Testlink.class);
        	name = tl.Title();
        }
        else
        {
        	//Remove test prefix from test methods
            name = rmTestMethodPrefix(methodName);
        }
        
        return name;
    }
   
    private void recordTestStart(Bundle results) 
    {
        mTestStarted = System.currentTimeMillis();
    }
    
    private void recordTestResult(int resultCode, Bundle results) throws IOException
    {
    	//Get test execution time
        float time = (System.currentTimeMillis() - mTestStarted) / 1000.0f;
        this.mTotalTime += time;
        String className = results.getString(REPORT_KEY_NAME_CLASS);
        String testMethod = results.getString(REPORT_KEY_NAME_TEST);
        String stack = results.getString(REPORT_KEY_STACK);
        int current = results.getInt(REPORT_KEY_NUM_CURRENT);
        int total = results.getInt(REPORT_KEY_NUM_TOTAL);
        
        Element tc = mDocument.createElement("testcase");
        tc.setAttribute("classname", className);
        
        tc.setAttribute("name", getName(className, testMethod));
        tc.setAttribute("methodname", testMethod);
        tc.setAttribute("priority", getPriority(className, testMethod));
        tc.setAttribute("time", String.format("%.3f", time));
        
    	//Create failure element for test failure
        if(resultCode!=REPORT_VALUE_RESULT_OK)
        {
        	Element failure = mDocument.createElement("failure");
        	if(stack!=null)
        	{
        		String reason = stack.substring(0, stack.indexOf('\n'));
        		String message = "";
        		int index = reason.indexOf(":");
        		if(index>-1)
        		{
        			message = reason.substring(index+1);
        			reason = reason.substring(0, index);
        		}
        		failure.setAttribute("message", message);
        		failure.setAttribute("type", reason);
        		failure.setTextContent(stack);
        		tc.appendChild(failure);
        	}
    		tc.appendChild(mDocument.createElement("system-err"));
    		this.mFailedTCNum++;
        }
        
        this.mRoot.appendChild(tc);
        
        if(current==total)
        {
        	this.mRoot.setAttribute("name", "");
        	this.mRoot.setAttribute("skips", String.valueOf(this.mSkippedTCNum));
        	this.mRoot.setAttribute("failures", String.valueOf(this.mFailedTCNum));
        	this.mRoot.setAttribute("errors", String.valueOf(this.mErrorTCNum));
        	this.mRoot.setAttribute("tests", String.valueOf(total));
        	this.mRoot.setAttribute("time", String.format("%.3f", this.mTotalTime));
        }
    }

    //Remove the prefix from test method name
    private String rmTestMethodPrefix(String testMethodName)
    {
    	Matcher matcher = this.mPattern.matcher(testMethodName);
    	
    	if(matcher.matches())
    		return matcher.group(1);
    	else
    		return testMethodName;
    }
    
    @Override
    public void finish(int resultCode, Bundle results) 
    {
        saveTestResults();
        super.finish(resultCode, results);
    }
    
    //Save XML result to file
    private void saveTestResults()
    {
    	try
    	{
    		Transformer transformer = TransformerFactory.newInstance().newTransformer();
    		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    		Result output = new StreamResult(new File(Environment.getExternalStorageDirectory().getPath(), JUNIT_XML_FILE));
    		Source input = new DOMSource(this.mDocument);
    		
    		transformer.transform(input, output);
    	}catch(Exception ex)
    	{
    		throw new RuntimeException(ex);
    	}
    }
}