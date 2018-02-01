package com.foo.dope.test;

import junit.framework.Assert;

import android.util.Log;

import com.foo.infra.Player;
import com.foo.infra.Player.Error;
import com.foo.infra.Player.IllegalArgumentException;
import com.foo.infra.Player.IllegalStateException;
import com.foo.infra.Player.StreamChangedListener;
import com.foo.infra.Player.PlayerState;
import com.foo.infra.Player.MediaInfo;

import com.foo.dope.test.util.CommonUtil;
import com.foo.dope.test.util.CommonUtil.ErrorFlag;
import com.foo.test.annotation.Low;
import com.foo.test.annotation.Medium;
import com.foo.test.annotation.High;
import com.foo.test.annotation.Testlink;

public class AudioBitrateSelTests extends BaseTests 
{
	//An internal flag to record the count number of the event onError;
	private int onErrorCount;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.addStreamChangedListener(streamChangedListener);
		
		CommonUtil.resetErrorFlags();
		
		onErrorCount = 0;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	Player.ErrorStateListener errorListener = new Player.ErrorStateListener() {
		@Override
		public void onError(Player player, Error error) {
			onErrorCount++;
		}
	};
	
	@High
	@Testlink(Id="IF-480", Title="In playing state, call selStream to choose the lower bitrate audio stream")
	public void testIF_480() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		verifyCurrentAudioStreamBW(192000);

		int trackId = 1, streamId = 1;
		
		playerWrapper.switchStream(1, 1);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		//verifyMsgEventCallbackForAudioBitrateChange(2);
		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(128000);
		
		trackId = 1; 
		streamId = 2;
		
		playerWrapper.switchStream(1, 2);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		verifyStreamChangedEventCallback(3, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
	}
		
	@Medium
	@Testlink(Id="IF-481", Title="In playing state, call selStream to choose the lowest bitrate audio stream")
	public void testIF_481() throws Exception
	{
		int trackId = 1, streamId=2;
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		verifyCurrentAudioStreamBW(192000);

		playerWrapper.switchStream(trackId, streamId);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));

		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Medium
	@Testlink(Id="IF-482", Title="In playing state, call selStream with the same params again after the first call has been invoked")
	public void testIF_482() throws Exception
	{
		int trackId = 1, streamId = 2;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		verifyCurrentAudioStreamBW(192000);

		playerWrapper.switchStream(trackId, streamId);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
		
		//The selectStream() function shall do nothing if the given stream is already selected. The return value shall be true in this case.
		playerWrapper.switchStream(trackId, streamId);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Low
	@Testlink(Id="IF-483", Title="In playing state, call selStream twice with different stream ID")
	public void testIF_483() throws Exception
	{
		int trackId = 1, streamId = 2;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		verifyCurrentAudioStreamBW(192000);

		playerWrapper.switchStream(1, 1);
		playerWrapper.switchStream(1, 2);

		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Medium
	@Testlink(Id="IF-484", Title="In playing state, call selStream with the out-of-range stream id")
	public void testIF_484() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_128k"));
		
		verifyCurrentAudioStreamBW(128000);
		
		playerWrapper.switchStream(1, 1);
		
		Assert.assertTrue("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(128000);
	}
	
	@Medium
	@Testlink(Id="IF-485", Title="In playing state, call selStream with the out-of-range track id")
	public void testIF_485() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_128k"));
		
		verifyCurrentAudioStreamBW(128000);
		
		playerWrapper.switchStream(2, 1);

		Assert.assertTrue("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(128000);
	}
	
	@Medium
	@Testlink(Id="IF-486", Title="In playing state, call selStream with the track id that is not selected yet")
	public void testIF_486() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_MultiAudioTrack"));
		
		verifyCurrentAudioStreamBW(192000);
		
		playerWrapper.switchStream(2, 0);

		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(192000);
	}
	
	@Medium
	@Testlink(Id="IF-487", Title="In paused state, call selStream to choose the lower bitrate audio stream")
	public void testIF_487() throws Exception
	{
		int trackId = 1, streamId = 2;
		gotoPausedState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		verifyCurrentAudioStreamBW(192000);
		
		playerWrapper.switchStream(trackId, streamId);

		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		playerWrapper.resumePlayback();
		
		verifyStreamChangedEventCallback(2, 1, 2);
		
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Medium
	@Testlink(Id="IF-488", Title="In paused state, call selStream twice with difference stream id")
	public void testIF_488() throws Exception
	{
		int trackId = 1, streamId = 1;
		gotoPausedState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		verifyCurrentAudioStreamBW(192000);
		
		playerWrapper.switchStream(trackId, streamId);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		CommonUtil.sleep(10);
		
		streamId = 2;
		
		playerWrapper.switchStream(trackId, streamId);
		Assert.assertFalse("An IllegalArgumentException exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		
		playerWrapper.resumePlayback();
		
		verifyStreamChangedEventCallback(2, trackId, streamId);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Low
	@Testlink(Id="IF-489", Title="In paused state, call selStream with an out-of-range stream id")
	public void testIF_489() throws Exception
	{
		gotoPausedState(testSignals_DASH.get("Basic_Bunny_128k"));
		
		verifyCurrentAudioStreamBW(128000);
		
		playerWrapper.switchStream(1, 1);

		Assert.assertTrue("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
	
		playerWrapper.resumePlayback();
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(128000);
	}
	
	@Low
	@Testlink(Id="IF-490", Title="In paused state, call selStream with the stream id which is being played")
	public void testIF_490() throws Exception
	{
		gotoPausedState(testSignals_DASH.get("Basic_Bunny_128k"));
		
		verifyCurrentAudioStreamBW(128000);
		
		playerWrapper.switchStream(1, 0);
		
		//The selectStream() function shall do nothing if the given stream is already selected. The return value shall be true in this case
		Assert.assertFalse("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
	
		playerWrapper.resumePlayback();
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(128000);
	}
	
	@Medium
	@Testlink(Id="IF-491", Title="In prepared state, call selStream to choose the lower bitrate audio stream")
	public void testIF_491() throws Exception
	{
		//Add an event listener to choose the lower bitrate audio stream in PREPARED state;
		playerWrapper.addPrepareStateListener(new Player.PrepareStateListener() 
		{
			@Override
			public void onPrepared(Player player) {
				try 
				{
					player.switchStream(1, 1);
				} catch (IllegalArgumentException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
				} catch (IllegalStateException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
				}
			}
		});
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));

		Assert.assertFalse("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertFalse("The IllegalStateException exception should NOT be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 1);
		verifyCurrentAudioStreamBW(128000);
	}
	
	@Low
	@Testlink(Id="IF-492", Title="In prepared state, call selStream twice with different stream ID")	
	public void testIF_492() throws Exception
	{
		//Add an event listener to change audio stream in PREPARED state;
		playerWrapper.addPrepareStateListener(new Player.PrepareStateListener() 
		{
			@Override
			public void onPrepared(Player player) {
				try 
				{
					player.switchStream(1, 1);
					player.switchStream(1, 2);
				} catch (IllegalArgumentException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
				} catch (IllegalStateException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
				}
			}
		});
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));

		Assert.assertFalse("An IllegalArgumentException exception should NOT be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertFalse("The IllegalStateException exception should NOT be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 2);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Medium
	@Testlink(Id="IF-493", Title="In prepared state, call selStream with an out-of-range stream id")	
	public void testIF_493() throws Exception
	{
		//Add an event listener to change audio stream in PREPARED state;
		playerWrapper.addPrepareStateListener(new Player.PrepareStateListener() 
		{
			@Override
			public void onPrepared(Player player) {
				try 
				{
					player.switchStream(1, 1);
				} catch (IllegalArgumentException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
				} catch (IllegalStateException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
				}
			}
		});
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_96k"));
		
		Assert.assertTrue("An IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertFalse("The IllegalStateException exception should NOT be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 0);
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Medium
	@Testlink(Id="IF-494", Title="In prepared state, call selStream with an out-of-range track id")	
	public void testIF_494() throws Exception
	{
		//Add an event listener to change audio stream in PREPARED state;
		playerWrapper.addPrepareStateListener(new Player.PrepareStateListener() 
		{
			@Override
			public void onPrepared(Player player) {
				try 
				{
					player.switchStream(2, 1);
				} catch (IllegalArgumentException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
				} catch (IllegalStateException e) {
					CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
				}
			}
		});
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_96k"));
		
		Assert.assertTrue("The IllegalArgumentException exception should be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertFalse("The IllegalStateException exception should NOT be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
		
		verifyStreamChangedEventCallback(1, 1, 0);
		
		verifyCurrentAudioStreamBW(96000);
	}
	
	@Low
	@Testlink(Id="IF-498", Title="In playback, call selStream continuously with correct stream ID")		
	public void testIF_498() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		for(int i=1;i<=50;i++)
		{
			int streamId = i%3;
			
			playerWrapper.switchStream(1, streamId);
			
			int expectedAudioBW = 0;
			
			switch(streamId)
			{
				case 0:
					expectedAudioBW = 192000;
					break;
				case 1:
					expectedAudioBW = 128000;
					break;
				default:
					expectedAudioBW = 96000;
			}
			
			Assert.assertEquals("The event onError should not be triggered", 0, onErrorCount);
			
			verifyStreamChangedEventCallback(i+1, 1, streamId);
			verifyCurrentAudioStreamBW(expectedAudioBW);
		}
	}
}
