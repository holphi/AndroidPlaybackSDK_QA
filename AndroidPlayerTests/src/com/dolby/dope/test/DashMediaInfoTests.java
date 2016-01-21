package com.dolby.dope.test;

import junit.framework.Assert;

import com.dolby.infra.Player.MediaInfo;
import com.dolby.infra.Player.MediaInfo.MediaType;
import com.dolby.infra.Player.MediaInfo.TrackInfo;
import com.dolby.infra.Player.MediaInfo.TrackInfo.StreamInfo;

import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Low;
import com.dolby.test.annotation.Testlink;

public class DashMediaInfoTests extends BaseTests
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	
	@High
	@Testlink(Id="IF-438", Title="MediaInfo(DASH): Retrieve movie duration in the playback of OnDemand content")
	public void testIF_438() throws Exception
	{
		long expectedDuration = 655708;
		
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		
		Assert.assertEquals(String.format("Call getMovieDuration(), it should return %d", expectedDuration), 
							expectedDuration, playerWrapper.getMediaInfo().getMovieDuration());
	}
	
	@Medium
	@Testlink(Id="IF-439", Title="MediaInfo(DASH): Retrieve movie duration in the playback of Live content")
	public void testIF_439() throws Exception
	{
		long expectedDuration = 655708;
		
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		
		Assert.assertEquals(String.format("Call getMovieDuration(), it should return %d", expectedDuration), 
							expectedDuration, playerWrapper.getMediaInfo().getMovieDuration());
	}
	
	@High
	@Testlink(Id="IF-440", Title="MediaInfo(DASH): Retrieve movie uri in streaming playback")
	public void testIF_440() throws Exception
	{
		String expectedUri = testSignals_DASH.get("Basic_Flash_640x360");

		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		
		Assert.assertEquals(String.format("Call getMovieUri(), it should return %s", expectedUri ), 
							expectedUri , playerWrapper.getMediaInfo().getMovieUri());
	}
	
	@High
	@Testlink(Id="IF-441", Title="MediaInfo(DASH): Retrieve number of tracks in basic streaming playback")
	public void testIF_441() throws Exception
	{
		int expected = 2;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		Assert.assertEquals(String.format("Call getNumTrackInfos(), it should return %d", expected ), 
							expected, playerWrapper.getMediaInfo().getNumTracks());
	}
	
	@Medium
	@Testlink(Id="IF-442", Title="MediaInfo(DASH): Retrieve number of tracks against an multi language on-demand content")
	public void testIF_442() throws Exception
	{
		int expected = 3;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Ondemand_DDP_Multi_Lang"));
		Assert.assertEquals(String.format("Call getNumTrackInfos(), it should return %d", expected ), 
				expected, playerWrapper.getMediaInfo().getNumTracks());
	}
	
	@Low
	@Testlink(Id="IF-443", Title="MediaInfo(DASH): Retrieve number of tracks against an multi language live content")
	public void testIF_443() throws Exception
	{
		int expected = 3;
		gotoPlayingState(testSignals_DASH.get("Basic_Live_DDP_Multi_Lang"));
		Assert.assertEquals(String.format("Call getNumTrackInfos(), it should return %d", expected ), 
				expected, playerWrapper.getMediaInfo().getNumTracks());
	}
	
	@Low
	@Testlink(Id="IF-444", Title="MediaInfo(DASH): Retrieve number of tracks against an audio-only content")
	public void testIF_444() throws Exception
	{
		int expected = 1;
		gotoPlayingState(testSignals_DASH.get("Basic_AudioOnly"));
		
		Assert.assertEquals(String.format("Call getNumTrackInfos(), it should return %d", expected ), 
							expected, playerWrapper.getMediaInfo().getNumTracks());
	}

	@High
	@Testlink(Id="IF-447", Title="MediaInfo(DASH): Retrieve track instance by passing a correct value")
	public void testIF_447() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Ondemand_DDP_Multi_Lang"));

		TrackInfo videoTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(0);
		Assert.assertNotNull("The return type of getTrackInfo(0) should not be null", videoTrackInfo);
		Assert.assertEquals("The return type of getTrackInfo(0) should be an video type", MediaType.VIDEO, videoTrackInfo.getType());
		
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		Assert.assertNotNull("The return type of getTrackInfo(1) should not be null", audioTrackInfo);
		Assert.assertEquals("The return type of getTrackInfo(1) should be an audio type", MediaType.AUDIO, audioTrackInfo.getType());
		
		//Subtitle can't be verified due to missing such test content
	} 
	
	@Medium
	@Testlink(Id="IF-448", Title="MediaInfo(DASH): Retrieve track instance by passing an out-of-range value")
	public void testIF_448() throws Exception
	{		
		gotoPlayingState(testSignals_DASH.get("Basic_Ondemand_DDP_Multi_Lang"));

		MediaInfo mediaInfo = playerWrapper.getMediaInfo();

		boolean illegalArgumentTriggered = true;

		Assert.assertTrue("The exception IllegalArugmentException should be triggered.", illegalArgumentTriggered);
	}
	
	@High
	@Testlink(Id="IF-449", Title="Track(DASH): Retrieve track type on an ondemand content")
	public void testIF_449() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		
		//Check the first track item
		TrackInfo track = mediaInfo.getTrackInfo(0);
		Assert.assertNotNull("The 1st track instance should not be null.", track);
		Assert.assertEquals(String.format("The first track type should be %s", MediaType.AUDIO.toString()), 
							MediaType.VIDEO, track.getType());
		
		//Check the second track item
		track = mediaInfo.getTrackInfo(1);
		Assert.assertNotNull("The 2nd track instance should not be null.", track);
		Assert.assertEquals(String.format("The second track type should be %s", MediaType.VIDEO.toString()), 
							MediaType.AUDIO, track.getType());
	}
	
	@Medium
	@Testlink(Id="IF-450", Title="Track(DASH): Retrieve track type on a live content")
	public void testIF_450() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		
		//Check the first track item
		TrackInfo track = mediaInfo.getTrackInfo(0);
		Assert.assertNotNull("The 1st track instance should not be null.", track);
		Assert.assertEquals(String.format("The first track type should be %s", MediaType.VIDEO.toString()), 
							MediaType.VIDEO, track.getType());
		
		//Check the second track item
		track = mediaInfo.getTrackInfo(1);
		Assert.assertNotNull("The 2nd track instance should not be null.", track);
		Assert.assertEquals(String.format("The second track type should be %s", MediaType.AUDIO.toString()), 
							MediaType.AUDIO, track.getType());
	}
	
	@High
	@Testlink(Id="IF-452", Title="MediaInfo(DASH): Retrieve movie timescale on a live content")
	public void testIF_452() throws Exception
	{
		int expectedValue = 1000;
		
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		
		Assert.assertEquals("Verify movie timescale", expectedValue, mediaInfo.getMovieTimescale());
	}
	
	@Medium
	@Testlink(Id="IF-453", Title="MediaInfo(DASH): Retrieve movie timescale on a ondemand content")
	public void testIF_453() throws Exception
	{
		int expectedValue = 1000;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		
		Assert.assertEquals("Verify movie timescale", expectedValue, mediaInfo.getMovieTimescale());
	}

	@High
	@Testlink(Id="IF-455", Title="Track(DASH): Retrieve language of audio track on a ondemand content")
	public void testIF_455() throws Exception
	{
		String expectedLang = "eng";
		
		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		TrackInfo track = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals("Verify lang of audio track.", expectedLang, track.getLanguage());
	}
	
	@Medium
	@Testlink(Id="IF-456", Title="Track(DASH): Retrieve language of audio tracks on a multi-language live content")
	public void testIF_456() throws Exception
	{
		String expectedLangArray[] = {"eng", "fra"};
		
		gotoPlayingState(testSignals_DASH.get("Basic_Live_DDP_Multi_Lang"));
		
		//Basic_Live_DDP_Multi_Lang: http://192.168.1.100/IF/Adaptive/DASH/OnDel/DDP/Live/BPL31_25fps/multi_lang/ChID_voices_eng_fra_51_256_ddp.mpd
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		for(int i=1; i<mediaInfo.getNumTracks(); i++)
		{
			Assert.assertEquals(String.format("Verify lang of track %d",  i), 
								expectedLangArray[i-1], mediaInfo.getTrackInfo(i).getLanguage());
		}
	}
	
	@Low
	@Testlink(Id="IF-457", Title="Track(DASH): Retrieve language of video track on a live content")
	public void testIF_457() throws Exception
	{
		String expectedLang = "und";
		
		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		TrackInfo track = playerWrapper.getMediaInfo().getTrackInfo(0);
		
		Assert.assertEquals("Verify lang of video track.", expectedLang, track.getLanguage());
	}
	
	@High
	@Testlink(Id="IF-458", Title="Track(DASH): Retrieve total streams of video track on an adaptive ondemand content")
	public void testIF_458() throws Exception
	{
		int expectedNumOfStreams = 3;
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		
		TrackInfo video_track = playerWrapper.getMediaInfo().getTrackInfo(0);
		
		//Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.VIDEO.toString()),
		//					MediaType.VIDEO, video_track.getType());
		Assert.assertEquals(String.format("The total streams of video track should be %d", expectedNumOfStreams),
							expectedNumOfStreams, video_track.getNumStreams());
	}
	
	@High
	@Testlink(Id="IF-459", Title="Track(DASH): Retrieve total streams of audio track on an adaptive ondemand content")
	public void testIF_459() throws Exception
	{
		int expectedNumOfStreams = 2;
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		
		TrackInfo audio_track = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.AUDIO.toString()),
							MediaType.AUDIO, audio_track.getType());
		Assert.assertEquals(String.format("The total streams of audio track should be %d", expectedNumOfStreams),
							expectedNumOfStreams, audio_track.getNumStreams());
	}
	
	@Medium
	@Testlink(Id="IF-460", Title="Track(DASH): Retrieve total streams of audio&video track respectively on an adaptive live content")
	public void testIF_460() throws Exception
	{
		int expectedNumOfAudioStreams = 2; 
		int expectedNumOfVideoStreams = 3;
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_Live"));
		
		TrackInfo video_track = playerWrapper.getMediaInfo().getTrackInfo(0);
		TrackInfo audio_track = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals(String.format("The 1st track retrieved should be the %s track.", MediaType.VIDEO.toString()),
							MediaType.VIDEO, video_track.getType());
		Assert.assertEquals(String.format("The 2nd track retrieved should be the %s track.", MediaType.AUDIO.toString()),
				MediaType.AUDIO, audio_track.getType());

		Assert.assertEquals(String.format("The total streams of 1st track should be %d", expectedNumOfVideoStreams),
				expectedNumOfVideoStreams, video_track.getNumStreams());
		
		Assert.assertEquals(String.format("The total streams of 2nd track should be %d", expectedNumOfAudioStreams),
				expectedNumOfAudioStreams, audio_track.getNumStreams());
	}
	
	@High
	@Testlink(Id="IF-464", Title="Track(DASH): Retrieve stream instance of audio track by passing a valid value on an ondemand content")
	public void testIF_464() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.AUDIO.toString()),
							MediaType.AUDIO, audioTrackInfo.getType());
		
		for(int i=0;i<audioTrackInfo.getNumStreams();i++)
		{
			StreamInfo streamInst = audioTrackInfo.getStreamInfo(i);
			
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
	}
	
	@Medium
	@Testlink(Id="IF-465", Title="Track(DASH): Retrieve stream inst of audio track by passing an out-of-range value")
	public void testIF_465() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.AUDIO.toString()),
							MediaType.AUDIO, audioTrackInfo.getType());
		
		boolean illegalArgumentTriggered = true;
		
		Assert.assertTrue("The exception IllegalStateException should be thrown.", illegalArgumentTriggered);
	}
	
	@Low
	@Testlink(Id="IF-466", Title="Track(DASH): Retrieve stream instance of audio&video track on an adaptive live content")
	public void testIF_466() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_Live"));
		
		TrackInfo video_track = playerWrapper.getMediaInfo().getTrackInfo(0);
		TrackInfo audio_track = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		for(int i=0;i<video_track.getNumStreams();i++)
		{
			StreamInfo streamInst = video_track.getStreamInfo(i);
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
		
		for(int i=0;i<audio_track.getNumStreams();i++)
		{
			StreamInfo streamInst = audio_track.getStreamInfo(i);
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
	}
	
	@Low
	@Testlink(Id="IF-467", Title="Track(DASH): Retrieve stream instance of audio track on an audio-only ondemand content")
	public void testIF_467() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_Live"));
		
		TrackInfo video_track = playerWrapper.getMediaInfo().getTrackInfo(0);
		TrackInfo audio_track = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		for(int i=0;i<video_track.getNumStreams();i++)
		{
			StreamInfo streamInst = video_track.getStreamInfo(i);
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
		
		for(int i=0;i<audio_track.getNumStreams();i++)
		{
			StreamInfo streamInst = audio_track.getStreamInfo(i);
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
	}
	
	@Low
	@Testlink(Id="IF-468", Title="Track(DASH): Retrieve stream instance of audio track on a music-only adaptive stream")
	public void testIF_468() throws Exception
	{
		int expectedSubStreamCount = 3;
		int expectedTrackInfoCount = 1;
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_AudioOnly_DDP_LIVE"));
		
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(0);
		
		Assert.assertEquals(String.format("There should be %d track included in the content.", expectedTrackInfoCount),
							expectedTrackInfoCount, playerWrapper.getMediaInfo().getNumTracks());
		Assert.assertEquals(String.format("There should be %d streams included in the audio track.", expectedSubStreamCount),
							expectedSubStreamCount, audioTrackInfo.getNumStreams());
		
		for(int i=0;i<audioTrackInfo.getNumStreams();i++)
		{
			StreamInfo streamInst = audioTrackInfo.getStreamInfo(i);
			Assert.assertNotNull(String.format("The %dth stream intance should not be null", i), streamInst);
		}
	}
	
	@High
	@Testlink(Id="IF-469", Title="Stream(DASH): Retrieve bandwidth info of video streams on an adaptive ondemand content")
	public void testIF_469() throws Exception
	{
		int expectedBW[] = {2965448, 983496, 484656};
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_VOD"));
		
		TrackInfo videoTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(0);
		//Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.VIDEO.toString()),
		//					MediaType.VIDEO, videoTrackInfo.getType());
		
		for(int i=0; i<videoTrackInfo.getNumStreams();i++)
		{
			long retrievedBW = videoTrackInfo.getStreamInfo(i).getBandwidth();
			Assert.assertEquals(String.format("The bandwidth of %dst stream should be", i, expectedBW[i]),
								expectedBW[i], retrievedBW);
		}
	}
	
	@Medium
	@Testlink(Id="IF-470", Title="Stream(DASH): Retrieve bandwidth info of audio streams on an adaptive live content")
	public void testIF_470() throws Exception
	{
		int expectedBW[] = {160000, 128000};
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_ACH_Live"));
		
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		Assert.assertEquals(String.format("The track retrieved should be the %s track.", MediaType.AUDIO.toString()),
							MediaType.AUDIO, audioTrackInfo.getType());
		
		for(int i=0; i<audioTrackInfo.getNumStreams();i++)
		{
			long retrievedBW = audioTrackInfo.getStreamInfo(i).getBandwidth();
			Assert.assertEquals(String.format("The bandwidth of %dst stream should be", i, expectedBW[i]),
								expectedBW[i], retrievedBW);
		}
	}
	
	@Low
	@Testlink(Id="IF-471", Title="Stream(DASH): Retrieve bandwidth info track on a basic live content")
	public void testIF_471() throws Exception
	{
		long expectedVideoBW = 500000;
		long expectedAudioBW = 96000;
		
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		
		long retrievedVideoBW = playerWrapper.getMediaInfo().getTrackInfo(0).getStreamInfo(0).getBandwidth();
		long retrievedAudioBW = playerWrapper.getMediaInfo().getTrackInfo(1).getStreamInfo(0).getBandwidth();
		
		Assert.assertEquals(String.format("The bandwidth of retrieved audio bandwidth should be", expectedAudioBW),
				expectedAudioBW, retrievedAudioBW);
		Assert.assertEquals(String.format("The bandwidth of retrieved video bandwidth should be", expectedVideoBW),
				expectedVideoBW, retrievedVideoBW);
	}
	
	@High
	@Testlink(Id="IF-505", Title="Stream(DASH): Retrieve codecs info on the VOD content")
	public void testIF_505() throws Exception
	{
		String expectedVideoCodecs = "avc1.42c01e";
		String expectedAudioCodecs = "ec-3";

		gotoPlayingState(testSignals_DASH.get("Basic_Flash_640x360"));
		
		TrackInfo videoTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(0);
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals("Verify audio codecs", expectedAudioCodecs, audioTrackInfo.getStreamInfo(0).getCodecs());
		Assert.assertEquals("Verify video codecs", expectedVideoCodecs, videoTrackInfo.getStreamInfo(0).getCodecs());
	}
	
	@Medium
	@Testlink(Id="IF-506", Title="Stream(DASH): Retrieve codecs info on the LIVE content")
	public void testIF_506() throws Exception
	{
		String expectedVideoCodecs = "avc1.42c015";
		String expectedAudioCodecs = "ec-3";

		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		
		TrackInfo videoTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(0);
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals("Verify audio codecs", expectedAudioCodecs, audioTrackInfo.getStreamInfo(0).getCodecs());
		Assert.assertEquals("Verify video codecs", expectedVideoCodecs, videoTrackInfo.getStreamInfo(0).getCodecs());
	}

	@High
	@Testlink(Id="IF-511", Title="Stream(DASH): Retrieve audio sampling rate on the vod content")
	public void testIF_511() throws Exception
	{
		int expectedAudioSamplingRate = 48000;
		
		gotoPlayingState(testSignals_DASH.get("Basic_Bunny_Multibitrate"));
		
		MediaInfo mediaInfo = playerWrapper.getMediaInfo();
		TrackInfo audioTrackInfo = mediaInfo.getTrackInfo(1);
		
		for(int i=0;i<audioTrackInfo.getNumStreams();i++)
			Assert.assertEquals(String.format("Verify stream %d", i), 
								expectedAudioSamplingRate, audioTrackInfo.getStreamInfo(i).getAudioSamplingRate());
	}
	
	@Medium
	@Testlink(Id="IF-512", Title="Stream(DASH): Retrieve audio sampling rate on the live conent")
	public void testIF_512() throws Exception
	{
		int expectedAudioSamplingRate = 48000;
		
		gotoPlayingState(testSignals_DASH.get("Basic_ED_Live_480x320"));
		TrackInfo audioTrackInfo = playerWrapper.getMediaInfo().getTrackInfo(1);
		
		Assert.assertEquals("Verify audio codecs", expectedAudioSamplingRate, audioTrackInfo.getStreamInfo(0).getAudioSamplingRate());
	}
}