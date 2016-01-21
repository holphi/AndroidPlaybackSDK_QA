package com.dolby.dope.test;

import junit.framework.Assert;

import com.dolby.infra.Player.PlayerState;
import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;
import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Low;
import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Testlink;

public class PlayerStateTests extends BaseTests {
	
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
	
	private int expectedDuration = 60160;
	private int expectedDuration_DASH = 87168;
	
	private String testContent = testSignals_MP4.get("The_flash_ddp.mp4");
	private String testContent_DASH = testSignals_DASH.get("Basic_DDP_87s");
	
	@High
	@Testlink(Id="IF-170", Title="getPlaybackDuration: Call getPlaybackDuration during media playback")
	public void testIF_170() throws Exception
	{
		gotoPlayingState(testContent);
		
		Assert.assertEquals("Call getPlaybackDuration() in playing state", expectedDuration, playerWrapper.getPlaybackDuration());
	}

	@High
	@Testlink(Id="IF-171", Title="getPlaybackDuration: Call getPlaybackDuration in stopped state")
	public void testIF_171() throws Exception
	{
		int expectedDuration = 0;
		
		gotoPlayingState(testSignals_MP4.get("flight_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertEquals("Call getPlaybackDuration() in stopped state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Medium
	@Testlink(Id="IF-172", Title="getPlaybackDuration: Call getPlaybackDuration in prepared state")
	public void testIF_172() throws Exception
	{
		gotoPreparedState(testContent);
		
		Assert.assertEquals("Call getPlaybackDuration() in idle state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Low
	@Testlink(Id="IF-173", Title="getPlaybackDuration: Call getPlaybackDuration if preparation fails")
	public void testIF_173() throws Exception
	{
		long expectedDuration = 0;
		
		gotoErrorState();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The expected duration should be 0", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Low
	@Testlink(Id="IF-174", Title="getPlaybackDuration: Call getPlaybackDuration in idle state")
	public void testIF_174() throws Exception
	{
		int expectedDuration = 0;
		
		Assert.assertEquals("Call getPlaybackDuration() in idle state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Low
	@Testlink(Id="IF-175", Title="getPlaybackDuration: Call getPlaybackDuration in initialized state")
	public void testIF_175() throws Exception
	{
		int expectedDuration = 0;
		
		gotoInitializedState(testContent);
		
		Assert.assertEquals(String.format("Call getPlaybackDuration() in initialized state, it should return %d",expectedDuration), 
				expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Low
	@Testlink(Id="IF-178", Title="getPlaybackDuration: Call getPlaybackDuration in paused state")
	public void testIF_178() throws Exception
	{
		gotoPausedState(testContent);
		
		Assert.assertEquals("Call getPlaybackDuration() in paused state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@High
	@Testlink(Id="IF-179", Title="getPlaybackPosition: Keep calling getPlaybackPosition during media playback")
	public void testIF_179() throws Exception
	{
		gotoPlayingState(testContent);
		
		int[] range = {500, 2200};
		
		for(int i=0;i<10; i++)
		{
			long currTime = playerWrapper.getPlaybackPosition();

			Assert.assertTrue(String.format("getPlaybackPosition should return correct value. %s",currTime), currTime>range[0]&&currTime<range[1]);
			
			CommonUtil.sleep(2);
			
			range[0]=range[0]+2000;
			range[1]=range[0]+1200;
		}
	}
	
	@Medium
	@Testlink(Id="IF-180", Title="getPlaybackPosition: Call getPlaybackPosition in stopped state")
	public void testIF_180() throws Exception
	{
		int expected = 0;
		gotoPlayingState(testSignals_MP4.get("flight_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertEquals(String.format("Call getPlaybackPosition() in stopped statem it should return %d",expected), 
							expected , 
							playerWrapper.getPlaybackPosition());	
	}
	
	@Medium
	@Testlink(Id="IF-181", Title="getPlaybackPosition: Call getPlaybackPosition in paused state")
	public void testIF_181() throws Exception
	{
		int[] expectedRange = new int[]{500, 2000};
		
		gotoPausedState(testSignals_MP4.get("flight_ddp_13s.mp4"));
		
		long currPos = playerWrapper.getPlaybackPosition();
		
		Assert.assertTrue("Call getPlaybackPosition() in paused state", currPos>expectedRange[0]&&currPos<expectedRange[1]);	
	}
	
	@Low
	@Testlink(Id="IF-182", Title="getPlaybackPosition: Call getPlaybackPosition in prepared state")
	public void testIF_182() throws Exception
	{
		int expectedCurrTime = 0;
		
		gotoPreparedState(testContent);
		
		Assert.assertEquals("Call getPlaybackPosition in paused state.", expectedCurrTime, playerWrapper.getPlaybackPosition());
	}
	
	@Low
	@Testlink(Id="IF-183", Title="getPlaybackPosition: Call getPlaybackPosition in idle state")
	public void testIF_183() throws Exception
	{
		int expectedCurrTime = 0;
		
		Assert.assertEquals("Call getPlaybackPosition in paused state.", expectedCurrTime, playerWrapper.getPlaybackPosition());
	}
	
	@Low
	@Testlink(Id="IF-184", Title="getPlaybackPosition: Call getPlaybackPosition in initialized state")
	public void testIF_184() throws Exception
	{
		int expectedCurrTime = 0;
		
		gotoInitializedState(testContent);
		
		Assert.assertEquals("Call getPlaybackPosition in initialized state.", expectedCurrTime, playerWrapper.getPlaybackPosition());
	}
	
	@Low
	@Testlink(Id="IF-187", Title="getPlaybackPosition: Call getPlaybackPosition in error state")
	public void testIF_187() throws Exception
	{
		gotoErrorState();
		
		long expectedPosition = 0;
		
		Assert.assertEquals("The expected duration should be 0", expectedPosition, playerWrapper.getPlaybackPosition());
		Assert.assertFalse("The IllegalStateError exception should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@High
	@Testlink(Id="IF-209", Title="getState: Call getState() in idle state")	
	public void testIF_209() throws Exception
	{
		Assert.assertEquals("Call getState() in idle state.", PlayerState.IDLE, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-210", Title="getState: Call getState() in initialized state")	
	public void testIF_210() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("Call getState() in idle state.", PlayerState.INITIALIZED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-211", Title="getState: Call getState() in prepared state")	
	public void testIF_211() throws Exception
	{
		gotoPreparedState();
		
		Assert.assertEquals("Call getState() in prepared state.", PlayerState.PREPARED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-212", Title="getState: Call getState() in playing state")	
	public void testIF_212() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		Assert.assertEquals("Call getState() in playing state.", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-213", Title="getState: Call getState() in paused state")	
	public void testIF_213() throws Exception
	{
		gotoPausedState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		Assert.assertEquals("Call getState() in paused state.", PlayerState.PAUSED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-214", Title="getState: Call getState() in stopped state")	
	public void testIF_214() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertEquals("Call getState() in stopped state.", PlayerState.STOPPED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-215", Title="getState: Call getState() in error state")	
	public void testIF_215() throws Exception
	{
		gotoErrorState();
		
		Assert.assertEquals("Call getState() in error state.", PlayerState.ERROR, playerWrapper.getState());
	}

	@Medium
	@Testlink(Id="IF-261", Title="getPlaybackDuration: DASH, OnDemand, Call getPlaybackDuration in media playback")	
	public void testIF_261() throws Exception
	{
		gotoPlayingState(testContent_DASH);
		CommonUtil.sleep(5);
		Assert.assertEquals("Call getPlaybackDuration in playing state", expectedDuration_DASH, playerWrapper.getPlaybackDuration());
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		Assert.assertEquals("Call getPlaybackDuration in paused state", expectedDuration_DASH, playerWrapper.getPlaybackDuration());
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		Assert.assertEquals("Call getPlaybackDuration in stopped state", 0, playerWrapper.getPlaybackDuration());
	}
	
	@Low
	@Testlink(Id="IF-262", Title="getPlaybackDuration: DASH, OnDemand, Call getPlaybackDuration in prepared state")	
	public void testIF_262() throws Exception
	{
		gotoPreparedState(testContent);
		
		Assert.assertEquals("Call getPlaybackDuration() in idle state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@Medium
	@Testlink(Id="IF-263", Title="getPlaybackPosition: DASH, OnDemand, Call getPlaybackPosition in playback")
	public void testIF_263() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_ED_480x320"));
		
		long lastCurrTime = 0;
		
		for(int i=0;i<30; i++)
		{
			long currTime = playerWrapper.getPlaybackPosition();

			Assert.assertTrue("getPlaybackPosition should return correct value.", currTime>lastCurrTime);
			
			lastCurrTime = currTime;
			
			CommonUtil.sleep(2);
		}
	}
	
	@Low
	@Testlink(Id="IF-264", Title="getPlaybackPosition: DASH, OnDemand, Call getPlaybackPosition in prepared state")	
	public void testIF_264() throws Exception
	{
		gotoPreparedState(testContent);
		
		Assert.assertEquals("Call getPlaybackDuration() in idle state", expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@High
	@Testlink(Id="IF-430", Title="getMediaInfo: Call getMediaInfo in prepared state")	
	public void testIF_430() throws Exception
	{
		gotoPreparedState(testContent_DASH);
		
		Assert.assertNotNull("It should return an instance of MediaInfo class.", playerWrapper.getMediaInfo());
		Assert.assertFalse("The IllegalStateError exception should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-431", Title="getMediaInfo: Call getMediaInfo in playing state")	
	public void testIF_431() throws Exception
	{
		gotoPlayingState(testContent_DASH);
		
		Assert.assertNotNull("It should return an instance of MediaInfo class.", playerWrapper.getMediaInfo());
		Assert.assertFalse("The IllegalStateError exception should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="IF-432", Title="getMediaInfo: Call getMediaInfo in paused state")	
	public void testIF_432() throws Exception
	{
		gotoPausedState(testContent_DASH);
		
		Assert.assertNotNull("It should return an instance of MediaInfo class.", playerWrapper.getMediaInfo());
		Assert.assertFalse("The IllegalStateError exception should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="IF-632", Title="getPlaybackPosition: Call getPlaybackPosition in preparing state")
	public void testIF_632() throws Exception
	{
		gotoPreparingState();
		
		Assert.assertEquals("Call getPlaybackPosition in PREPARING state.", 0, playerWrapper.getPlaybackPosition());
		Assert.assertEquals("The player should be in preparing state", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-633", Title="getPlaybackDuration: Call getPlaybackDuration in preparing state")
	public void testIF_633() throws Exception
	{
		gotoPreparingState();
		
		Assert.assertEquals("Call getPlaybackPosition in PREPARING state.", 0, playerWrapper.getPlaybackDuration());
		Assert.assertEquals("The player should be in preparing state", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-634", Title="getState: Call getState in preparing state")
	public void testIF_634() throws Exception
	{
		gotoPreparingState();

		Assert.assertEquals("The player should be in preparing state", PlayerState.PREPARING, playerWrapper.getState());
	}
}
