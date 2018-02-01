package com.foo.dope.test;

import junit.framework.Assert;

import android.util.Log;

import com.foo.infra.Player.PlayerState;
import com.foo.infra.Player.MediaInfo;
import com.foo.infra.Player.Value;

import com.foo.dope.test.util.CommonUtil;
import com.foo.dope.test.util.CommonUtil.ErrorFlag;
import com.foo.test.annotation.Medium;
import com.foo.test.annotation.Low;
import com.foo.test.annotation.Testlink;

public class IllegalStateHandlingTests extends BaseTests 
{
	
	private MediaInfo mediaInfo;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		mediaInfo=null;
		
		CommonUtil.resetErrorFlags();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Medium
	@Testlink(Id="IF-1", Title="Call setSource in initialized state")
	public void testIF_1() throws Exception
	{
		gotoInitializedState();
		
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertFalse("The IllegalStateError exception should not be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-2", Title="Call setSource in prepared state")
	public void testIF_2() throws Exception
	{
		gotoPreparedState();
		
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in prepared state.", PlayerState.PREPARED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-3", Title="Call setSource in playing state")
	public void testIF_3() throws Exception
	{		
		gotoPlayingState();
		
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-4", Title="Call setSource in paused state")
	public void testIF_4() throws Exception
	{
		gotoPausedState();
		
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in paused state", PlayerState.PAUSED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-7", Title="Call prepare in idle state")
	public void testIF_7() throws Exception
	{
		playerWrapper.prepare();		
		CommonUtil.sleep(1);

		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-8", Title="Call prepare in prepared state")
	public void testIF_8() throws Exception
	{
		gotoPreparedState();
		
		playerWrapper.prepare();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in prepared state", PlayerState.PREPARED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception  should be thrown.", 
				          CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	
	@Medium
	@Testlink(Id="IF-9", Title="Call prepare in playing state")
	public void testIF_9() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		playerWrapper.prepare();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-10", Title="Call prepare in paused state")
	public void testIF_10() throws Exception
	{
		Log.i(TAG, "Transit the playre to paused state first.");
		gotoPausedState();
		
		playerWrapper.prepare();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in paused state", PlayerState.PAUSED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-11", Title="Call prepare in error state")
	public void testIF_11() throws Exception
	{
		Log.i(TAG, "Transit the player to error state first.");
		gotoErrorState();
		
		playerWrapper.prepare();
		CommonUtil.sleep(1);
		
		//Assert.assertEquals("The player should be in error state", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown in error state.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}

	@Medium
	@Testlink(Id="IF-28", Title="Call prepare in stopped state")
	public void testIF_28() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(6);
		
		waitForPlayerState(PlayerState.STOPPED, 10);
		
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.prepare();
		CommonUtil.sleep(1);
		
		Assert.assertTrue("The exception IllegalStateError should be thrown.", 
						  CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-52", Title="Call start in idle state")
	public void testIF_52() throws Exception
	{
		Assert.assertEquals("The state of the player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown in idle state", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-53", Title="Call start in initialized state")
	public void testIF_53() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The state of the player should be in initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-54", Title="Call start in stopped state")
	public void testIF_54() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The state of the player should be in stoppped state.", PlayerState.STOPPED, playerWrapper.getState());
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in stopped state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-55", Title="Call start in error state")
	public void testIF_55() throws Exception
	{
		Log.i(TAG, "Transit the player to error state first.");
		gotoErrorState();
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}

	@Medium
	@Testlink(Id="IF-112", Title="Call pause in idle state")
	public void testIF_112() throws Exception
	{
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-113", Title="Call pause in initialized state")
	public void testIF_113() throws Exception
	{
		gotoInitializedState();
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-114", Title="Call pause in prepared state")
	public void testIF_114() throws Exception
	{
		gotoPreparedState();
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in prepared state.", PlayerState.PREPARED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-115", Title="Call pause in stopped state")
	public void testIF_115() throws Exception
	{
		gotoPlayingState();
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The player should transit to stopped state when finishing media playback.", PlayerState.STOPPED, playerWrapper.getState());
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in stopped state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-116", Title="Call pause in error state")
	public void testIF_116() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
		
	@Medium
	@Testlink(Id="IF-132", Title="Call stop in error state")
	public void testIF_132() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.stop();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}

	@Medium
	@Testlink(Id="IF-299", Title="Call seekTo in idle state")
	public void testIF_299() throws Exception
	{
		playerWrapper.seekTo(3000);		
		
		CommonUtil.sleep(1);

		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-300", Title="Call seekTo in initialized state")
	public void testIF_300() throws Exception
	{
		gotoInitializedState();
		
		playerWrapper.seekTo(3000);
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-301", Title="Call seekTo in stopped state")
	public void testIF_301() throws Exception
	{
		gotoPlayingState();
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The player should transit to stopped state when finishing media playback.", PlayerState.STOPPED, playerWrapper.getState());
		
		playerWrapper.seekTo(3000);
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in stopped state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-302", Title="Call seekTo in error state")
	public void testIF_302() throws Exception
	{
		Log.i(TAG, "Transit the player to error state first.");
		gotoErrorState();
		
		playerWrapper.seekTo(3000);
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-433", Title="Call getMediaInfo in idle state")
	public void testIF_433() throws Exception
	{
		mediaInfo = playerWrapper.getMediaInfo();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("The IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-434", Title="Call getMediaInfo in initialized state")
	public void testIF_434() throws Exception
	{	
		gotoInitializedState();
		
		mediaInfo = playerWrapper.getMediaInfo();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertTrue("The IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-435", Title="Call getMediaInfo in stopped state")
	public void testIF_435() throws Exception
	{	
		gotoPlayingState();
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The player should be stopped state.", PlayerState.STOPPED, playerWrapper.getState());
		
		mediaInfo = playerWrapper.getMediaInfo();
		CommonUtil.sleep(1);
		Assert.assertTrue("The IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-436", Title="Call getMediaInfo in error state")
	public void testIF_436() throws Exception
	{
		gotoErrorState();
		
		Assert.assertEquals("The player should be error state.", PlayerState.ERROR, playerWrapper.getState());
		
		mediaInfo = playerWrapper.getMediaInfo();
		CommonUtil.sleep(1);
		Assert.assertTrue("The IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}

	@Medium
	@Testlink(Id="IF-475", Title="Call selStream in idle state")
	public void testIF_475() throws Exception
	{	
		playerWrapper.switchStream(0, 1);		
		CommonUtil.sleep(1);

		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-476", Title="Call selStream in initialized state")
	public void testIF_476() throws Exception
	{
		gotoInitializedState();
		
		playerWrapper.switchStream(0, 1);
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-477", Title="Call selStream in stopped state")
	public void testIF_477() throws Exception
	{	
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The state of the player should be in stoppped state.", PlayerState.STOPPED, playerWrapper.getState());
		
		playerWrapper.switchStream(0, 1);
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}

	@Medium
	@Testlink(Id="IF-479", Title="Call selStream in error state")
	public void testIF_479() throws Exception
	{	
		Log.i(TAG, "Transit the player to error state first.");
		gotoErrorState();
		
		playerWrapper.switchStream(0, 1);
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	
	}
	
	@Low
	@Testlink(Id="IF-623", Title="Call start in preparing state")
	public void testIF_623() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-624", Title="Call pause in preparing state")
	public void testIF_624() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-625", Title="Call stop in preparing state")
	public void testIF_625() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.stop();
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-626", Title="Call seekTo in preparing state")
	public void testIF_626() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.seekTo(0);
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-635", Title="getProperty: Call getProperty in preparing state")
	public void testIF_635() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.getProperty("null");
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-630", Title="Call selectStream in preparing state")
	public void testIF_630() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.switchStream(0, 0);
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-631", Title="Call setSource in preparing state")
	public void testIF_631() throws Exception
	{
		gotoPreparingState();
		
		playerWrapper.setSource(testSignals_DASH.get("Basic_Bunny_96k"));
		CommonUtil.sleep(1);
		
		Assert.assertTrue("An IllegalStateError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		Assert.assertEquals("The player should be in preparing state.", PlayerState.PREPARING, playerWrapper.getState());
	}
}