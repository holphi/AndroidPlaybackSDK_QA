/**
//* @Title:			TestLogCollector
* @Package:			com.dolby.test.runner
* @Description:		This class is used to collect test log info during test execution.   
* @author:			Alex LI
* @date:			2014/06/12
* @COPYRIGHT:		2014 Dolby Labs. All rights reserved
*/

package com.dolby.test.runner;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class TestLogCollector extends Thread
{
	private Process _process;
	private String _logFilePath;
	
	public TestLogCollector()
	{
		this._logFilePath = String.format("%s/test.log", Environment.getExternalStorageDirectory());
		
		//Delete the original file
		File f = new File(_logFilePath);
		if(f.exists()&&f.isFile())
			f.delete();
	}
		
	public void run()
	{
		try
		{				
			//Flush original log and shut down logcat;
			_process = Runtime.getRuntime().exec("/system/bin/logcat -c");
			_process = null;
		    
			//Start writing log to default external storage
			String cmd = String.format("/system/bin/logcat -v time -f %s", _logFilePath);
			_process = Runtime.getRuntime().exec(cmd);
					
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void interrupt()
	{
		if(_process!=null)
			_process.destroy();
		
		try 
		{
			Runtime.getRuntime().exec("/system/bin/logcat -c");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.interrupt();
	}
	
	//Shut down test log collection, save it to folder /mnt/sdcard/;
	public void stop(String fileName)
	{
		//Shut down test log collection
		this.interrupt();
		
		try
		{
			Runtime.getRuntime().exec(String.format("mv %s/test.log %s/%s.log", Environment.getExternalStorageDirectory(), 
										Environment.getExternalStorageDirectory(), fileName));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
