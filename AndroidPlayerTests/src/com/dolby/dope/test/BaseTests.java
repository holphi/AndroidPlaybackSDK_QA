package com.dolby.dope.test;

import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;

import com.dolby.infra.Player;
import com.dolby.infra.Player.IllegalArgumentException;
import com.dolby.infra.Player.MediaInfo;
import com.dolby.infra.Player.MediaInfo.MediaType;
import com.dolby.infra.Player.PlayerState;

import com.dolby.application.infra.app.AndroidPlayer;

import com.dolby.dope.test.util.CommonUtil;

public abstract class BaseTests extends ActivityInstrumentationTestCase2<AndroidPlayer> 
{
	protected HashMap<String, String> testSignals_MP4 = null;
	protected HashMap<String, String> testSignals_DASH =null;
	protected HashMap<String, String> testSignals_TS = null;
	
	protected final String TAG = "DopePlayerTest";
	protected PlayerWrapper playerWrapper=null;
	
	//restServerUrl = "http://255.255.255.255:8095" 
	private String restServerUrl = "http://10.131.102.92:139";
		
	//An internal flag to record the count number of the event callback onStreamChanged for Audio stream;
	private int onAudioStreamChangedCount = 0;
	//An internal flag to record track id passed from the event callback onStreamChanged;
	private int trackId = 0;
	//An internal flag to record stream id passed from the event callback onStreamChanged;
	private int streamId = 0;

	Player.StreamChangedListener streamChangedListener = new Player.StreamChangedListener( ) {
		
		@Override
		public void onStreamChanged(Player player, int trackIdx, int streamIdx) {
			// TODO Auto-generated method stub
			Log.i(TAG, "The event callback onStreamChanged is triggered.");
			
			if(trackIdx == 1)
			{
				onAudioStreamChangedCount++;
				trackId = trackIdx;
				streamId = streamIdx;
			}
		}
	};
		
	protected boolean setUpIpfw(String direction) throws Exception
	{
		String url = null;
		direction = direction.toLowerCase();
		try
		{
			if(direction=="in"||direction=="out")
				url = String.format("%s/ipfw/setup/%s", restServerUrl, direction);
			else
				throw new Exception("The parameter can't be resolved.");
			
			JSONObject json = sendRequest(url);
			return (Boolean)json.get("result");
			
		}catch(Exception ex)
		{
			throw ex;
		}
	}
	
	protected boolean setBandwidth(String direction, int value) throws Exception
	{
		String url = null;
		direction = direction.toLowerCase();
		try
		{
			if(direction=="in"||direction=="out")
				url = String.format("%s/ipfw/config/%s/%d", restServerUrl, direction, value);
			else
				throw new Exception("The parameter can't be resolved.");
			
			JSONObject json = sendRequest(url);
			return (Boolean)json.get("result");
			
		}catch(Exception ex)
		{
			throw ex;
		}
	}
	
	protected boolean cleanUpIpfw() throws Exception
	{
		String url = String.format("%s/ipfw/cleanup", restServerUrl);
		
		try
		{
			JSONObject json = sendRequest(url);
			return (Boolean)json.get("result");
		}
		catch(Exception ex)
		{
			throw ex;
		}	
	}
	
	protected boolean isRestServerAwake() throws Exception
	{
		try
		{
			JSONObject json = sendRequest(restServerUrl);
			Boolean returnCode = (Boolean)json.get("result");
			return returnCode;
		}catch(Exception ex)
		{
			throw ex;
		}
	}
	
	private JSONObject sendRequest(String url) throws Exception
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		try
		{
			String strResponse = null;
			Log.i(TAG, String.format("Send request: %s", url));
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(statusCode==HttpStatus.SC_OK)
			{
				Log.i(TAG, "Response status code: 200");
				strResponse = EntityUtils.toString(httpResponse.getEntity());
				Log.i(TAG, String.format("Command execution result: %s", strResponse));
			}else
			{
				strResponse = "{result:False}";
			}
			
			JSONObject result = new JSONObject(strResponse);
			return result;
			
		}catch(Exception ex)
		{
			throw ex;
		}
	}

	//Transit the player from initialized state to error state.
	protected void gotoErrorState() throws Exception
	{
		Log.i(TAG,"Transit the player from INITIALIZED state to ERROR state");
		
		playerWrapper.setSource(testSignals_MP4.get("inexistent_file"));
		playerWrapper.prepare();

		waitForPlayerState(PlayerState.ERROR);	
	}
	
	//Transit the player to initialized state
	protected void gotoInitializedState() throws Exception
	{
		playerWrapper.setSource(testSignals_MP4.get("trim_ac4_13s.mp4"));
		
		waitForPlayerState(PlayerState.INITIALIZED);	
	}
	
	//Transit the player to initialized state
	protected void gotoInitializedState(String url) throws Exception
	{
		playerWrapper.setSource(url);
		
		waitForPlayerState(PlayerState.INITIALIZED);
	}
	
	//Default timeout is 10 seconds
	protected void waitForPlayerState(PlayerState expectedState) throws Exception
	{
		waitForPlayerState(expectedState, 10);
	}
	
	protected void waitForPlayerState(PlayerState expectedState, int timeout) throws Exception
	{
		int i = 0;
		while(playerWrapper.getState()!=expectedState)
		{
			if(i<timeout)
			{
				//Wait for 1 second for player state transition
				CommonUtil.sleep(1);
				i++;
			}else
				Assert.fail(String.format("The player doesn't transit to expected state: %s, actual state: %s", expectedState, playerWrapper.getState().toString()));
		}
	}
	
	protected void verifyStreamChangedEventCallback(int expectedCount, int expectedTrackId, int expectedStreamId) throws Exception
	{
		int timeout = 20, i = 0;
		
		while(onAudioStreamChangedCount != expectedCount)
		{
			if(i<timeout)
			{
				//Wait for 1 second for the event callback onMessage
				CommonUtil.sleep(1);
				i++;
			}else
				Assert.fail(String.format("The event callback onStreamChanged may not be triggered, expected = %d, actual = %d", expectedCount, onAudioStreamChangedCount));
		}
		
		if(onAudioStreamChangedCount>0)
		{
			Assert.assertEquals("Verify track id passed in", expectedTrackId, trackId);
			Assert.assertEquals("Verify stream id passed in", expectedStreamId, streamId);
		}
	}
	
	//Verify bandwidth of current audio stream
	protected void verifyCurrentAudioStreamBW(long expectedBW)
	{
		int trackId = playerWrapper.getPlayingTrack(MediaType.AUDIO);
		int audioStreamId = playerWrapper.getPlayingStream(trackId);
		
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		
		long bandwidth = 0;
		
		try {
			bandwidth = mediaInfo.getTrackInfo(trackId).getStreamInfo(audioStreamId).getBandwidth();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals("Verify the bandwidth of current audio stream which is being played", expectedBW, bandwidth);
	}
	
	//Transit the player to prepared state
	protected void gotoPreparedState() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		gotoInitializedState();
		
		playerWrapper.prepare();
		
		waitForPlayerState(PlayerState.PREPARED);
	}
	
	//Transit the player to prepared state
	protected void gotoPreparedState(String url) throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		gotoInitializedState(url);
		
		playerWrapper.prepare();
		
		waitForPlayerState(PlayerState.PREPARED);
	}

	//Transit the player to playing state
	protected void gotoPlayingState() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		
		waitForPlayerState(PlayerState.PLAYING);
	}
	
	protected void gotoPreparingState() throws Exception
	{
		playerWrapper.rmOnPreparedListener();
		
		gotoInitializedState(testSignals_DASH.get("Adaptive_VOD_LowBW"));
		
		playerWrapper.prepare();
		
		waitForPlayerState(PlayerState.PREPARING);
	}
	
	protected void gotoStoppedState() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("flight_ddp_13s.mp4"));
		
		waitForPlayerState(PlayerState.STOPPED, 20);
	}
		
	//Transit the player to playing state
	protected void gotoPlayingState(String url) throws Exception
	{
		playerWrapper.startPlayback(url);
		
		waitForPlayerState(PlayerState.PLAYING);
	}
	
	//Transit the player to paused state
	protected void gotoPausedState() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("trim_ac4_13s.mp4"));
		waitForPlayerState(PlayerState.PLAYING);
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		waitForPlayerState(PlayerState.PAUSED);
	}
	
	//Transit the player to paused state
	protected void gotoPausedState(String url) throws Exception
	{
		playerWrapper.startPlayback(url);
		waitForPlayerState(PlayerState.PLAYING);
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(1);
		
		waitForPlayerState(PlayerState.PAUSED);	
	}
	
	//Load DTT XML file generated from DTT
	protected String loadDTTFile(String filePath)
	{
		Scanner fileReader = null;
		String xmlTuning = "";
		
		try 
		{
			fileReader = new Scanner(new URL(filePath).openStream(), "UTF-8");
			xmlTuning = fileReader.useDelimiter("\\A").next(); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			fileReader.close();
		}
		
		return xmlTuning;
	}
	
	protected void gotoEndState() throws Exception
	{
		playerWrapper.release();
		CommonUtil.sleep(2);
	}

	public BaseTests() 
	{
		super("com.dolby.application.dope.app", AndroidPlayer.class);
		
		//DDP
		testSignals_MP4 = new HashMap<String, String>();
		testSignals_MP4.put("gotye_ddp_13s.mp4", "file:///sdcard/dolby/DOPE/media/gotye_ddp_13s.mp4");
		testSignals_MP4.put("Dolby_51_72s.mp4", "file:///sdcard/dolby/DOPE/media/TestVector_002_51.mp4");
		testSignals_MP4.put("missing_file_prefix", "/sdcard/dolby/DOPE/media/gotye_ddp_13s.mp4");
		testSignals_MP4.put("unsupport_file", "file:///sdcard/dolby/DOPE/media/test.txt");
		testSignals_MP4.put("inexistent_file", "file:///sdcard/dolby/DOPE/media/inexistent_folder/test.mp4");
		testSignals_MP4.put("flight_ddp_13s.mp4",  "file:///sdcard/dolby/DOPE/media/flight_ddp_13s.mp4");
		testSignals_MP4.put("Corrupt_container_ddp.mp4", "file:///sdcard/dolby/DOPE/media/Corrupt_container_ddp.mp4");
		testSignals_MP4.put("Corrupt_audio_ddp.mp4", "file:///sdcard/dolby/DOPE/media/Corrupt_audio_ddp.mp4");
		testSignals_MP4.put("Corrupt_video_ddp.mp4", "file:///sdcard/dolby/DOPE/media/Corrupt_video_ddp.mp4");
		testSignals_MP4.put("ACH_ddp.mp4", "file:///sdcard/dolby/DOPE/media/ACH_1920x1080_24fps_20_ddp_4min16s.mp4");
		testSignals_MP4.put("ED_ddp_51.mp4", "file:///sdcard/dolby/DOPE/media/ED_1280x720_24fps_51_ddp_10min55sec.mp4");
		testSignals_MP4.put("AudioOnly_DDP", "file:///sdcard/dolby/DOPE/media/Sample_MusicOnly_20_ddp.mp4");
		testSignals_MP4.put("VideoOnly_DDP", "file:///sdcard/dolby/DOPE/media/Sample_VideoOnly_55s.mp4");
		testSignals_MP4.put("ChID_5_1_2_JOC.mp4", "file:///sdcard/dolby/DOPE/media/ChIDJOC.mp4");
		testSignals_MP4.put("The_flash_ddp.mp4", "file:///sdcard/dolby/DOPE/media/The_Flash_ec3.mp4");
		testSignals_MP4.put("car_ddp_51.mp4", "file:///sdcard/dolby/DOPE/media/Cars_1920x1080_24fps_51_ddp.mp4");
		testSignals_MP4.put("6CH_DDP_AUDIO_DE.mp4", "file:///sdcard/dolby/DOPE/media/6CH_DDP_AUDIO_DE.mp4");
		testSignals_MP4.put("Leaf_DDP_JOC.mp4", "file:///sdcard/dolby/DOPE/media/Leaf_5P1JOC.mp4");
		testSignals_MP4.put("DAA_Main_Commentary", "file:///sdcard/dolby/DOPE/media/skip_armstrong_main_3_language.mp4");
		testSignals_MP4.put("Audio_only_ddp_30s.mp4", "file:///sdcard/dolby/DOPE/media/audio_only_ddp_30s.mp4");
		
		testSignals_MP4.put("Alizee_YUV", "file:///sdcard/dolby/DOPE/media/alizee.yuv");
		
		//AC-4
		testSignals_MP4.put("trim_ac4_13s.mp4", "file:///sdcard/dolby/DOPE/media/Alizee_ac4_10s.mp4");
		testSignals_MP4.put("slient_ac4_20.mp4", "file:///sdcard/dolby/DOPE/media/The_Flash_ec3.mp4");
		testSignals_MP4.put("slient_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/The_Flash_ec3.mp4");
		testSignals_MP4.put("transformer_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/Transformer_1920x1080_23976fps_51_ac4_204s.mp4");
		testSignals_MP4.put("transformer_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/ED_1280x720_24fps_51_ddp_10min55sec.mp4");
		testSignals_MP4.put("ED_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/ED_1280x720_24fps_51_ac4_10min55sec.mp4");
		testSignals_MP4.put("HOP_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/Hop_1280x720_24fps_51_30min_ac4.mp4");
		testSignals_MP4.put("Music_ac4_20.mp4", "file:///sdcard/dolby/DOPE/media/Music_44100_20_32_ac4.mp4");
		testSignals_MP4.put("Transformer_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/Transformer_51_ac4_204s.mp4");
		testSignals_MP4.put("Cars_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/Inception_H264_480p_6ch_128k.mp4");
		testSignals_MP4.put("Inception_ac4_51.mp4", "file:///sdcard/dolby/DOPE/media/Inception_H264_480p_6ch_128k.mp4");
		testSignals_MP4.put("Alizee_ac4_10s.mp4", "file:///sdcard/dolby/DOPE/media/Alizee_ac4_10s.mp4");
		testSignals_MP4.put("Multi_Presentation_AC4.mp4", "file:///sdcard/dolby/DOPE/media/car_multi_presentation.mp4");
		testSignals_MP4.put("Main_Asso_AC4.mp4", "file:///sdcard/dolby/DOPE/media/car_main_asso.mp4");
		
		//Test signals for DASH playback
		testSignals_DASH = new HashMap<String, String>();
		
		//General
		testSignals_DASH.put("MPD_Missing_EndTag", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_IllFormat_MPD/missing_endtag.mpd");
		testSignals_DASH.put("MPD_Incorrect_Content", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_IllFormat_MPD/incorrect_content_in_mpd.mpd");
		testSignals_DASH.put("MPD_Inexistent_URL", "http://192.168.1.19/IF/Adaptive/DASH/Internal/Inexistent_Folder/inexistent.mpd");
		
		//DDP
		testSignals_DASH.put("Basic_DDP_87s", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Short_87s/Basic_Short_87s.mpd");
		testSignals_DASH.put("Basic_ED_480x320", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_ED_480x320_492344_96000/test.mpd");
		testSignals_DASH.put("Basic_ED_Live_480x320", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/Basic_ED_480x320_492344_96000/test.mpd");
		testSignals_DASH.put("Basic_ED_Live_480x320_MissingCodecsInfo", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/Basic_ED_480x320_492344_96000/test_missingcodescInfo.mpd");
		testSignals_DASH.put("Basic_Flash_640x360", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_The_Flash_Man_640x360_BP_15fps_ddp/test.mpd");
		testSignals_DASH.put("Basic_BipBop", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_BipBop_AVSync/test.mpd");
		testSignals_DASH.put("Basic_VideoOnly", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_VideoOnly/test.mpd");
		testSignals_DASH.put("Basic_AudioOnly", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_AudioOnly/test.mpd");
		testSignals_DASH.put("Basic_Missing_Audio_File", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_FileMissing/Basic_Audio_File_Missing.mpd");
		testSignals_DASH.put("Basic_Missing_Video_File", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_FileMissing/Basic_Video_File_Missing.mpd");
		testSignals_DASH.put("Basic_Missing_BothAV", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_FileMissing/Basic_BothAV_Missing.mpd");
		testSignals_DASH.put("Basic_Corrupted_Audio_Container", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Corrputed_Contents/test-corrputed-audio-con.mpd");
		testSignals_DASH.put("Basic_Corrputed_Video_Container", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Corrputed_Contents/test-corrputed-video-con.mpd");
		testSignals_DASH.put("Basic_Corrupted_Audio_Stream", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Corrputed_Contents/test-corrputed-audio-stream.mpd");
		testSignals_DASH.put("Basic_Corrputed_Video_Stream", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Corrputed_Contents/test-corrputed-video-stream.mpd");
		testSignals_DASH.put("Basic_LongURL", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/RequestedURL_TooLong/ondel/Gravity_Demo/five_contents_and_wild_child_h264muxed_streams/muxed_streams/DASH/OnDemand/Wild-China_eng_51chchannel_id_51/852x480p23976fps750kh264main30_6ch/852x480p23976fps750kh.mpd");
		testSignals_DASH.put("Basic_WildChina", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_WildChina/test.mpd");
		
		testSignals_DASH.put("Basic_Ondemand_DDP_Multi_Lang", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/OnDemand/BPL31_25fps/multi_lang/ChID_voices_eng_fra_51_256_ddp.mpd");
		testSignals_DASH.put("Basic_Ondemand_DDP_Multi_Rate", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/OnDemand/BPL31_25fps/multi_rate/ChID_voices_71_384_448_768_ddp.mpd");
		testSignals_DASH.put("Basic_Ondemand_DDP_Dual_Decoding", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/OnDemand/BPL31_25fps/dual_decoding/ChID_voices_AD_51_10_256_128_ddp.mpd");
		
		testSignals_DASH.put("Basic_Live_DDP_Multi_Lang", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/Live/BPL31_25fps/multi_lang/ChID_voices_eng_fra_51_256_ddp.mpd");
		testSignals_DASH.put("Basic_Live_DDP_Multi_Rate", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/Live/BPL31_25fps/multi_rate/ChID_voices_71_384_448_768_ddp.mpd");
		testSignals_DASH.put("Basic_Live_DDP_Main_Cmt", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/cars_jpbathroom_main_dvs_cmt/cars_jpbathroom.mpd");
		testSignals_DASH.put("Basic_Live_DDP_Dual_Decoding", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/DDP/Live/BPL31_25fps/dual_decoding/ChID_voices_AD_51_10_256_128_ddp.mpd");
		testSignals_DASH.put("Basic_Ondemand_DDP_Substream", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/cars_jpbathroom_main_dvs_cmt/cars_jpbathroom.mpd");
		testSignals_DASH.put("Basic_Live_DDP_Substream", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/cars_jpbathroom_main_dvs_cmt/cars_jpbathroom.mpd");
		testSignals_DASH.put("Basic_JOC_VOD_MissingBWProp", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/AudioSphere_DDPJOC_OnDemand/AudioSphere_MissingBWProp.mpd");
		testSignals_DASH.put("Basic_JOC_VOD", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/AudioSphere_DDPJOC_OnDemand/AudioSphere_DDPJOC.mpd");
		testSignals_DASH.put("Basic_Bunny_Multibitrate", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Bunny_MultiAudioRate/test.mpd");
		testSignals_DASH.put("Basic_Bunny_MultiAudioTrack", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Bunny_MultiAudioRate/test_multiAudioTrack.mpd");
		testSignals_DASH.put("Basic_Bunny_96k", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Bunny_MultiAudioRate/test_96.mpd");
		testSignals_DASH.put("Basic_Bunny_128k", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Bunny_MultiAudioRate/test_128.mpd");
		testSignals_DASH.put("Basic_Bunny_192k", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Basic_Bunny_MultiAudioRate/test_192.mpd");
		
		testSignals_DASH.put("Adaptive_ACH_VOD", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/Adaptive_ACH_VOD/test.mpd");
		testSignals_DASH.put("Adaptive_ACH_Live", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/Adaptive_ACH/test.mpd");
		testSignals_DASH.put("Adaptive_AudioOnly_DDP_ONDEMAND", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/AudioOnly_DASH_DDP_ONDEMAND/test_adaptive.mpd");
		testSignals_DASH.put("Adaptive_AudioOnly_DDP_LIVE", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/AudioOnly_DASH_DDP_LIVE/test_adaptive.mpd");
		testSignals_DASH.put("Adaptive_AudioOnly_AAC_ONDEMAND", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/OnDemand/AudioOnly_DASH_AAC_ONDEMAND/test_adaptive.mpd");
		testSignals_DASH.put("Adaptive_AudioOnly_AAC_LIVE", "http://192.168.1.19/IF/Adaptive/DASH/Internal/DDP/Live/AudioOnly_DASH_AAC_LIVE/test_adaptive.mpd");
		
		testSignals_DASH.put("Adaptive_DAA_Main_Commentary", "http://192.168.1.19/IF/Adaptive/DASH/DAA/Main_Commentary_Video/test.mpd");
		
		
		//AC-4
		testSignals_DASH.put("Adaptive_Live_NGC_Multi_AV_Rate","http://192.168.1.19/IF/Adaptive/DASH/OnDel/NGC/Silent/Live/H264/HPL_40/multi_av_rate/Silent_23976fps_h264_51_51_20_128_64_32_ac4.mpd");
		testSignals_DASH.put("Adaptive_Ondemand_NGC_Multi_AV_Rate", "http://192.168.1.19/IF/Adaptive/DASH/OnDel/NGC/Silent/OnDemand/H264/HPL_40/multi_av_rate/Silent_23976fps_h264_51_51_20_128_64_32_ac4.mpd");
		testSignals_DASH.put("Adaptive_MultiPresentation_AC4", "http://192.168.1.19/IF/Adaptive/DASH/Internal/AC_4_0_99/OnDemand/DASH_Multi_Presentation/test.mpd");
		
		//DDP
		testSignals_DASH.put("Adaptive_VOD_LowBW", "http://192.168.0.19:90/car/test.mpd");
		
		
		//MPEG2-TS
		testSignals_TS = new HashMap<String, String>();
		testSignals_TS.put("test_DDP_AV", "file:///sdcard/dolby/DOPE/media/mpeg2ts_ddp_av.ts");
		testSignals_TS.put("test_DDP_AudioOnly", "file:///sdcard/dolby/DOPE/media/mpeg2ts_ddp_audioonly.ts");
	}
	
	@Override
	protected void setUp() throws Exception
	{
		int seconds = 2;
		
		Log.i(TAG, String.format("Sleep %d seconds forcedly for fully releasing the last activity instance.", seconds));
		CommonUtil.sleep(seconds);
		
		super.setUp();
		
		Log.i(TAG, "DopePlayerBaseTests.setUp()");
		Log.i(TAG, "Reset all error flags.");
		
		CommonUtil.resetErrorFlags();
		
		AndroidPlayer androidPlayer = (AndroidPlayer)getActivity();
		playerWrapper = new PlayerWrapper(androidPlayer, true);
		
		Log.i(TAG, "Check if the instance of surface view has been created.");
		
		while(!playerWrapper.isSurfaceViewCreated())
		{
			Log.i(TAG, "Wait 1 second until the instance of surface view is created.");
			CommonUtil.sleep(1);
		}

		onAudioStreamChangedCount = 0;
		trackId = -9999;
		streamId = -9999;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		Log.i(TAG, "DopePlayerBaseTests.tearDown()");
		
		if(getActivity()!=null)
		{
			getActivity().finish();
		}
		
		super.tearDown();
	}
}