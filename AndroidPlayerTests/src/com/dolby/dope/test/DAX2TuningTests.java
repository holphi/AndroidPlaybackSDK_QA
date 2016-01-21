package com.dolby.dope.test;

import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;
import com.dolby.infra.Player.PlayerState;
import com.dolby.infra.Player.Value;
import com.dolby.test.annotation.Testlink;

import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Low;


import junit.framework.Assert;

public class DAX2TuningTests extends BaseTests
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		CommonUtil.resetErrorFlags();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	private String strDTTFilePath = "file:///sdcard/dolby/DOPE/tuning/dtt_Nexus5.xml";
	
	private final String DAX_DEC_DAP_TUNINGS = "DAX_DEC_DAP_TUNINGS";
	
	@High
	@Testlink(Id="IF-645", Title="Users begin DAX2 playback with valid device tuning")	
	public void testIF_645() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		playerWrapper.setProperty(DAX_DEC_DAP_TUNINGS, new Value(loadDTTFile(strDTTFilePath)));
		CommonUtil.sleep(5);
		
		Assert.assertEquals("The player should be in playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-646", Title="Users begin DAX2 playback with invalid device tuning")
	public void testIF_646() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		playerWrapper.setProperty(DAX_DEC_DAP_TUNINGS, new Value("HELLO WORLD"));
		CommonUtil.sleep(5);
		
		Assert.assertTrue("An IllegalArgumentError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertEquals("The player should be in playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-668", Title="Users begin DAX2 playback without setting device tuning")
	public void testIF_647() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.setSource(testSignals_MP4.get("The_flash_ddp.mp4"));
		playerWrapper.prepare();
		CommonUtil.sleep(2);
		
		playerWrapper.start();
		
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The player should be in playing state", PlayerState.ERROR, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-648", Title="Users set device tuning in IDLE state")
	public void testIF_648() throws Exception
	{
		playerWrapper.setProperty(DAX_DEC_DAP_TUNINGS, new Value(loadDTTFile(strDTTFilePath)));
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));	
	}
	
	@Low
	@Testlink(Id="IF-649", Title="Users set device tuning in ERROR state")
	public void testIF_649() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.setProperty(DAX_DEC_DAP_TUNINGS, new Value(loadDTTFile(strDTTFilePath)));
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));	
	}
}