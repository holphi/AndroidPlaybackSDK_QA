package com.dolby.dope.test;

import junit.framework.Assert;

import android.util.Log;

import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Testlink;
import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Low;

import com.dolby.infra.Player;
import com.dolby.infra.Player.Error;
import com.dolby.dope.test.util.CommonUtil;

public class EventHandlingTests extends BaseTests
{
	//An internal flag to record the count number of the event onPrepared;
	private int onPreparedCount = 0;
	//An internal flag to record the count number of the event onCompletion;
	private int onCompletionCount = 0;
	//An internal flag to record the count number of the event onError;
	private int onErrorCount = 0;
	//An internal flag to record the count number of the event onStreamChanged;
	private int onStreamChangedCount = 0;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.onPreparedCount = 0;
		this.onCompletionCount = 0;
		this.onErrorCount = 0;
		this.onStreamChangedCount = 0;

		CommonUtil.resetErrorFlags();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	@High
	@Testlink(Id="IF-46", Title="onPrepared: Register an event callback for onPrepare")
	public void testIF_46() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));
		playerWrapper.prepare();
		
		//Add sleep statement to guarantee that the internal variable is overwritten by the event callback;
		CommonUtil.sleep(2);
		Assert.assertEquals("The event onPrepared should be notified", 1, onPreparedCount);
	}
	
	@Medium
	@Testlink(Id="IF-47", Title="onPrepared: Register multiple event callbacks for onPrepare")
	public void testIF_47() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.prepare();
		
		//Add sleep statement to guarantee that the internal variable is overwritten by the event callback;
		CommonUtil.sleep(2);
		Assert.assertEquals("The event onPrepared should be notified", 2, onPreparedCount);
	}
	
	@High
	@Testlink(Id="IF-48", Title="onPrepared: Remove the event callback for onPrepare")
	public void testIF_48() throws Exception
	{

		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));
		playerWrapper.removePrepareStateListener(prepareListener);
		
		playerWrapper.prepare();
		
		//Add sleep statement to guarantee that the internal variable is overwritten by the event callback;
		CommonUtil.sleep(2);
		Assert.assertEquals("The event onPrepared should be notified", 0, onPreparedCount);
	}
	
	@Medium
	@Testlink(Id="IF-49", Title="onPrepared: Prepare failed")
	public void testIF_49() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("inexistent_file"));
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.prepare();
		
		//Add sleep statement to guarantee that the internal variable is overwritten by the event callback;
		CommonUtil.sleep(2);
		Assert.assertEquals("The event onPrepared should not be notified", 0, onPreparedCount);
	}
	
	@Low
	@Testlink(Id="IF-50", Title="onPrepared: Pass an null object to addPrepareListener")
	public void testIF_50() throws Exception
	{
		playerWrapper.addPrepareStateListener(null);
	}
	
	@Low
	@Testlink(Id="IF-51", Title="onPrepared: Pass an null object to removePrepareListener")
	public void testIF_51() throws Exception
	{
		playerWrapper.removePrepareStateListener(null);
	}
	
	@High
	@Testlink(Id="IF-88", Title="onCompletion:Register an event callback for onCompletion")
	public void testIF_88() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);

		playerWrapper.startPlayback(testSignals_MP4.get("Audio_only_ddp_30s.mp4"));
		CommonUtil.sleep(50);
		
		Assert.assertEquals("The event onCompletion should be notified", 1 , onCompletionCount);
	}
	
	@Medium
	@Testlink(Id="IF-89", Title="onCompletion:Register multiple event callbacks for onCompletion")
	public void testIF_89() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);
		playerWrapper.addCompletionListener(completionListener);
		
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		CommonUtil.sleep(20);
		
		Assert.assertEquals("The event onCompletion should be notified", 2, onCompletionCount);
	}
	
	@High
	@Testlink(Id="IF-90", Title="onCompletion:Remove event callbacks for onCompletion")
	public void testIF_90() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);
		playerWrapper.removeCompletionListener(completionListener);
		
		playerWrapper.startPlayback(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The event onCompletion should not be notified", 0, onCompletionCount);
	}
	
	@Low
	@Testlink(Id="IF-91", Title="onCompletion:Pass an null object to addCompletionListener")
	public void testIF_91() throws Exception
	{
		playerWrapper.addCompletionListener(null);
	}
	
	@Low
	@Testlink(Id="IF-92", Title="onCompletion:Pass an null object to removeCompletionListener")
	public void testIF_92() throws Exception
	{
		playerWrapper.removeCompletionListener(null);
	}
	
	/*@Low
	@Testlink(Id="IF-93", Title="onCompletion:Verify it in looping mode")
	public void testIF_93() throws Exception
	{
		throw new Exception("It should be implemented and tested in Sprint 5");
	}*/
	
	@High
	@Testlink(Id="IF-99", Title="onError: Register an event callback for onError")
	public void testIF_99() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		
		gotoErrorState();
		
		Assert.assertEquals("The event onError should be notified.", 1, onErrorCount);
	}
	
	@Medium
	@Testlink(Id="IF-100", Title="onError: Register multiple event callbacks for onError")
	public void testIF_100() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.addErrorStateListener(errorListener);
		
		gotoErrorState();
		
		Assert.assertEquals("The event onError should be notified.", 2, onErrorCount);
	}
	
	@High
	@Testlink(Id="IF-101", Title="onError: Remove the event callback for onError")
	public void testIF_101() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.removeErrorStateListener(errorListener);
		
		gotoErrorState();
		
		Assert.assertEquals("The event onError should be notified.", 0, onErrorCount);
	}
	
	@Low
	@Testlink(Id="IF-102", Title="onError: Pass an null object to addErrorListener")
	public void testIF_102() throws Exception
	{
		playerWrapper.addErrorStateListener(null);
	}
	
	@Low
	@Testlink(Id="IF-103", Title="onError: Pass an null object to removeErrorListener")
	public void testIF_103() throws Exception
	{
		playerWrapper.removeErrorStateListener(null);
	}
	
	@Low
	@Testlink(Id="IF-105", Title="onError: Pass an unregistered method callback to removeErrorCallback")
	public void testIF_105() throws Exception
	{
		playerWrapper.removeErrorStateListener(errorListener);
	}
	
	@Low
	@Testlink(Id="IF-106", Title="onPrepare: Pass an unregistered method to removePrepareCallback")
	public void testIF_106() throws Exception
	{
		playerWrapper.removePrepareStateListener(prepareListener);
	}
	
	@Low
	@Testlink(Id="IF-107", Title="onCompletion: Pass an unregistered method callback to removeCompletionCallback")
	public void testIF_107() throws Exception
	{
		playerWrapper.removeCompletionListener(completionListener);
	}
	
	@High
	@Testlink(Id="IF-189", Title="onStreamChanged Register an event callback for onStreamChanged")
	public void testIF_189() throws Exception
	{
		playerWrapper.addStreamChangedListener(streamChangedListener);
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));	
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The event onStreamChanged should be notified.", 2, onStreamChangedCount);
	}
	
	@Medium
	@Testlink(Id="IF-190", Title="onStreamChanged: Register multiple event callback for onStreamChanged")
	public void testIF_190() throws Exception
	{
		playerWrapper.addStreamChangedListener(streamChangedListener);
		playerWrapper.addStreamChangedListener(streamChangedListener);
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));	
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The event onStreamChanged should be notified.", 4, onStreamChangedCount);
	}
	
	@High
	@Testlink(Id="IF-191", Title="onStreamChanged: Remove the event callback for onStreamChanged")
	public void testIF_191() throws Exception
	{
		playerWrapper.addStreamChangedListener(streamChangedListener);
		playerWrapper.removeStreamChangedListener(streamChangedListener);
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));	
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The event onStreamChanged should NOT be notified.", 0, onStreamChangedCount);
	}
	
	@Low
	@Testlink(Id="IF-192", Title="onStreamChanged: Pass an null object to addStreamChangedListener")
	public void testIF_192() throws Exception
	{
		playerWrapper.addStreamChangedListener(null);
	}
	
	@Low
	@Testlink(Id="IF-193", Title="onStreamChanged: Pass an null object to removeStreamChangedListener")
	public void testIF_193() throws Exception
	{
		playerWrapper.removeCompletionListener(null);
	}
	
	@Low
	@Testlink(Id="IF-194", Title="onStreamChanged: Pass an unregistered method callback to removeStreamChangedListener")
	public void testIF_194() throws Exception
	{
		playerWrapper.removeStreamChangedListener(streamChangedListener);
	}
	
	Player.PrepareStateListener prepareListener = new Player.PrepareStateListener() {
		
		@Override
		public void onPrepared(Player player)
		{
			onPreparedCount++;
		}
	};
	
	Player.ErrorStateListener errorListener = new Player.ErrorStateListener() {
		@Override
		public void onError(Player player, Error error) {
			onErrorCount++;
		}
	};
	
	Player.StreamChangedListener streamChangedListener = new Player.StreamChangedListener() {
		
		@Override
		public void onStreamChanged(Player player, int trackIdx, int streamIdx) {
			onStreamChangedCount++;
		}
	};
	
	Player.CompletionListener completionListener = new Player.CompletionListener() 
	{
		@Override
		public void onCompletion(Player player) 
		{
			Log.i(TAG, "The event callback onCompletion is triggered.");
			onCompletionCount++;
		}
	};
}
