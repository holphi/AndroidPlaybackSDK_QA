package com.dolby.dope.test;

import junit.framework.Assert;

import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Testlink;
import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Low;
import com.dolby.infra.Player;
import com.dolby.infra.Player.Value;
import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;

public class DAPNodeConfigTests extends BaseTests 
{

	private final String DAP_ENABLED = "DAP_ENABLED";
	private final String DIALOG_ENH_ENABLED = "DIALOG_ENH_ENABLED";
	private final String VOLUME_LEVELER_ENABLED = "VOLUME_LEVELER_ENABLED";
	private final String OUTPUT_MODE = "OUTPUT_MODE";
	private final String DRC_MODE = "DRC_MODE";
	private final String EC3_DEC_SUBSTREAM_INDEX = "SUBSTREAM_INDEX";
	private final String EC3_DEC_MAIN_ASSOC_MIX = "MAIN_ASSOC_MIX";
	private final String MIXER_SWITCH = "MIXER_SWITCH";
	
	//New properties
	private final String AUDIO_OPTIMIZER_ENABLED = "AUDIO_OPTIMIZER_ENABLED";
	private final String REGULATOR_ENABLED = "REGULATOR_ENABLED";
	private final String REGULATOR_SPEAKER_DISTORTION_ENABLED = "REGULATOR_SPEAKER_DISTORTION_ENABLED";
	private final String PROCESS_OPTIMIZER_ENABLED = "PROCESS_OPTIMIZER_ENABLED";
	private final String BASS_EXTRACTION_ENABLED = "BASS_EXTRACTION_ENABLED";
	
	private enum OutputMode{
		DAP_CPDP_PROCESS_2_HEADPHONE_HEIGHT, DAP_CPDP_PROCESS_5_1_2_SPEAKER,DAP_CPDP_PROCESS_2_HEADPHONE,DAP_CPDP_PROCESS_5_1_SPEAKER,DAP_CPDP_PROCESS_2_LTRT,DAP_CPDP_PROCESS_2
	}
	
	private enum DrcMode{
		DDPI_UDC_COMP_CUSTOM_0, DDPI_UDC_COMP_CUSTOM_1, DDPI_UDC_COMP_LINE, DDPI_UDC_COMP_RF, DDPI_UDC_COMP_PORTABLE_L8, DDPI_UDC_COMP_PORTABLE_L11,
		DDPI_UDC_COMP_PORTABLE_L14, DDPI_UDC_COMP_PORTABLE_TEST
	}
	
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
	
	/*@High
	@Testlink(Id="IF-359", Title="VOLUME_LEVELER_ENABLED: Enable/disable volumn leveler in playback")
	public void testIF_359() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Verify default value of property VOLUME_LEVELER_ENABLED should be 0
		verifyVolumeLeveler(0);
		
		for(int i=0;i<10;i++)
		{
			int userSetValue;
			
			if(i%2==0)
				userSetValue = 1;
			else
				userSetValue = 0;
			
			setVolumeLeveler(userSetValue);			
			verifyVolumeLeveler(userSetValue);
			
			CommonUtil.sleep(2);
		}
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-360", Title="VOLUME_LEVELER_ENABLED: Enable volumn leveler in prepared state")
	public void testIF_360() throws Exception
	{
		playerWrapper.addPrepareStateListener(enableVolumeLeveler);
		
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//The volumn leveler should be enabled;
		verifyVolumeLeveler(1);
	}
	
	@Medium
	@Testlink(Id="IF-361", Title="VOLUME_LEVELER_ENABLED: Volumn leveler should be disabled in the second playback")
	public void testIF_361() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Enable volumn leveler
		setVolumeLeveler(1);
		
		CommonUtil.sleep(5);
		
		playerWrapper.stopPlayback();
		
		//Second playback
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//In the second playback, the volumn leveler should be disabled;
		verifyVolumeLeveler(0);
		
		playerWrapper.stopPlayback();
	}
	
	@Low
	@Testlink(Id="IF-362", Title="VOLUME_LEVELER_ENABLED: Pass out of range value")
	public void testIF_362() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Pass an out of range value to volumn leveler
		setVolumeLeveler(100);
		
		CommonUtil.sleep(10);
		
		//The volumn leveler should be still disabled;
		verifyVolumeLeveler(0);
	}
	
	@Low
	@Testlink(Id="IF-363", Title="VOLUME_LEVELER_ENABLED: Pass invalid character")
	public void testIF_363() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		Value prop = new Value();
		prop.setString("Hello world");
		playerWrapper.setProperty(VOLUME_LEVELER_ENABLED, prop);
		
		CommonUtil.sleep(5);
		
		//The volumn leveler should be still disabled;
		verifyVolumeLeveler(0);
	}*/
	
	/*@High
	@Testlink(Id="IF-364", Title="DAP_ENABLED: Enable/disable dap in playback")
	public void testIF_364() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//Verify default value of property DAP_ENABLED should be 1
		verifyDAP(1);
		
		for(int i=0;i<10;i++)
		{
			int userSetValue;
			
			if(i%2==0)
				userSetValue = 0;
			else
				userSetValue = 1;
			
			setDAP(userSetValue);
			verifyDAP(userSetValue);
			
			CommonUtil.sleep(2);
		}
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-365", Title="DAP_ENABLED: Disable dap in prepared state")
	public void testIF_365() throws Exception
	{
		playerWrapper.addPrepareStateListener(disableDAP);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//The DAP should be disabled;
		verifyDAP(0);
	}
	
	@Medium
	@Testlink(Id="IF-366", Title="DAP_ENABLED: DAP should be enabled in the second playback")
	public void testIF_366() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//Disable DAP
		setDAP(0);
		
		CommonUtil.sleep(5);
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		
		//Second playback
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//In the second playback, the dap should be enabled;
		verifyDAP(1);
		
		playerWrapper.stopPlayback();
	}

	@Low
	@Testlink(Id="IF-367", Title="DAP_ENABLED: Pass out of range value")
	public void testIF_367() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//Pass an out of range value to dap
		setDAP(100);
		
		CommonUtil.sleep(10);
		
		//The dap should be still enabled;
		verifyDAP(1);
	}
	
	@Low
	@Testlink(Id="IF-368", Title="DAP_ENABLED: Pass invalid character")
	public void testIF_368() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		Value prop = new Value();
		prop.setString("Hello world");
		playerWrapper.setProperty(VOLUME_LEVELER_ENABLED, prop);
		
		CommonUtil.sleep(5);
		
		//The dap should still be enabled;
		verifyDAP(1);
	}*/
	
	
// Dialog Enhancement cases
	@High
	@Testlink(Id="IF-384", Title="DIALOG_ENH_ENABLED: Enable/disable dialog enhancement in playback")
	public void testIF_384() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Verify default value of property DIALOG_ENH_ENABLED should be 0
		verifyDlgEnhancement(0);
		
		for(int i=0;i<10;i++)
		{
			int userSetValue;
			
			if(i%2==0)
				userSetValue = 0;
			else
				userSetValue = 1;
			
			setDlgEnhancement(userSetValue);
			CommonUtil.sleep(1);
			verifyDlgEnhancement(userSetValue);
			CommonUtil.sleep(1);
		}
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-385", Title="DIALOG_ENH_ENABLED: Enable dialog enhancement in prepared state")
	public void testIF_385() throws Exception
	{
		playerWrapper.addPrepareStateListener(disableDE);
		
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//The DAP should be disabled;
		verifyDlgEnhancement(0);
	}
	
	@Medium
	@Testlink(Id="IF-386", Title="DIALOG_ENH_ENABLED: Dialog enhancement should be disabled in the second playback")
	public void testIF_386() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Enable DE
		setDlgEnhancement(1);
		
		CommonUtil.sleep(5);
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		
		//Second playback
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		CommonUtil.sleep(1);
		
		//In the second playback, the DE should be enabled as default;
		verifyDlgEnhancement(0);
		
		playerWrapper.stopPlayback();
	}
	
	@Low
	@Testlink(Id="IF-387", Title="DIALOG_ENH_ENABLED: Pass out of range value")
	public void testIF_387() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		//Pass an out of range value to DE
		setDlgEnhancement(65535);
		
		CommonUtil.sleep(10);
		
		//The DE should be still disabled;
		verifyDlgEnhancement(0);
	}
	
	@Low
	@Testlink(Id="IF-388", Title="DIALOG_ENH_ENABLED: Pass invalid character")
	public void testIF_388() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		Value prop = new Value("hello world");
		playerWrapper.setProperty(DIALOG_ENH_ENABLED, prop);
		
		CommonUtil.sleep(5);
		
		//The DE should still be enabled;
		verifyDlgEnhancement(0);
	}
	
//Output Mode test cases
	@High
	@Testlink(Id="IF-389", Title="OUTPUT_MODE: Ensure default value is DAP_CPDP_PROCESS_2_LTRT")
	public void testIF_389() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-390", Title="OUTPUT_MODE: Set all valid values during playback")
	public void testIF_390() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		String[] outputMode = {"DAP_CPDP_PROCESS_2_HEADPHONE_HEIGHT", "DAP_CPDP_PROCESS_5_1_2_SPEAKER","DAP_CPDP_PROCESS_2_HEADPHONE","DAP_CPDP_PROCESS_5_1_SPEAKER","DAP_CPDP_PROCESS_2_LTRT","DAP_CPDP_PROCESS_2"};
		for(int i=0;i<12;i++)
		{
			setOutputMode(outputMode[i%6]);
			CommonUtil.sleep(1);
			verifyOutputMode(outputMode[i%6]);
			CommonUtil.sleep(1);
		}
		
		playerWrapper.stopPlayback();
	}	
	
	@High
	@Testlink(Id="IF-391", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_2_HEADPHONE_HEIGHT to output mode in prepared state")
	public void testIF_391() throws Exception
	{
		playerWrapper.addPrepareStateListener(set2HeadphoneHeight);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_HEADPHONE_HEIGHT.toString());
	}
	
	@High
	@Testlink(Id="IF-392", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_5_1_2_SPEAKER to output mode in prepared state")
	public void testIF_392() throws Exception
	{
		playerWrapper.addPrepareStateListener(set512Speaker);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
	}

	@High
	@Testlink(Id="IF-393", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_2_HEADPHONE to output mode in prepared state")
	public void testIF_393() throws Exception
	{
		playerWrapper.addPrepareStateListener(set2Headphone);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_HEADPHONE.toString());
	}
	
	@High
	@Testlink(Id="IF-394", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_5_1_SPEAKER to output mode in prepared state")
	public void testIF_394() throws Exception
	{
		playerWrapper.addPrepareStateListener(set51Speaker);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_SPEAKER.toString());
	}
	
	@High
	@Testlink(Id="IF-395", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_2_LTRT to output mode in prepared state")
	public void testIF_395() throws Exception
	{
		playerWrapper.addPrepareStateListener(set2LTRT);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
	}
	
	@High
	@Testlink(Id="IF-429", Title="OUTPUT_MODE: Set DAP_CPDP_PROCESS_2 to output mode in prepared state")
	public void testIF_429() throws Exception
	{
		playerWrapper.addPrepareStateListener(setProcess2);
		
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2.toString());
	}
	
	@High
	@Testlink(Id="IF-652", Title="[DDP Main-asso mixing] Before playback, pass a valid value to mix in asso stream")
	public void testIF_652() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = -32;
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
		
		value = 20;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@Medium
	@Testlink(Id="IF-653", Title="[DDP Main-asso mixing] Before playback, pass -32(minimal value) to enable main stream only")
	public void testIF_653() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = -32;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@Medium
	@Testlink(Id="IF-654", Title="[DDP Main-asso mixing] Before playback, pass 32(maximal value) to enable asso stream")
	public void testIF_654() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = 32;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}

	@Medium
	@Testlink(Id="IF-655", Title="[DDP Main-asso mixing] In content playback, pass an out-of-range mixing value")
	public void testIF_655() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
	
		CommonUtil.sleep(30);
		
		int out_of_range= 9999;
		
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(out_of_range));
		
		int default_value = -32;
		Assert.assertEquals(default_value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@High
	@Testlink(Id="IF-656", Title="[DDP Main-asso mixing] In playback, pass a valid value to mix in asso stream")
	public void testIF_656() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = -32;
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
		
		value = 20;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@Medium
	@Testlink(Id="IF-657", Title="[DDP Main-asso mixing] In playback, pass -32 to enable main stream only")
	public void testIF_657() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = -32;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@Medium
	@Testlink(Id="IF-658", Title="[DDP Main-asso mixing] In playback, pass 32 to enable asso stream only")
	public void testIF_658() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		int value = 32;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		CommonUtil.sleep(30);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
							playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@Medium
	@Testlink(Id="IF-659", Title="[DDP Main-asso mixing] In playback, switch bwteen -32 & 32 to toggle asso stream on/off")
	public void testIF_659() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		for(int i=0;i<20;i++)
		{
			int value;
			if(i%2==0)
				value = -32;
			else
				value = 32;
			
			playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
			
			CommonUtil.sleep(3);
			
			Assert.assertEquals(String.format("The value should be %d", value), value, 
					playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
		}
	}
	
	@Low
	@Testlink(Id="IF-660", Title="[DDP Main-asso mixing] In playback, loop all possible maxing value")
	public void testIF_660() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		for(int i=-32;i<=32;i++)
		{
			playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(i));
			
			CommonUtil.sleep(2);
			
			Assert.assertEquals(String.format("The value should be %d", i), i, 
					playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
		}
	}
	
	@Low
	@Testlink(Id="IF-661", Title="[DDP Main-asso mixing] In paused state, set a valid value mix in asso stream, then resume playback")
	public void testIF_661() throws Exception
	{
		playerWrapper.addPrepareStateListener(setSubStreamIndex);
		
		gotoPlayingState(testSignals_MP4.get("DAA_Main_Commentary"));
		
		CommonUtil.sleep(5);
		playerWrapper.pausePlayback();
		
		int value = 20;
		playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(value));
		
		
		playerWrapper.resumePlayback();
		
		CommonUtil.sleep(20);
		
		Assert.assertEquals(String.format("The value should be %d", value), value, 
				playerWrapper.getProperty(EC3_DEC_MAIN_ASSOC_MIX).toInt());
	}
	
	@High
	@Testlink(Id="IF-662", Title="[DDP Assoc selection] In playing state, select associated stream")
	public void testIF_662() throws Exception
	{
		playerWrapper.addPrepareStateListener(setMixValue);
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_DAA_Main_Commentary"));
		
		CommonUtil.sleep(10);
		
		playerWrapper.setProperty(EC3_DEC_SUBSTREAM_INDEX, new Value(0));
		playerWrapper.setProperty(MIXER_SWITCH, new Value(1));
		
		CommonUtil.sleep(20);
	}
	
	@High
	@Testlink(Id="IF-664", Title="[DDP Assoc selection] In playing state, iterate all possible index of associated stream")
	public void testIF_664() throws Exception
	{
		playerWrapper.addPrepareStateListener(setMixValue);
		
		gotoPlayingState(testSignals_DASH.get("Adaptive_DAA_Main_Commentary"));
		
		CommonUtil.sleep(5);
		
		for(int i=3;i<=30;i++)
		{
			
			playerWrapper.setProperty(EC3_DEC_SUBSTREAM_INDEX, new Value(i%2));
			playerWrapper.setProperty(MIXER_SWITCH, new Value(1));
			
			CommonUtil.sleep(3);
		}
	}
	
	@Low
	@Testlink(Id="IF-669", Title="[DDP Assoc selection] Pass 0 to EC3_DEC_SUBSTREAM_INDEX")
	public void testIF_669() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_DAA_Main_Commentary"));
		CommonUtil.sleep(2);
		
		playerWrapper.setProperty(EC3_DEC_SUBSTREAM_INDEX, new Value(0));
		CommonUtil.sleep(2);
		
		Assert.assertTrue("An invalid value exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
	}
	
	Player.PrepareStateListener setSubStreamIndex = new Player.PrepareStateListener() 
	{
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(3);
			playerWrapper.setProperty(EC3_DEC_SUBSTREAM_INDEX, pv);
			
			//Hack code
			playerWrapper.setProperty(MIXER_SWITCH, new Value(1));
		}
	};
	
	Player.PrepareStateListener setMixValue = new Player.PrepareStateListener() 
	{
		@Override
		public void onPrepared(Player player) 
		{
			playerWrapper.setProperty(EC3_DEC_MAIN_ASSOC_MIX, new Value(17));
		}
	};
	
	@Medium
	@Testlink(Id="IF-396", Title="OUTPUT_MODE: Output mode should be set to default in the second playback")
	public void testIF_396() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		setOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
		CommonUtil.sleep(6);
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		
		//Second playback
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		CommonUtil.sleep(1);
		
		//In the second playback, the output mode should be enabled as default;
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		playerWrapper.stopPlayback();
	}
	
	@Low
	@Testlink(Id="IF-397", Title="OUTPUT_MODE: Pass out of range value")
	public void testIF_397() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		//Pass an out of range value to DE
		Value prop = new Value(65535);
		playerWrapper.setProperty(OUTPUT_MODE, prop);
		
		CommonUtil.sleep(5);
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
	}
	
	@Low
	@Testlink(Id="IF-398", Title="OUTPUT_MODE: Pass invalid character")
	public void testIF_398() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		Value prop = new Value("hello world");

		playerWrapper.setProperty(DIALOG_ENH_ENABLED, prop);
		
		setOutputMode("hello world");
		CommonUtil.sleep(5);
		
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
	}
	
	/*@High
	@Testlink(Id="IF-399", Title="Set DE,VL DAP off, Output mode default in playback")
	public void testIF_399() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		CommonUtil.sleep(1);
		
		verifyVolumeLeveler(0);
		verifyDlgEnhancement(1);
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		
		//Set DE-0, VL-0,DAP-0, output mode-DAP_CPDP_PROCESS_2_LTRT
		setDAP(0);
		setVolumeLeveler(0);
		setDlgEnhancement(0);
		setOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		CommonUtil.sleep(10);
		
		verifyDAP(0);
		verifyVolumeLeveler(0);
		verifyDlgEnhancement(0);
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		
		playerWrapper.stopPlayback();
	}*/	
	
	@High
	@Testlink(Id="IF-400", Title="Set DE on, Output mode DAP_CPDP_PROCESS_5_1_2_SPEAKER in playback")
	public void testIF_400() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		CommonUtil.sleep(1);
		
		//Verify default value of DE(0) and Output mode(DAP_CPDP_PROCESS_2_LTRT)
		verifyDlgEnhancement(0);
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
		
		//Set DE-1,  output mode-DAP_CPDP_PROCESS_2_LTRT
		setDlgEnhancement(1);
		setOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
		CommonUtil.sleep(18);

		verifyDlgEnhancement(1);
		verifyOutputMode(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="IF-422", Title="DRC_MODE: Ensure default value is DDPI_UDC_COMP_LINE")
	public void testIF_422() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		
		CommonUtil.sleep(5);
		
		verifyDRCMode(DrcMode.DDPI_UDC_COMP_LINE.toString());
	}
	
	@High
	@Testlink(Id="IF-423", Title="DRC_MODE: Set all valid values one by one during playback")	
	public void testIF_423() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		String[] drcMode = {"DDPI_UDC_COMP_CUSTOM_0",  "DDPI_UDC_COMP_CUSTOM_1",  "DDPI_UDC_COMP_LINE",  "DDPI_UDC_COMP_RF",  "DDPI_UDC_COMP_PORTABLE_L8", "DDPI_UDC_COMP_PORTABLE_L11",
							"DDPI_UDC_COMP_PORTABLE_L14", "DDPI_UDC_COMP_PORTABLE_TEST"};
		for(int i=0;i<8;i++)
		{
			setDRCMode(drcMode[i]);
			CommonUtil.sleep(1);
			verifyDRCMode(drcMode[i]);
			CommonUtil.sleep(1);
		}
		
		playerWrapper.stopPlayback();
	}
	
	@Medium
	@Testlink(Id="IF-424", Title="DRC_MODE: Set value of drc mode in prepared state")	
	public void testIF_424() throws Exception
	{
		playerWrapper.addPrepareStateListener(setDrcModeToPortable_L11);

		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		CommonUtil.sleep(10);
		
		verifyDRCMode(DrcMode.DDPI_UDC_COMP_PORTABLE_L11.toString());
	}
	
	@Low
	@Testlink(Id="IF-425", Title="DRC_MODE: Drc mode should be set to default in the second playback")	
	public void testIF_425() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		CommonUtil.sleep(10);
		
		setDRCMode(DrcMode.DDPI_UDC_COMP_PORTABLE_L11.toString());
		CommonUtil.sleep(1);
		
		verifyDRCMode(DrcMode.DDPI_UDC_COMP_PORTABLE_L11.toString());
		CommonUtil.sleep(1);
		
		playerWrapper.stopPlayback();
	}
	
	private void setVolumeLeveler(int enabled)
	{
		Value prop = new Value(enabled);
		
		boolean ret = playerWrapper.setProperty(VOLUME_LEVELER_ENABLED, prop);
		Assert.assertEquals("The return value of setProperty(VOLUME_LEVELER_ENABLED) should be true", true, ret);
	}
	
	private void verifyVolumeLeveler(int expected)
	{
		Assert.assertEquals(String.format("The volumn leveler should be %d",  expected), 
							expected, playerWrapper.getProperty(VOLUME_LEVELER_ENABLED).toInt());
	}
	
	private void setDlgEnhancement(int enabled)
	{
		Value prop = new Value(enabled);
		
		playerWrapper.setProperty(DIALOG_ENH_ENABLED, prop);
	}
	
	private void verifyDlgEnhancement(int expected)
	{
		Assert.assertEquals(String.format("The Dialog enhancement should be %d",  expected), 
							expected, playerWrapper.getProperty(DIALOG_ENH_ENABLED).toInt());
	}
	
	private void setDAP(int enabled)
	{
		Value prop = new Value(enabled);

		boolean ret = playerWrapper.setProperty(DAP_ENABLED, prop);
		Assert.assertEquals("The return value of setProperty(DAP_ENABLED) should be true", true, ret);
	}
	
	private void verifyDAP(int expected)
	{
		Assert.assertEquals(String.format("DAP should be %d",  expected), 
							expected, playerWrapper.getProperty(DAP_ENABLED).toInt());
	}
	
	private void setOutputMode(String outputmode)
	{
		Value prop = new Value(outputmode);
		
		playerWrapper.setProperty(OUTPUT_MODE, prop);
	}
	
	private void verifyOutputMode(String expected)
	{
		Assert.assertEquals(String.format("The output mode should be %s",  expected), 
							expected, playerWrapper.getProperty(OUTPUT_MODE).toString());
	}
	
	private void setDRCMode(String drcmode)
	{
		Value prop = new Value(drcmode);
		
		playerWrapper.setProperty(DRC_MODE, prop);
	}
	
	private void verifyDRCMode(String expected)
	{
		Assert.assertEquals(String.format("The drc mode should be %s",  expected), 
				expected, playerWrapper.getProperty(DRC_MODE).toString());
	}
	
	private void setAudioOptimizer(String value)
	{
		Value prop = new Value(value);
		
		boolean ret = playerWrapper.setProperty(AUDIO_OPTIMIZER_ENABLED, prop);
		Assert.assertEquals("The return value of setAudioOptimizer() should be true", true, ret);
	}
	
	private void verifyAudioOptimizer(String expected)
	{
		Assert.assertEquals(String.format("The audio optimizer should be %s",  expected), 
				expected, playerWrapper.getProperty(AUDIO_OPTIMIZER_ENABLED).toString());
	}
	
	private void setRegulator(String value)
	{
		Value prop = new Value(value);
		
		boolean ret = playerWrapper.setProperty(REGULATOR_ENABLED, prop);
		Assert.assertEquals("The return value of setRegulator() should be true", true, ret);
	}
	
	private void verifyRegulator(String expected)
	{
		Assert.assertEquals(String.format("The regulator should be %s",  expected), 
				expected, playerWrapper.getProperty(REGULATOR_ENABLED).toString());
	}
	
	private void setRegulatorSpeakerDistortion(String value)
	{
		Value prop = new Value(value);
		
		boolean ret = playerWrapper.setProperty(REGULATOR_SPEAKER_DISTORTION_ENABLED, prop);
		Assert.assertEquals("The return value of setRegulatorSpeakerDistortion() should be true", true, ret);
	}
	
	private void verifyRegulatorSpeakerDistortion(String expected)
	{
		Assert.assertEquals(String.format("The regulator speaker distortion should be %s",  expected), 
				expected, playerWrapper.getProperty(REGULATOR_SPEAKER_DISTORTION_ENABLED).toString());
	}
	
	private void setProcessOptimizer(String value)
	{
		Value prop = new Value(value);
		
		boolean ret = playerWrapper.setProperty(PROCESS_OPTIMIZER_ENABLED, prop);
		Assert.assertEquals("The return value of setProcessOptimizer() should be true", true, ret);
	}
	
	private void verifyProcessOptimizer(String expected)
	{
		Assert.assertEquals(String.format("The process optimizer should be %s",  expected), 
				expected, playerWrapper.getProperty(PROCESS_OPTIMIZER_ENABLED).toString());
	}
	
	private void setBassExtraction(String value)
	{
		Value prop = new Value();
		
		boolean ret = playerWrapper.setProperty(BASS_EXTRACTION_ENABLED, prop);
		Assert.assertEquals("The return value of setBassExtraction() should be true", true, ret);
	}
	
	private void verifyBassExtraction(String expected)
	{
		Assert.assertEquals(String.format("The bass extraction should be %s",  expected), 
				expected, playerWrapper.getProperty(BASS_EXTRACTION_ENABLED).toString());
	}
	
	/* Temporarily disabled due to DAP 2.5 integration.
	@High
	@Testlink(Id="testIF_357", Title="Set and Get normal DAP_CPDP_OUTPUT_PRESET_CONFIG during playback")
	public void testIF_357() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		CommonUtil.sleep(3);
		//Default DAP_CPDP_OUTPUT_PRESET_CONFIG is 0
		pv = playerWrapper.getProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG");
		Log.i(TAG, pv.getString());
		Assert.assertEquals(0,pv.getInt());
		CommonUtil.sleep(1);
		
		//Set DAP_CPDP_OUTPUT_PRESET_CONFIG to 1
		pv.setInt(1);
		Assert.assertEquals(true, playerWrapper.setProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG",pv));
		CommonUtil.sleep(1);
		
		pv = playerWrapper.getProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG");
		Assert.assertEquals(1,pv.getInt());
		CommonUtil.sleep(1);
		
		//Set DAP_CPDP_OUTPUT_PRESET_CONFIG to 0
		pv.setInt(0);
		Assert.assertEquals(true, playerWrapper.setProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG",pv));
		CommonUtil.sleep(1);
		
		pv = playerWrapper.getProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG");
		Assert.assertEquals(0,pv.getInt());
	}
	
	
	@High
	@Testlink(Id="testIF_358", Title="Set DAP_CPDP_OUTPUT_PRESET_CONFIG before start() get its value during playback")
	public void testIF_358() throws Exception
	{
		//Add listener for setting property value after prepared
		playerWrapper.addPrepareStateListener(SetDAP_CPDP_OUTPUT_PRESET_CONFIG);
		gotoPlayingState(testSignals_MP4.get("ChID_5_1_2_JOC.mp4"));
		CommonUtil.sleep(3);
		Assert.assertEquals("The onPrepared event should not be notified.", 1, onPrepareCount);
		Assert.assertEquals("The player should transit to PLAYING state.", PlayerState.PLAYING, playerWrapper.getState());
		//Get property when playing
		pv = playerWrapper.getProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG");
		Assert.assertEquals(1,pv.getInt());
	}
	
	
	Player.PrepareStateListener SetDAP_CPDP_OUTPUT_PRESET_CONFIG = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			onPrepareCount++;
			pv.setInt(1);
			playerWrapper.setProperty("DAP_CPDP_OUTPUT_PRESET_CONFIG",pv);
		}
	};*/
	
	/*public void test_Foo001() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		verifyAudioOptimizer("1");
		verifyRegulator("1");
		verifyRegulatorSpeakerDistortion("1");
		
		verifyProcessOptimizer("0");
		verifyBassExtraction("0");
	}
	
	public void test_Foo002() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("car_ddp_51.mp4"));
		
		
		CommonUtil.sleep(10);
		
		setAudioOptimizer("0");
		setRegulator("0");
		setRegulatorSpeakerDistortion("0");
		setProcessOptimizer("0");
		setBassExtraction("0");
		
		CommonUtil.sleep(10);
		
		verifyAudioOptimizer("0");
		verifyRegulator("0");
		verifyRegulatorSpeakerDistortion("0");
		
		verifyProcessOptimizer("0");
		verifyBassExtraction("0");
	}*/
	
	Player.PrepareStateListener enableVolumeLeveler = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv = new Value(1);
			playerWrapper.setProperty(VOLUME_LEVELER_ENABLED, pv);
		}
	};
	
	Player.PrepareStateListener disableDAP = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(0);
			playerWrapper.setProperty(DAP_ENABLED, pv);
		}
	};
	
	Player.PrepareStateListener disableDE = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(0);
			playerWrapper.setProperty(DIALOG_ENH_ENABLED, pv);
		}
	};
	
	Player.PrepareStateListener set2HeadphoneHeight = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_2_HEADPHONE_HEIGHT.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener set2Headphone = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_2_HEADPHONE.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener set2LTRT = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_2_LTRT.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener set512Speaker= new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_5_1_2_SPEAKER.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener set51Speaker= new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_5_1_SPEAKER.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener setProcess2= new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(OutputMode.DAP_CPDP_PROCESS_2.toString());
			playerWrapper.setProperty(OUTPUT_MODE, pv);
		}
	};
	
	Player.PrepareStateListener setDrcModeToPortable_L11= new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value pv  = new Value(DrcMode.DDPI_UDC_COMP_PORTABLE_L11.toString());
			playerWrapper.setProperty(DRC_MODE, pv);
		}
	};
}
