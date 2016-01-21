/**
* @Title:			CommonUtil
* @Package:			com.dolby.dope.test.util
* @Description:		Utility class serves for Automation test cases 
* @author:			Alex LI
* @date:			2014/04/29
* @COPYRIGHT:		2014 Dolby Labs. All rights reserved
*/

package com.dolby.dope.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Random;

import android.util.Log;

public class CommonUtil 
{
	public enum ErrorFlag
	{
		ILLEGAL_STATE_ERROR_FLAG, ILLEGAL_ARGUMENT_ERROR_FLAG, INSTANTIATION_ERROR_FLAG, PROP_NOT_FOUND_ERROR_FLAG, INVALID_VALUE_ERROR_FLAG
	}
	
	protected static final String TAG = "CommonUtil";
	
	private static String CONFIG_FILE_PATH = "/mnt/sdcard/config.properties";
	private static String ILLEGAL_STATE_ERROR = "ILLEGAL_STATE_EXCEPTION_ERROR";
	private static String ILLEGAL_ARGUMENT_ERROR = "ILLEGAL_ARGUMENT_ERROR";
	private static String INSTANTIATION_ERROR = "INSTANTIATION_ERROR";
	private static String PROP_NOT_FOUND_ERROR = "PROPERTY_NOT_FOUND_ERROR";
	private static String INVALID_VALUE_ERROR = "INVALID_VALUE_ERROR";
	
	public static void sleep(int sec) throws Exception
	{
		Log.i(TAG, String.format("Sleep %d second(s) forcibly.", sec));

		Thread.sleep(sec * 1000);
	}
	
	public static void sleep(double sec) throws Exception
	{
		Log.i(TAG, String.format("Sleep %f sceond(s) forcibly", sec));
		
		Thread.sleep((long)(sec * 1000));
	}
	
    /**
     * Read config file.
     */
	private static Properties loadConfig()
	{
		Properties properties = new Properties();
		try
		{
			FileInputStream s = new FileInputStream(CONFIG_FILE_PATH);
			properties.load(s);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return properties;
	}
	
    /**
     * Save config file.
     */
	private static void saveConfig(Properties properties)
	{
		try
		{
			File file = new File(CONFIG_FILE_PATH);
			if(!file.exists())
				file.createNewFile();
			FileOutputStream s = new FileOutputStream(file);
			properties.store(s, "");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
    /**
     * Reset all error flags to 0.
     */
	public static void resetErrorFlags()
	{
		Log.i(TAG, "Reset all error flags.");
		
		Properties prop = new Properties();
		
		prop.put(ILLEGAL_STATE_ERROR, "0");
		prop.put(ILLEGAL_ARGUMENT_ERROR, "0");
		prop.put(INSTANTIATION_ERROR, "0");
		
		saveConfig(prop);
	}

    /**
     * Set error flag
     */
	public static void setErrorFlag(ErrorFlag errorFlag, boolean flag)
	{
		String strErrFlag = ""; 
		
		Properties prop = loadConfig();
		
		switch(errorFlag)
		{
			case ILLEGAL_STATE_ERROR_FLAG:
				strErrFlag = ILLEGAL_STATE_ERROR;
				break;
			case ILLEGAL_ARGUMENT_ERROR_FLAG:
				strErrFlag = ILLEGAL_ARGUMENT_ERROR;
				break;
			case INSTANTIATION_ERROR_FLAG:
				strErrFlag = INSTANTIATION_ERROR;
				break;
			case PROP_NOT_FOUND_ERROR_FLAG:
				strErrFlag = PROP_NOT_FOUND_ERROR;
				break;
			case INVALID_VALUE_ERROR_FLAG:
				strErrFlag = INVALID_VALUE_ERROR;
				break;
			default:
				strErrFlag = ILLEGAL_STATE_ERROR;
				break;
		}
		
		if(flag)
		{
			prop.put(strErrFlag, "1");
		}else
		{
			prop.put(strErrFlag, "0");
		}
		
		saveConfig(prop);
	}
	
    /**
     * Get error flag
     */
	public static boolean getErrorFlag(ErrorFlag errorFlag) throws Exception
	{		
		String strErrFlag = "";
		
		switch(errorFlag)
		{
			case ILLEGAL_STATE_ERROR_FLAG:
				strErrFlag = ILLEGAL_STATE_ERROR;
				break;
			case ILLEGAL_ARGUMENT_ERROR_FLAG:
				strErrFlag = ILLEGAL_ARGUMENT_ERROR;
				break;
			case INSTANTIATION_ERROR_FLAG:
				strErrFlag = INSTANTIATION_ERROR;
				break;
			case PROP_NOT_FOUND_ERROR_FLAG:
				strErrFlag = PROP_NOT_FOUND_ERROR;
				break;
			case INVALID_VALUE_ERROR_FLAG:
				strErrFlag = INVALID_VALUE_ERROR;
				break;
			default:
				strErrFlag = ILLEGAL_STATE_ERROR;
				break;
		}
		
		Properties prop = loadConfig();
		
		if(prop!=null)
		{
			if(prop.containsKey(strErrFlag))
			{
				String value = (String)prop.get(strErrFlag);
				if(value.equals("1"))
					return true;
				else
					return false;
			}
		}else
		{
			throw new Exception("Null object of Properties");
		}
		
		return false;
	}

    /**
     * Get a random number within a given upper limit;
     */
	public static int getRandomInt(int max) throws Exception
	{
		int ret;
		Random r = new Random();
		ret = r.nextInt(max);
		
		Log.i(TAG, String.format("Generate an integer value: %d", ret));
		
		return ret;
	}
	
	/*
	 * Get a random double number within a given upper limit 
	 */
	public static double getRandomDouble(double max) throws Exception
	{
		double ret;
		Random r = new Random();
		ret = r.nextDouble()*max;
		
		return ret;
	}
	
	/**
	 * Get a random double number within a given range;
	 */
	public static double getRandomDouble(double min, double max) throws Exception
	{
		double range = max - min;
		
		double ret; 
		
		Random r = new Random();
		ret = r.nextDouble()*range;
		
		return ret;
	}
	
    /**
     * Get a random number within a given range;
     */
	public static int getRandomInt(int min, int max) throws Exception
	{
		
		int ret;
		int range = max-min;
		
		ret = (int)(Math.random()*min) + range;
		
		Log.i(TAG, String.format("Generate an integer value between %d and %d: %d", min, max, ret));
		
		return ret;
	}
	
}
