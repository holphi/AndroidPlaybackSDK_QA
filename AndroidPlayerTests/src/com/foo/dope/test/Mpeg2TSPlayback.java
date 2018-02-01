package com.foo.dope.test;

import junit.framework.Assert;

import com.foo.infra.Player;
import com.foo.infra.Player.Error;
import com.foo.infra.Player.PlayerState;

import com.foo.test.annotation.Medium;
import com.foo.test.annotation.Testlink;
import com.foo.test.annotation.High;
import com.foo.test.annotation.Low;

import com.foo.dope.test.util.CommonUtil;
import com.foo.dope.test.util.CommonUtil.ErrorFlag;

public class Mpeg2TSPlayback extends BaseTests
{
	private int onPrepareCount = 0;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		CommonUtil.resetErrorFlags();
		
		this.onPrepareCount = 0;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	@High
	@Testlink(Id="IF-670", Title="setSource: Correct file path schema of ts file")
	public void testIF_670() throws Exception
	{
		gotoInitializedState(testSignals_TS.get("test_DDP_AV"));
	}
	
	@High
	@Testlink(Id="IF-671", Title="prepare: An existent ts media file")
	public void testIF_671() throws Exception
	{
		gotoPreparedState(testSignals_TS.get("test_DDP_AV"));
	}

	@High
	@Testlink(Id="IF-673", Title="start: Start playback with ts file")
	public void testIF_673() throws Exception
	{
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
	}
	
	@High
	@Testlink(Id="IF-674", Title="stop: Call stop in the playback of ts file")
	public void testIF_674() throws Exception
	{
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		CommonUtil.sleep(6);
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-675", Title="pause: Call pause in ts playback")
	public void testIF_675() throws Exception
	{
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		//Verify the player state should transit to PAUSED state
		Assert.assertEquals(PlayerState.PAUSED, playerWrapper.getState());
		
		playerWrapper.resumePlayback();
		CommonUtil.sleep(2);
		
		//Verify the player state should transit to PLAYING state
		Assert.assertEquals(PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-676", Title="onCompletion: Register an event callback for onCompletion, play the ts file to the end")
	public void testIF_676() throws Exception
	{
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		CommonUtil.sleep(6);
		
		playerWrapper.stopPlayback();
	}
	
	@Medium
	@Testlink(Id="IF-677", Title="onPrepared: Register an event callback for onPrepared, then call prepare for the ts file")	
	public void testIF_677() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		
		gotoPreparedState(testSignals_TS.get("test_DDP_AV"));
		
		Assert.assertEquals("Verify the event onPrepared should be triggered.", 
							1, onPrepareCount);
	}
	
	@High
	@Testlink(Id="IF-678", Title="getPlaybackDuration: Call getPlaybackDuration against the ts file")	
	public void testIF_678() throws Exception
	{
		long expectedDuration = 71000;
		
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		
		Assert.assertEquals("Verify the content duration retrieved by getPlaybackDuration()",
							expectedDuration, playerWrapper.getPlaybackDuration());
	}
	
	@High
	@Testlink(Id="IF-679", Title="getPlaybackPosition: Call getPlaybackPosition against the ts file")	
	public void testIF_679() throws Exception
	{
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		
		long currentPosition = playerWrapper.getPlaybackPosition();
		
		CommonUtil.sleep(5);
		
		//Verify the playback position should be updated
		Assert.assertTrue("Verify the playback position should be updated.", playerWrapper.getPlaybackPosition() > currentPosition);
	}

	@High
	@Testlink(Id="IF-690", Title="seekTo: Call seekTo against the ts file")		
	public void testIF_690() throws Exception
	{
		int forwardSeekPos = 32000;
		int backwardSeekPos = 6000;
		
		gotoPlayingState(testSignals_TS.get("test_DDP_AV"));
		
		//Seek forward
		playerWrapper.seekTo(forwardSeekPos);
		CommonUtil.sleep(1);

		Assert.assertTrue("The player should go to the specified position", playerWrapper.getPlaybackPosition() > forwardSeekPos);
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
	
		CommonUtil.sleep(3);
		
		//Seek backward
		playerWrapper.seekTo(backwardSeekPos);
		CommonUtil.sleep(1);

		Assert.assertTrue("The player should go to the specified position", playerWrapper.getPlaybackPosition() > backwardSeekPos);
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		
		CommonUtil.sleep(3);
	}
	
	Player.PrepareStateListener prepareListener = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			onPrepareCount++;
		}
	};
}
