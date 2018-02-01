package com.foo.dope.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class BasicPlaybackTests extends BaseTests
{
	private int onErrorCount = 0;
	private int onCompletionCount = 0;
	private int onPrepareCount = 0;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		CommonUtil.resetErrorFlags();
		
		onErrorCount = 0;
		onCompletionCount = 0;
		onPrepareCount = 0;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
		
	@High
	@Testlink(Id="IF-13", Title="create()")
	public void testIF_13() throws Exception
	{
		playerWrapper.testCreate();
		
		Assert.assertEquals("The player should transit to idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertFalse("An InstantaiontError should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INSTANTIATION_ERROR_FLAG));
	}
	
	/*
	 * Disable IF-14 & IF-16 as the method create(String url) is not supported by IF v1.0
	 * 
	 * @High 
	@Testlink(Id="IF-14", Title="create(String url)")
	public void testIF_14() throws Exception
	{
		playerWrapper.testCreateWithParams(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		CommonUtil.sleep(3);
		Assert.assertEquals("The player should transit to initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
		Assert.assertFalse("An InstantaiontError should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INSTANTIATION_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-16", Title="create(String url): Exception handling")
	public void testIF_16() throws Exception
	{
		//Create player instance with a local file path missing file prefix.
		playerWrapper.testCreateWithParams(testSignals_MP4.get("missing_file_prefix"));
		
		//Add a sleep statement forcibly to ensure the test thread receive InstantiationError
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should be in idle state.", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An InstantaiontError should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INSTANTIATION_ERROR_FLAG));
	}*/
	
	@High
	@Testlink(Id="IF-17", Title="setSource(): Correct file path schema")
	public void testIF_17() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		CommonUtil.sleep(1);
		
		Assert.assertFalse("The IllegalArgumentError should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should transit to initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
	}
	
	@High
	@Testlink(Id="IF-18", Title="setSource(): Incorrect file path schema")
	public void testIF_18() throws Exception
	{			
		playerWrapper.setSource(testSignals_MP4.get("missing_file_prefix"));
		
		//Add a sleep statement forcibly to ensure the test thread receive IllegalArgumentError
		CommonUtil.sleep(2);
		
		Assert.assertTrue("An IllegalArgumentError should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should stay in idle state.", PlayerState.IDLE, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-234", Title="setSource(): Pass an null object")
	public void testIF_234() throws Exception
	{			
		playerWrapper.setSource(null);
		
		//Add a sleep statement forcibly to ensure the test thread receive IllegalArgumentError
		CommonUtil.sleep(2);
		
		Assert.assertTrue("An IllegalArgumentError should be thrown", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should stay in idle state.", PlayerState.IDLE, playerWrapper.getState());
	}
	
	@High
	@Testlink(Id="IF-19", Title="setSource(): A correct streaming URL schema with \"http\" prefix")
	public void testIF_19() throws Exception
	{
		playerWrapper.setSource("http://10.10.110.10/streams/test.m3u8");
		
		CommonUtil.sleep(2);
		Assert.assertFalse("The IllegalArgumentError should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should transit to initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-20", Title="setSource(): A correct streaming URL schema with \"https\" prefix")
	public void testIF_20() throws Exception
	{
		playerWrapper.setSource("https://10.10.110.10/streams/test.m3u8");
		
		CommonUtil.sleep(2);
		Assert.assertTrue("The IllegalArgumentError should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should stay in idle state.", PlayerState.IDLE, playerWrapper.getState());
	}
	
	@High
	@Testlink(Id="IF-21", Title="setSource(): Incorrect streaming URL schema")
	public void testIF_21() throws Exception
	{
		playerWrapper.setSource("10.10.110.10/streams/test.m3u8");
		
		CommonUtil.sleep(2);
		Assert.assertTrue("The IllegalArgumentError should not be thrown.", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));
		Assert.assertEquals("The player should stay in idle state.", PlayerState.IDLE, playerWrapper.getState());
	}
	
	@High
	@Testlink(Id="IF-22", Title="prepare(): An existent local media file")
	public void testIF_22() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		CommonUtil.sleep(1);
		
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));	
		playerWrapper.prepare();
		
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should transit to prepared state.", PlayerState.PREPARED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-188", Title="prepare(): A MP4 file with corrupted mp4 container")
	public void testIF_188() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.addPrepareStateListener(prepareListener);
		
		//playerWrapper.setSource(testSignals_MP4.get("inexistent_file"));
		playerWrapper.setSource(testSignals_MP4.get("Corrupt_container_ddp.mp4"));
		playerWrapper.prepare();
		
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The onError event should be notified.", 1, onErrorCount);
		Assert.assertEquals("The onPrepared event should not be notified.", 0, onPrepareCount);
	}
	
	@Medium
	@Testlink(Id="IF-23", Title="prepare(): An inexistent local media path")
	public void testIF_23() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.addPrepareStateListener(prepareListener);

		playerWrapper.setSource(testSignals_MP4.get("inexistent_file"));
		playerWrapper.prepare();
		
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("The onError event should be notified.", 1, onErrorCount);
		Assert.assertEquals("The onPrepared event should not be notified.", 0, onPrepareCount);
	}

	@Medium
	@Testlink(Id="IF-24", Title="prepare(): An unsupport local file path")
	public void testIF_24() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("unsupport_file"));
		playerWrapper.addErrorStateListener(errorListener);
		playerWrapper.addPrepareStateListener(prepareListener);
		
		playerWrapper.prepare();
		CommonUtil.sleep(5);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		Assert.assertEquals("An onError event should be notified.", 1, onErrorCount);
		Assert.assertEquals("The onPrepared event should not be notified.", 0, onPrepareCount);
	}
		
	@Medium
	@Testlink(Id="IF-29", Title="start(): Call start in prepared state")
	public void testIF_29() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		Assert.assertEquals("The player state should be playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-57", Title="start(): Call start in paused state")
	public void testIF_57() throws Exception
	{
		gotoPausedState();
		
		playerWrapper.resumePlayback();
		CommonUtil.sleep(3);
		
		Assert.assertEquals("The player state be playing state again.", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-58", Title="start(): Call start again during media playback;")
	public void testIF_58() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("trim_ac4_13s.mp4"));
		playerWrapper.start();
		Assert.assertEquals("The player state should be playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-60", Title="start(): Start playback on an ill-format local media file")
	public void testIF_60() throws Exception
	{
		gotoInitializedState(testSignals_MP4.get("Corrupt_container_ddp.mp4"));
		
		playerWrapper.prepare();
		CommonUtil.sleep(2);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
		
		playerWrapper.start();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to error state.", PlayerState.ERROR, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-104", Title="start(): Play the content to the end")
	public void testIF_104() throws Exception
	{
		playerWrapper.addCompletionListener(completionListener);
		
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		CommonUtil.sleep(15);
		
		Assert.assertEquals("The player state should be stopped after playing the content to the end.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertEquals("onCompletion should be notified.", 1, onCompletionCount);
	}
	
	@Medium
	@Testlink(Id="IF-108", Title="start: Call start in paused state")
	public void testIF_108() throws Exception
	{
		gotoPausedState();
		
		playerWrapper.resumePlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The playback should be resumed", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-109", Title="pause: Call pause in playing state")
	public void testIF_109() throws Exception
	{
		gotoPlayingState();
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to paused state", PlayerState.PAUSED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-110", Title="pause: Call pause in paused state")
	public void testIF_110() throws Exception
	{
		gotoPausedState();
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should keep in paused state.", PlayerState.PAUSED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-111", Title="pause: Call pause and start in sequence for several times")
	public void testIF_111() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("slient_ac4_51.mp4"));
		
		for(int i=0;i<20;i++)
		{
			playerWrapper.pausePlayback();
			CommonUtil.sleep(1);
			Assert.assertEquals("The player should transit to paused state.", PlayerState.PAUSED, playerWrapper.getState());
			
			playerWrapper.resumePlayback();
			CommonUtil.sleep(1);
			Assert.assertEquals("The player should transit to playing state.",  PlayerState.PLAYING, playerWrapper.getState());
		}
	}
	
	@Medium
	@Testlink(Id="IF-122", Title="setSource: Call setSource in error state")
	public void testIF_122() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.setSource(testSignals_MP4.get("slient_ac4_51.mp4"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit back to initialized state.", PlayerState.INITIALIZED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-123", Title="setSource: Call setSource with an incorrect file path in error state")
	public void testIF_123() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.setSource(testSignals_MP4.get("missing_file_prefix"));
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should keep in error state.", PlayerState.ERROR, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-124", Title="stop: Call stop in prepared state")
	public void testIF_124() throws Exception
	{
		gotoPreparedState();
		
		playerWrapper.stop();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to stopped state", PlayerState.STOPPED, playerWrapper.getState());
		
		//The player can still be initialized and prepared from stopped state.
		gotoPreparedState();
	}
	
	@High
	@Testlink(Id="IF-125", Title="stop: Call stop in playing state")
	public void testIF_125() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));

		playerWrapper.stopPlayback();
		
		waitForPlayerState(PlayerState.STOPPED, 20);
		
		//The player can still be initialized and prepared from stopped state.
		gotoPreparedState();
	}
	
	@High
	@Testlink(Id="IF-126", Title="stop: Call stop in paused state")
	public void testIF_126() throws Exception
	{
		gotoPausedState(testSignals_MP4.get("flight_ddp_13s.mp4"));

		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should transit to stopped state", PlayerState.STOPPED, playerWrapper.getState());
		
		//The player can still be initialized and prepared from stopped state.
		gotoPreparedState();
	}
	
	@Low
	@Testlink(Id="IF-127", Title="stop: Call stop in stopped state")
	public void testIF_127() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("flight_ddp_13s.mp4"));

		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should transit to stopped state", PlayerState.STOPPED, playerWrapper.getState());
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		Assert.assertEquals("The player should transit to stopped state", PlayerState.STOPPED, playerWrapper.getState());
	}
	
	@Medium
	@Testlink(Id="IF-128", Title="stop: Play a new media file after finishing playback")
	public void testIF_128() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		waitForPlayerState(PlayerState.STOPPED, 30);
		
		gotoPlayingState(testSignals_MP4.get("Audio_only_ddp_30s.mp4"));
		waitForPlayerState(PlayerState.STOPPED, 40);
	}
	
	@Medium
	@Testlink(Id="IF-130", Title="Call stop in idle state")
	public void testIF_130() throws Exception
	{
		playerWrapper.stop();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in idle state", PlayerState.IDLE, playerWrapper.getState());
		Assert.assertTrue("An IllegalStateError exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-131", Title="Call stop in initialized state")
	public void testIF_131() throws Exception
	{
		gotoInitializedState();
		
		playerWrapper.stop();
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in stopped state", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertFalse("An IllegalStateError exception should not be thrown." , CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="IF-202", Title="release: Call release in stopped state")
	public void testIF_202() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(15);
		
		playerWrapper.release();
		CommonUtil.sleep(1);
		
		//The player should not crash
	}

	@Medium
	@Testlink(Id="IF-203", Title="release: Call release in idle state")
	public void testIF_203() throws Exception
	{
		
		playerWrapper.release();
		CommonUtil.sleep(1);
		
		//The player should not crash
	}
	
	@Medium
	@Testlink(Id="IF-204", Title="release: Call release in initialized state")
	public void testIF_204() throws Exception
	{
		gotoInitializedState();

		playerWrapper.release();
		CommonUtil.sleep(1);

	}
	
	@Medium
	@Testlink(Id="IF-205", Title="release: Call release in prepared state")
	public void testIF_205() throws Exception
	{
		gotoPreparedState();
		
		playerWrapper.release();
		CommonUtil.sleep(1);

	}
	
	@Medium
	@Testlink(Id="IF-206", Title="release: Call release in playing state")
	public void testIF_206() throws Exception
	{
		gotoPlayingState();
		
		playerWrapper.release();
		CommonUtil.sleep(1);

	}
	
	@Medium
	@Testlink(Id="IF-207", Title="release: Call release in paused state")
	public void testIF_207() throws Exception
	{
		gotoPausedState(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		playerWrapper.release();
		CommonUtil.sleep(1);

	}
	
	@Medium
	@Testlink(Id="IF-208", Title="release: Call release in error state")
	public void testIF_208() throws Exception
	{
		gotoErrorState();
		
		playerWrapper.release();
		CommonUtil.sleep(1);

	}

	@High
	@Testlink(Id="IF-293", Title="seekTo: Call seekTo in playing state")
	public void testIF_293() throws Exception
	{
		int forwardSeekPos = 32000;
		int backwardSeekPos = 6000;
		
		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
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
	
	@Medium
	@Testlink(Id="IF-294", Title="seekTo: Call seekTo in paused state")
	public void testIF_294() throws Exception
	{
		int forwardSeekPos = 32000;

		gotoPausedState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		playerWrapper.seekTo(forwardSeekPos);
		
		playerWrapper.resumePlayback();
		CommonUtil.sleep(1);

		Assert.assertTrue("The player should go to the specified position", playerWrapper.getPlaybackPosition() > forwardSeekPos);
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		
		CommonUtil.sleep(3);
	}
	
	@Medium
	@Testlink(Id="IF-295", Title="seekTo: Call seekTo with a value which's greater than content duration")
	public void testIF_295() throws Exception
	{
		//The duration of the test conent is 60160
		int forwardSeekPos = 60161;

		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		long originalPos = playerWrapper.getPlaybackPosition();
		
		playerWrapper.seekTo(forwardSeekPos);
		CommonUtil.sleep(2);
		
		long currPos = playerWrapper.getPlaybackPosition();

		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertTrue("The player should keep current playback.", currPos>originalPos&&currPos<originalPos+3000);
	}
	
	@Medium
	@Testlink(Id="IF-296", Title="seekTo: Call seekTo with a value which's equal to content duration")
	public void testIF_296() throws Exception
	{
		playerWrapper.addErrorStateListener(errorListener);
		
		//The duration of the test content is 60160
		int forwardSeekPos = 60100;

		gotoPlayingState(testSignals_MP4.get("The_flash_ddp.mp4"));
		
		long originalPos = playerWrapper.getPlaybackPosition();
		
		playerWrapper.seekTo(forwardSeekPos);
		
		CommonUtil.sleep(1);
		
		Assert.assertEquals("The player should be in stopped state.", PlayerState.STOPPED, playerWrapper.getState());
		Assert.assertEquals("The event callback onError should not be triggered", 0, onErrorCount);
	}
	
	@Medium
	@Testlink(Id="IF-297", Title="seekTo: Call seekTo with a value which's equal to 0")
	public void testIF_297() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("flight_ddp_13s.mp4"));
 
		playerWrapper.seekTo(0);
		CommonUtil.sleep(1);
		long currPos = playerWrapper.getPlaybackPosition();

		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertTrue("The player should continue playback from specified position.", currPos> 0 && currPos<2000);
	}
	
	@Low
	@Testlink(Id="IF-298", Title="seekTo: Call seekTo with a minus value")
	public void testIF_298() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("flight_ddp_13s.mp4"));
 
		long originalPos = playerWrapper.getPlaybackPosition();
		playerWrapper.seekTo(-1);
		
		CommonUtil.sleep(1);
		
		long currPos = playerWrapper.getPlaybackPosition();

		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		Assert.assertTrue("The player should keep current playback", currPos> originalPos && currPos< originalPos + 2000);
	}
	
	@Medium
	@Testlink(Id="IF-333", Title="seekTo: Call seekTo on audio only file(DDP)")
	public void testIF_333() throws Exception
	{
		int forwardSeekPos = 32000;
		int backwardSeekPos = 6000;

		gotoPlayingState(testSignals_MP4.get("AudioOnly_DDP"));
		
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
	
	@Medium
	@Testlink(Id="IF-334", Title="seekTo; Call seekTo on audio only file(NGC)")
	public void testIF_334() throws Exception
	{
		int forwardSeekPos = 18000;
		int backwardSeekPos = 6000;

		gotoPlayingState(testSignals_MP4.get("Music_ac4_20.mp4"));
		
		//Seek forward
		playerWrapper.seekTo(forwardSeekPos);
		CommonUtil.sleep(1);

		Assert.assertTrue(String.format("The player should go to the specified position %s", playerWrapper.getPlaybackPosition() ), playerWrapper.getPlaybackPosition() >= forwardSeekPos);
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
	
		CommonUtil.sleep(3);
		
		//Seek backward
		playerWrapper.seekTo(backwardSeekPos);
		CommonUtil.sleep(1);

		Assert.assertTrue("The player should go to the specified position", playerWrapper.getPlaybackPosition() >= backwardSeekPos);
		Assert.assertEquals("The player should be in playing state.", PlayerState.PLAYING, playerWrapper.getState());
		
		CommonUtil.sleep(3);
	}
	
	@Medium
	@Testlink(Id="IF-335", Title="seekTo: Call seekTo on video only file")
	public void testIF_335() throws Exception
	{
		int forwardSeekPos = 32000;
		int backwardSeekPos = 6000;

		gotoPlayingState(testSignals_MP4.get("VideoOnly_DDP"));
		
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
	
	@Medium
	@Testlink(Id="IF-639", Title="getVersion(): Check version value")
	public void testIF_639() throws Exception
	{
		Assert.assertEquals("The spec version should match the value to be delivered in sprint", 
				  		   "1.5.0", String.format("%d.%d.%d", playerWrapper.getVersion().MAJOR, 
				  				   	playerWrapper.getVersion().MINOR, 
				  				   	playerWrapper.getVersion().PATCH));
	}
	
	public void test_FOO() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Alizee_YUV"));
		
		CommonUtil.sleep(30);
	}
	
	private boolean checkVersionFormat(String version)
	{
		Pattern p = Pattern.compile("^\\d+.\\d+.\\d+$");
		Matcher m = p.matcher(version);
		
		if(m.find())
			return true;
		
		return false;
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
