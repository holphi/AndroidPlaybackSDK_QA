package com.dolby.dope.test;

import junit.framework.Assert;

import com.dolby.infra.Player;
import com.dolby.infra.Player.Error;
import com.dolby.infra.Player.PlayerState;
import com.dolby.infra.Player.Value;

import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Testlink;
import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Low;

import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;

public class DashPlaybackTests extends BaseTests 
{
	private int onErrorCount = 0;
	private int onCompletionCount = 0;
	private int onPrepareCount = 0;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		onErrorCount = 0;
		onCompletionCount = 0;
		onPrepareCount = 0;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	@Medium
	@Testlink(Id="IF-25", Title="setSource(): DASH, A correct URL schema")
	public void testIF_25() throws Exception
	{
		playerWrapper.setSource(testSignals_DASH.get("Basic_DDP_87s"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to initialized state", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertFalse("The IllegalArgumentExcepiton should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="IF-26", Title="setSource(): DASH, Incorrect URL schema")
	public void testIF_26() throws Exception
	{
		String[] incorrect_URLs = new String[]{
		"10.131.102.164/adaptive/DASH/Internal/DDP/OnDemand/Basic_Short_87s/Basic_Short_87s.mpd",
		"ttp://10.131.102.164/adaptive/DASH/Internal/DDP/OnDemand/Basic_Short_87s/Basic_Short_87s.mpd",
		"//10.131.102.164/adaptive/DASH/Internal/DDP/OnDemand/Basic_Short_87s/Basic_Short_87s.mpd",
		"https://10.131.102.164/adaptive/DASH/Internal/DDP/OnDemand/Basic_Short_87s/Basic_Short_87s.mpd",
		"\\\\10.131.102.164\\TestData\\adaptive\\DASH\\Internal\\DDP\\OnDemand\\Basic_Short_87s\\Basic_Short_87s.mpd"
		};
		
		for(int i=0;i<incorrect_URLs.length;i++)
		{
			String url = incorrect_URLs[i];
			
			playerWrapper.setSource(url);
			CommonUtil.sleep(1);

			Assert.assertTrue(String.format("The IllegalArgumentError should be thrown if pass %s to setSource().", url), 
							  CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
			Assert.assertEquals("The player should stay in idle state.", PlayerState.IDLE, playerWrapper.getState());
			
			CommonUtil.resetErrorFlags();
		}
	}
	
	@High
	@Testlink(Id="IF-27", Title="prepare(): DASH, General, An inexistent URL")
	public void testIF_27() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.setSource(testSignals_DASH.get("MPD_Inexistent_URL"));
		
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		
		playerWrapper.prepare();
		
		CommonUtil.sleep(7);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The callback onError should be notified.", 1, onErrorCount);
		Assert.assertEquals("The callback onPrepare should not be notified.", 0, onPrepareCount);
	}
	
	@Low
	@Testlink(Id="IF-236", Title="prepare(): DASH, General, Missing end tag in MPD")
	public void testIF_236() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);

		gotoPlayingState(testSignals_DASH.get("MPD_Missing_EndTag"));
		
		Assert.assertEquals("The player should transit to playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The callback onPrepare should be notified.", 1, onPrepareCount);
	}
	
	@Medium
	@Testlink(Id="IF-237", Title="prepare(): DASH, General, Incorrect file content in MPD")
	public void testIF_237() throws Exception
	{
		playerWrapper.setSource(testSignals_DASH.get("MPD_Incorrect_Content"));
		
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		
		playerWrapper.prepare();
		
		CommonUtil.sleep(3);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The callback onError should be notified.", 1, onErrorCount);
		Assert.assertEquals("The callback onPrepare should not be notified.", 0, onPrepareCount);
	}
	
	@High
	@Testlink(Id="IF-238", Title="prepare(): DASH, OnDemand, Correct URL")
	public void testIF_238() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.rmOnPreparedListener();
		
		String[] correct_URLs = new String[] {
				testSignals_DASH.get("Basic_WildChina"),
				testSignals_DASH.get("Basic_WildChina"),
				testSignals_DASH.get("Basic_WildChina")};
		
		for(int i=0;i<correct_URLs.length;i++)
		{
			String url = correct_URLs[i];
			
			gotoPreparedState(url);

			Assert.assertEquals("The callback onPrepared should be notified.", 1, onPrepareCount);
			Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
			
			playerWrapper.stop();
			
			CommonUtil.sleep(2);
			
			onErrorCount = 0;
			onCompletionCount = 0;
			onPrepareCount = 0;
		}
	}
	
	@Medium
	@Testlink(Id="IF-247", Title="prepare(): DASH, OnDemand, Audio only content")
	public void testIF_247() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.rmOnPreparedListener();

		gotoPreparedState(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		Assert.assertEquals("The player should transit to prepared state.", PlayerState.PREPARED, playerWrapper.getState());
		Assert.assertEquals("The callback onPrepared should be notified.", 1, onPrepareCount);
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
	}
	
	@Low
	@Testlink(Id="IF-248", Title="prepare(): DASH, OnDemand, Video only content")
	public void testIF_248() throws Exception
	{
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.setSource(testSignals_DASH.get("Basic_VideoOnly"));
		
		playerWrapper.prepare();
		CommonUtil.sleep(6);
		
		Assert.assertEquals("The player should transit to prepared state.", PlayerState.PREPARED, playerWrapper.getState());
		Assert.assertEquals("The callback onPrepared should be notified.", 1, onPrepareCount);
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
	}
	
	@Low
	@Testlink(Id="IF-239", Title="prepare(): DASH, OnDemand, Audio data file missing")
	public void testIF_239() throws Exception
	{
		//According to bug DOPE-398
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
	
		gotoPlayingState(testSignals_DASH.get("Basic_Missing_Audio_File"));
		
		Assert.assertEquals("The player should transit to playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The callback onPrepare should be notified.", 1, onPrepareCount);
	}
	
	@Low
	@Testlink(Id="IF-240", Title="prepare(): DASH, OnDemand, Video data file missing")
	public void testIF_240() throws Exception
	{		
		//According to bug DOPE-398
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
	
		gotoPlayingState(testSignals_DASH.get("Basic_Missing_Video_File"));

		Assert.assertEquals("The player should transit to playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The callback onPrepare should be notified.", 1, onPrepareCount);
	}
	
	@Low
	@Testlink(Id="IF-241", Title="prepare(): DASH, OnDemand, Video and audio data file missing")
	public void testIF_241() throws Exception
	{
		//According to bug DOPE-398
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
	
		gotoPlayingState(testSignals_DASH.get("Basic_Missing_BothAV"));
		
		Assert.assertEquals("The player should transit to playing state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The callback onPrepare should be notified.", 1, onPrepareCount);
	}
	
	@Low
	@Testlink(Id="IF-242", Title="prepare(): DASH, OnDemand, Corrupted audio container")
	public void testIF_242() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.setSource(testSignals_DASH.get("Basic_Corrupted_Audio_Container"));
		
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		
		playerWrapper.prepare();
		
		CommonUtil.sleep(6);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The callback onError should be notified.", 1, onErrorCount);
		Assert.assertEquals("The callback onPrepare should not be notified.", 0, onPrepareCount);
	}
	
	@Low
	@Testlink(Id="IF-243", Title="prepare(): DASH, OnDemand, Corrupted video container")
	public void testIF_243() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		playerWrapper.setSource(testSignals_DASH.get("Basic_Corrputed_Video_Container"));
		
		playerWrapper.addPrepareStateListener(prepareListener);
		playerWrapper.addErrorStateListener(errorListener);
		
		playerWrapper.prepare();
		
		CommonUtil.sleep(5);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The callback onError should be notified.", 1, onErrorCount);
		Assert.assertEquals("The callback onPrepare should not be notified.", 0, onPrepareCount);
	}
	
	@High
	@Testlink(Id="IF-249", Title="start: DASH, OnDemand, Call start in prepared state")
	public void testIF_249() throws Exception
	{
		//Player.start() is called internally.
		playerWrapper.addErrorStateListener(errorListener);

		gotoPlayingState(testSignals_DASH.get("Basic_DDP_87s"));
		
		Assert.assertEquals("The player should transit to playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
	}
	
	@Medium
	@Testlink(Id="IF-250", Title="start: DASH, OnDemand, Call start in paused state")
	public void testIF_250() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		gotoPausedState(testSignals_DASH.get("Basic_DDP_87s"));
		
		//Player.start is called internally.
		playerWrapper.resumePlayback();
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The player should transit to playing state from paused state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
	}
	
	@Medium
	@Testlink(Id="IF-251", Title="start: DASH, OnDemand, Call start during media playback")
	public void testIF_251() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		gotoPlayingState(testSignals_DASH.get("Basic_DDP_87s"));
		
		//Player.start is called internally.
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in PLAYING state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
	}
	
	@Low
	@Testlink(Id="IF-252", Title="start: DASH, OnDemand, Play the content to the end")
	public void testIF_252() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);
		playerWrapper.addErrorStateListener(errorListener);
		gotoPlayingState(testSignals_DASH.get("Basic_DDP_87s"));
		
		CommonUtil.sleep(90);
		
		Assert.assertEquals("The player should be in STOPPED state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The callback onCompletion shoud be notified", 1, onCompletionCount);
	}
	
	@High
	@Testlink(Id="IF-253", Title="pause: DASH, OnDemand, Call pause in playing state")
	public void testIF_253() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_DDP_87s"));
		CommonUtil.sleep(3);
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in PAUSED state", PlayerState.PAUSED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-254", Title="pause: DASH, OnDemand, Call pause in paused state")
	public void testIF_254() throws Exception
	{
		gotoPausedState(testSignals_DASH.get("Basic_DDP_87s"));
		
		playerWrapper.pause();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in PAUSED state", PlayerState.PAUSED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-255", Title="pause: DASH, OnDemand, Call pause and start in sequence for several times")
	public void testIF_255() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_ED_480x320"));	
		CommonUtil.sleep(5);
		
		for(int i=0;i<30;i++)
		{
			playerWrapper.pausePlayback();
			CommonUtil.sleep(2);
			Assert.assertEquals("The player should transit to paused state.", PlayerState.PAUSED, playerWrapper.getState());
			
			playerWrapper.resumePlayback();
			CommonUtil.sleep(2);
			Assert.assertEquals("The player should transit to playing state.",  PlayerState.PLAYING, playerWrapper.getState());
		}
	}
	
	@High
	@Testlink(Id="IF-256", Title="stop: DASH, OnDemand, Call stop in playing state;")
	public void testIF_256() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		CommonUtil.sleep(3);
		
		//Stop is called internally
		playerWrapper.stopPlayback();
		
		waitForPlayerState(PlayerState.STOPPED, 6);
	}
	
	@Medium
	@Testlink(Id="IF-257", Title="stop: DASH, OnDemand, Call stop in paused state")
	public void testIF_257() throws Exception
	{
		gotoPausedState(testSignals_DASH.get("Basic_ED_480x320"));
		CommonUtil.sleep(3);
		
		//Stop is called internally
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to stopped state.",  PlayerState.STOPPED, playerWrapper.getState());
	}
	
	@Low
	@Testlink(Id="IF-258", Title="stop: DASH, OnDemand, Call stop in prepared state;")
	public void testIF_258() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		gotoPreparedState(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		playerWrapper.stop();
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The player should transit to stopped state.",  PlayerState.STOPPED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-259", Title="stop: DASH, OnDemand, Play a new dash content after finishing previous playback")
	public void testIF_259() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		waitForPlayerState(PlayerState.STOPPED, 15);
		
		gotoPlayingState(testSignals_DASH.get("Basic_DDP_87s"));
	}
	
	@Low
	@Testlink(Id="IF-260", Title="start(): DASH, OnDemand, Long content url")
	public void testIF_260() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);
		playerWrapper.addErrorStateListener(errorListener);

		gotoPlayingState(testSignals_DASH.get("Basic_LongURL"));
		
		Assert.assertEquals("The callback onError should not be notified.", 0, onErrorCount);
		Assert.assertEquals("The player should be in PLAYING state.", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	Player.ErrorStateListener errorListener = new Player.ErrorStateListener() {
		
		@Override
		public void onError(Player player, Error error) {
			onErrorCount++;
		}
	};
	
	Player.PrepareStateListener prepareListener = new Player.PrepareStateListener() {
		
		@Override
		public void onPrepared(Player player) 
		{
			onPrepareCount++;
		}
	};
	
	Player.CompletionListener completionListener = new Player.CompletionListener() {
		
		@Override
		public void onCompletion(Player player) {
			onCompletionCount++;
		}
	};

}
