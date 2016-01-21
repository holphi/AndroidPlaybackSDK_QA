package com.dolby.dope.test;

import junit.framework.Assert;
import android.util.Log;

import com.dolby.infra.Player;
import com.dolby.infra.Player.PlayerState;
import com.dolby.infra.Player.Value;
import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;
import com.dolby.test.annotation.High;
import com.dolby.test.annotation.Medium;
import com.dolby.test.annotation.Low;
import com.dolby.test.annotation.Testlink;

public class NGCNodeConfigTests extends BaseTests 
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
	
	private final String AC4_DEC_TARGET_REF_LEVEL = "AC4_DEC_TARGET_REF_LEVEL";
	private final String AC4_DEC_DIALOG_ENH_GAIN_INPUT = "AC4_DEC_DIALOG_ENH_GAIN_INPUT";
	private final String AC4_DEC_DAP_ENABLED = "AC4_DEC_DAP_ENABLED";
	private final String AC4_DEC_CHANNEL_CONFIG = "AC4_DEC_CHANNEL_CONFIG";
	private final String AC4_DEC_PRESENTATION_INDEX = "AC4_DEC_PRESENTATION_INDEX";
	private final String AC4_DEC_MAIN_ASSOC_MIX = "AC4_DEC_MAIN_ASSOC_MIX";
	
	private enum ChannelConfig{
		 AC4DEC_OUT_CH_UNDEFINED, AC4DEC_OUT_CH_LO_RO, AC4DEC_OUT_CH_LT_RT,AC4DEC_OUT_CH_LT_RT_PLII,AC4DEC_OUT_CH_HEADPHONE,
		AC4DEC_OUT_CH_SPEAKER_VIRT, AC4DEC_OUT_CH_3_2_1,AC4DEC_OUT_OBJ_15_1,AC4DEC_OUT_OBJ_31_1		
	}
	
	@High
	@Testlink(Id="testIF_336", Title="Set and Get normal AC4_DEC_TARGET_REF_LEVEL during playback")
	public void testIF_336() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));

		//Verify the default value
		int expectedDefValue = -14;	
		Assert.assertEquals(expectedDefValue, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());
		
		//Adjust the value of ref level incrementally from -31 to -7 
		for(int i=-31; i<=-7; i++)
		{
			Value p = new Value(i);
			
			playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
			
			CommonUtil.sleep(1);

			Assert.assertEquals(String.format("Verify the return value of getProperty should be %d.", i), 
								i, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());
		}
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="testIF_337", Title="Set AC4_DEC_TARGET_REF_LEVEL before start() get its value during playback")
	public void testIF_337() throws Exception
	{
		int expectedRefLevel = -31;
		
		//Set the parameter AC4_DEC_TARGET_REF_LEVEL to -31 in prepared state;
		playerWrapper.addPrepareStateListener(setDecTargetRefLevel);
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", expectedRefLevel), 
				expectedRefLevel, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());
	}
	
	@Medium
	@Testlink(Id="testIF_338", Title="Set AC4_DEC_TARGET_REF_LEVEL after playback get its value when playing another content")
	public void testIF_338() throws Exception
	{
		int customizedRefLevel = -31;
		int defRefLevel = -14;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value(customizedRefLevel);

		playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", customizedRefLevel), 
				customizedRefLevel, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());
		CommonUtil.sleep(5);
		playerWrapper.stopPlayback();

		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		CommonUtil.sleep(5);
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", defRefLevel), 
				defRefLevel, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());		
	}
	
	@Low
	@Testlink(Id="testIF_339", Title="Set out-of-range value to AC4_DEC_TARGET_REF_LEVEL")
	public void testIF_339() throws Exception
	{
		int default_value = -14;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value(-32);
		playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
		
		Assert.assertTrue("An invalid argument exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", default_value), 
				default_value, playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toInt());
		
		p = new Value(-8);
		playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
		
		Assert.assertTrue("An invalid argument exception should be thrown", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));

		CommonUtil.sleep(3);
	}
	
	@Low
	@Testlink(Id="testIF_340", Title="Set invalid character to AC4_DEC_TARGET_REF_LEVEL")
	public void testIF_340() throws Exception
	{
		String invalid_value = "Hello world";
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value(invalid_value);
		
		playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
		CommonUtil.sleep(5);
		
		//The player should not crash.
	}
	
	
	@High
	@Testlink(Id="testIF_341", Title="Set and Get AC4_DEC_DIALOG_ENH_GAIN_INPUT in playback")
	public void testIF_341() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));

		//Verify the default value
		int expectedDefValue = 0;	
		Assert.assertEquals(expectedDefValue, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
		
		//Adjust the value of ref level incrementally from 0 to 12 
		for(int i=0; i<=12; i++)
		{
			Value p = new Value(i);
			
			playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
			CommonUtil.sleep(1);

			Assert.assertEquals(String.format("Verify the return value of getProperty should be %d.", i), 
								i, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
		}
		
		CommonUtil.sleep(30);
		
		playerWrapper.stopPlayback();
	}
	
	@Medium
	@Testlink(Id="testIF_342", Title="Set and Get AC4_DEC_DIALOG_ENH_GAIN_INPUT before start() get its value during  playback")	
	public void testIF_342() throws Exception
	{
		int expectedValue = 12;
		
		//Set the parameter AC4_DEC_TARGET_REF_LEVEL to -31 in prepared state;
		playerWrapper.addPrepareStateListener(setDlgEnhancement);
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", expectedValue), 
							expectedValue, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
	}
	
	@Medium
	@Testlink(Id="testIF_343", Title="Set AC4_DEC_DIALOG_ENH_GAIN_INPUT after playback get its value when playing another content")		
	public void testIF_343() throws Exception
	{
		int customizedDlgEnhancement = 12;
		int defDlgEnhancement = 0;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value(customizedDlgEnhancement);
		
		playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", customizedDlgEnhancement), 
				customizedDlgEnhancement, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
		CommonUtil.sleep(5);
		playerWrapper.stopPlayback();

		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		CommonUtil.sleep(5);
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", defDlgEnhancement), 
				defDlgEnhancement, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());		
	}
	
	@Low
	@Testlink(Id="testIF_344", Title="Set out-of-range value to AC4_DEC_DIALOG_ENH_GAIN_INPUT")	
	public void testIF_344() throws Exception
	{
		int out_of_range = 13;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value(out_of_range);

		playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", out_of_range), 
				out_of_range, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
		
		CommonUtil.sleep(6);
	}

	@Low
	@Testlink(Id="testIF_345", Title="Set invalid character to AC4_DEC_DIALOG_ENH_GAIN_INPUT")	
	public void testIF_345() throws Exception
	{
		String invalid_value = "Hello world";
	
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Value p = new Value(invalid_value);

		playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
		CommonUtil.sleep(5);
	
		//The player should not crash.
	}
	
	/*
	@High
	@Testlink(Id="testIF_346", Title="Set and Get normal AC4_DEC_DAP_ENABLED during playback")
	public void testIF_346() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));

		//Verify the default value
		int expectedDefValue = 1;	
		Assert.assertEquals(expectedDefValue, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
		
		//Adjust the value of ref level incrementally from -31 to -7 
		for(int i=0; i<10; i++)
		{
			boolean ret;
			
			Value p = new Value();
			
			int val = i%2==0 ? 0 : 1;
			
			p.setInt(val);
			
			ret = playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
			CommonUtil.sleep(1);
			
			Assert.assertTrue(String.format("Verify the return value of setProperty should be %b", ret), ret);
			Assert.assertEquals(String.format("Verify the return value of getProperty should be %d.", val), 
								val, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
		}
		
		playerWrapper.stopPlayback();
	}
	
	@High
	@Testlink(Id="testIF_347", Title="Set AC4_DEC_DAP_ENABLED before start() get its value during playback")
	public void testIF_347() throws Exception
	{
		int expectedValue = 0;
		
		playerWrapper.addPrepareStateListener(setDapEnabled);
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", expectedValue), 
				expectedValue, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
	}
	
	@Medium
	@Testlink(Id="testIF_348", Title="Set AC4_DEC_DAP_ENABLED after playback get its value when playing another content")
	public void testIF_348() throws Exception
	{
		int customizedValue = 0;
		int defValue = 1;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value();
		p.setInt(customizedValue);
		
		playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", customizedValue), 
				customizedValue, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
		CommonUtil.sleep(5);
		playerWrapper.stopPlayback();

		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		CommonUtil.sleep(5);
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", defValue), 
				defValue, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());	
	}
	
	@Low
	@Testlink(Id="testIF_349", Title="Set out-of-range value to AC4_DEC_DAP_ENABLED")
	public void testIF_349() throws Exception
	{
		int out_of_range = 13;
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		
		Value p = new Value();
		p.setInt(out_of_range);
		
		playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
		
		Assert.assertEquals(String.format("Verify the return value of getProperty should be %d", out_of_range), 
				out_of_range, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
		
		CommonUtil.sleep(6);
	}
	
	@Low
	@Testlink(Id="testIF_350", Title="Set invalid character to AC4_DEC_DAP_ENABLED")
	public void testIF_350() throws Exception
	{
		String invalid_value = "Hello world";
	
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Value p = new Value();
		p.setString(invalid_value);
	
		playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
		CommonUtil.sleep(5);
	
		//The player should not crash.
	}*/
	
	@High
	@Testlink(Id="testIF_351", Title="Set and Get AC4_DEC_CHANNEL_CONFIG in playback")
	public void testIF_351() throws Exception
	{
		//Some properties are invalid for AC-4 decoder v0.99
		//String[] ChConf = {"AC4DEC_OUT_CH_UNDEFINED", "AC4DEC_OUT_CH_LO_RO", "AC4DEC_OUT_CH_LT_RT", "AC4DEC_OUT_CH_LT_RT_PLII", "AC4DEC_OUT_CH_HEADPHONE",
		//				   "AC4DEC_OUT_CH_SPEAKER_VIRT", "AC4DEC_OUT_CH_3_2_1", "AC4DEC_OUT_OBJ_15_1", "AC4DEC_OUT_OBJ_31_1"};
		
		//Valid properties for AC-4 decoder v0.99
		String[] ChConf = {"AC4DEC_OUT_CH_LO_RO", "AC4DEC_OUT_CH_LT_RT", "AC4DEC_OUT_CH_LT_RT_PLII", "AC4DEC_OUT_CH_3_2_1"};
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Value p = new Value();
		Assert.assertEquals(String.format("Verify the default value of channel config should be %s", ChConf[0]), 
				ChConf[0], playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
		CommonUtil.sleep(1);
		
		for(int i=0; i<4; i++)
		{
			p = new Value(ChConf[i]);	
			playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
			CommonUtil.sleep(1);
			
			Assert.assertEquals(String.format("Verify the return value of channel config should be %s", ChConf[i]), 
					ChConf[i], playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
			CommonUtil.sleep(2);
		}		
	}	
	
	@High
	@Testlink(Id="testIF_352", Title="Set AC4_DEC_CHANNEL_CONFIG before start() get its value during playback")
	public void testIF_352() throws Exception
	{
		
		playerWrapper.addPrepareStateListener(setChConfig);
		
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		CommonUtil.sleep(2);
		
		Assert.assertEquals("Verify the value of channel config should be AC4DEC_OUT_CH_LT_RT_PLII", 
				ChannelConfig.AC4DEC_OUT_CH_LT_RT_PLII.toString(), playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
	}	
	
	@Medium
	@Testlink(Id="testIF_353", Title="Set AC4_DEC_CHANNEL_CONFIG after playback get its value when playing another content")
	public void testIF_353() throws Exception
	{
		
		//playerWrapper.addPrepareStateListener(setChConfig);
		Value p = new Value();
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
		CommonUtil.sleep(2);
		
		p = new Value(ChannelConfig.AC4DEC_OUT_CH_LT_RT.toString());
		playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
		
		playerWrapper.stopPlayback();
		CommonUtil.sleep(1);
		Assert.assertEquals("Player should transfer to STOPPED state", PlayerState.STOPPED, playerWrapper.getState());
		
		gotoPlayingState(testSignals_MP4.get("Alizee_ac4_10s.mp4"));
		CommonUtil.sleep(2);
		Assert.assertEquals("Verify the value of channel config should be AC4DEC_OUT_CH_LO_RO", 
				ChannelConfig.AC4DEC_OUT_CH_LO_RO.toString(), playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
	}
	
	@Low
	@Testlink(Id="testIF_354", Title="Set out-of-range value to AC4_DEC_CHANNEL_CONFIG")
	public void testIF_354() throws Exception
	{
		int out_of_range = 65535;
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Value p = new Value();
		Assert.assertEquals("Verify the default value of channel config should be AC4DEC_OUT_CH_LO_RO", 
				ChannelConfig.AC4DEC_OUT_CH_LO_RO.toString(), playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
		CommonUtil.sleep(2);
		
		
		p = new Value(out_of_range);	
		playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
		CommonUtil.sleep(2);
		//The player should not crash.
		//Assert.assertTrue("An IllegalArgumentError should be thrown,but not", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));					
	}	
	
	@Low
	@Testlink(Id="testIF_355", Title="Set invalid character to AC4_DEC_CHANNEL_CONFIG")
	public void testIF_355() throws Exception
	{
		String invalid_value = "hello_world";
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Value p = new Value();
		Assert.assertEquals("Verify the default value of channel config should be AC4DEC_OUT_CH_LO_RO", 
				ChannelConfig.AC4DEC_OUT_CH_LO_RO.toString(), playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
		CommonUtil.sleep(2);
		
		
		p = new Value(invalid_value);	
		playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
		CommonUtil.sleep(2);
		//The player should not crash.
		//Assert.assertTrue("An IllegalArgumentError should be thrown,but not", CommonUtil.getErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG));					
	}	
	
	@High
	@Testlink(Id="testIF_356", Title="Set multi AC4 parameters together and get the value back")
	public void testIF_356() throws Exception
	{
		playerWrapper.addPrepareStateListener(setAllParameters);
		gotoPlayingState(testSignals_MP4.get("Inception_ac4_51.mp4"));
	
		Assert.assertEquals("Verify the default value of channel config should be -9", 
				"-9", playerWrapper.getProperty(AC4_DEC_TARGET_REF_LEVEL).toString());
		CommonUtil.sleep(1);
		
		Assert.assertEquals("Verify the default value of channel config should be 3", 
				12, playerWrapper.getProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT).toInt());
		CommonUtil.sleep(1);
		
		/*
		Assert.assertEquals("Verify the default value of channel config should be 0", 
				0, playerWrapper.getProperty(AC4_DEC_DAP_ENABLED).toInt());
		CommonUtil.sleep(1);
		*/
		
		Assert.assertEquals("Verify the default value of channel config should be AC4DEC_OUT_CH_LT_RT", 
				ChannelConfig.AC4DEC_OUT_CH_LT_RT.toString(), playerWrapper.getProperty(AC4_DEC_CHANNEL_CONFIG).toString());
		
		CommonUtil.sleep(1);
	}
	
	@High
	@Testlink(Id="testIF_548", Title="[Multi-Presentation] Before playback, pass a valid index to select different presentation")
	public void testIF_548() throws Exception
	{
		Value p = new Value("3");
		
		playerWrapper.setProperty("AC4_DEC_PRESENTATION_INDEX", p);
		
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		//The 3rd presentation : German should be selected
		CommonUtil.sleep(5);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_549", Title="[Multi-Presentation] Before playback, pass an out-of-range index")
	public void testIF_549() throws Exception
	{
		Value p = new Value(4);
		
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		//The 1st presentation(Default) should be selected as users pass an out-of-range value
		CommonUtil.sleep(5);
		
		Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="testIF_550", Title="[Multi-Presentation] In content playback, pass an out-of-range index")
	public void testIF_550() throws Exception
	{		
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		//According to bug DOPE-549.
		
		Value p = new Value("-1");
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		
		CommonUtil.sleep(1);
		
		Assert.assertTrue("The InvalidVauleError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
	}
	
	@High
	@Testlink(Id="testIF_551", Title="[Multi-Presentation] In playback, pass a valid index to select different presentation")
	public void testIF_551() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		//The default presentation is selected
		CommonUtil.sleep(10);
		
		Value p = new Value(1);
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		
		//The 2nd presentation: French is selected
		CommonUtil.sleep(10);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_552", Title="[Multi-Presentation] In playback, pass an out-of-range index")
	public void testIF_552() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		//The default presentation is selected
		CommonUtil.sleep(10);
		
		Value p = new Value(4);
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		
		//The 1st presentation: French is still selected
		CommonUtil.sleep(5);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="testIF_553", Title="[Multi-Presentation] In playback, switch presentation in the loop of all possible index")
	public void testIF_553() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));

		CommonUtil.sleep(3);
		
		for(int i=1;i<20;i++)
		{
			Value p = new Value(i%4);
			playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
			
			CommonUtil.sleep(2);
			
			Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
			Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		}
	}
	
	@Low
	@Testlink(Id="testIF_560", Title="[Multi-Presentation] In playback, pass the presentation index which is already being played back")
	public void testIF_560() throws Exception
	{
		Value p = new Value(2);
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		
		CommonUtil.sleep(3);
		
		for(int i=1;i<20;i++)
		{
			p = new Value(2);
			playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
	
			CommonUtil.sleep(2);
			
			Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
			Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		}
	}
	
	@Low
	@Testlink(Id="testIF_561", Title="[Multi-Presentation] In paused sate, switch presentation then resume playback")
	public void testIF_561() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Multi_Presentation_AC4.mp4"));
		CommonUtil.sleep(5);
		
		playerWrapper.pausePlayback();
		CommonUtil.sleep(2);
		
		Value p = new Value(2);
		playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
		
		playerWrapper.resumePlayback();	
		CommonUtil.sleep(5);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@High
	@Testlink(Id="testIF_562", Title="[Main-asso mixing] Before playback, pass a valid value to mix in asso stream")
	public void testIF_562() throws Exception
	{
		Value p = new Value(-11);
		
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_563", Title="[Main-asso mixing] Before playback, pass -32(minimal value) to enable main stream only")
	public void testIF_563() throws Exception
	{
		Value p = new Value(-32);
		
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_564", Title="[Main-asso mixing] Before playback, pass 32(maximal value) to enable asso stream")
	public void testIF_564() throws Exception
	{
		Value p = new Value(32);
		
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(15);
		
		Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Low
	@Testlink(Id="testIF_565", Title="[Main-asso mixing] In content playback, pass an out-of-range mixing value")
	public void testIF_565() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		
		Value p = new Value(-33);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		CommonUtil.sleep(1);
		
		Assert.assertTrue("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		
		p = new Value(33);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		CommonUtil.sleep(1);
		
		Assert.assertTrue("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@High
	@Testlink(Id="testIF_567", Title="[Main-asso mixing] In playback, pass a valid value to mix in asso stream")
	public void testIF_567() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(5);
		
		Value p = new Value(2);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		CommonUtil.sleep(10);
		
		Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_568", Title="[Main-asso mixing] In playback, pass -32 to enable main stream only")
	public void testIF_568() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(5);
		
		Value p = new Value(-32);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		CommonUtil.sleep(10);
		
		Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_569", Title="[Main-asso mixing] In plaback, pass 32 to enable asso stream only")
	public void testIF_569() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(5);
		
		Value p = new Value(32);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		CommonUtil.sleep(10);
		
		Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_570", Title="[Main-asso mixing] In playback, switch bwteen -32 & 32 to toggle asso stream on/off")
	public void testIF_570() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		
		for(int i=0;i<15;i++)
		{
			Value p;
			if(i%2==0)
				p=new Value(-32);
			else
				p = new Value(32);
			
			playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
			
			CommonUtil.sleep(3);
			
			Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
			Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		}
	}
	
	@Low
	@Testlink(Id="testIF_571", Title="[Main-asso mixing] In playback, loop all possible maxing value")
	public void testIF_571() throws Exception
	{
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		
		for(int i=-32;i<32;i+=5)
		{
			Value p = new Value(i);
			
			playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
			
			CommonUtil.sleep(3);
			
			Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
			Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		}
	}
	
	@Low
	@Testlink(Id="testIF_572", Title="[Main-asso mixing] In paused state, set a valid value mix in asso stream, then resume playback")
	public void testIF_572() throws Exception
	{
		//Disable the associated stream
		gotoPlayingState(testSignals_MP4.get("Main_Asso_AC4.mp4"));
		CommonUtil.sleep(10);
		
		playerWrapper.pausePlayback();
		
		Value p = new Value(-5);
		playerWrapper.setProperty(AC4_DEC_MAIN_ASSOC_MIX, p);
		
		playerWrapper.resumePlayback();
		CommonUtil.sleep(10);
		
		Assert.assertFalse("The InvalidValueError exception should be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
		Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
	}
	
	@Medium
	@Testlink(Id="testIF_594", Title="[Multi-Presentation] Switch presentations in DASH playback")
	public void testIF_594() throws Exception
	{
		gotoPlayingState(testSignals_DASH.get("Adaptive_MultiPresentation_AC4"));

		for(int i=1;i<20;i++)
		{
			Value p = new Value(i%4);
			playerWrapper.setProperty(AC4_DEC_PRESENTATION_INDEX, p);
			
			CommonUtil.sleep(2);
			
			Assert.assertFalse("The InvalidValueError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG));
			Assert.assertFalse("The PropertyNotFoundError exception should NOT be thrown.", CommonUtil.getErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG));
		}
	}

	Player.PrepareStateListener setDecTargetRefLevel = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player)
		{
			Value p = new Value(-31);
			playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
		}
	};
	
	Player.PrepareStateListener setDlgEnhancement = new Player.PrepareStateListener() {
		@Override
		public void onPrepared(Player player) 
		{
			Value p = new Value(12);
			playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
		}
	};
	
	Player.PrepareStateListener setDapEnabled = new Player.PrepareStateListener() {
		
		@Override
		public void onPrepared(Player player) 
		{
			Value p = new Value(0);
			playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
		}
	};
	
	Player.PrepareStateListener setAllParameters = new Player.PrepareStateListener() {
		
		@Override
		public void onPrepared(Player player) 
		{
			Value p = new Value("-9");
			playerWrapper.setProperty(AC4_DEC_TARGET_REF_LEVEL, p);
			p = new Value(12);
			playerWrapper.setProperty(AC4_DEC_DIALOG_ENH_GAIN_INPUT, p);
			p = new Value(0);
			playerWrapper.setProperty(AC4_DEC_DAP_ENABLED, p);
			p = new Value(ChannelConfig.AC4DEC_OUT_CH_LT_RT.toString());
			playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
		}
	};
	
	Player.PrepareStateListener setChConfig = new Player.PrepareStateListener() {
		
		@Override
		public void onPrepared(Player player) 
		{
			Value p = new Value(ChannelConfig.AC4DEC_OUT_CH_LT_RT_PLII.toString());

			playerWrapper.setProperty(AC4_DEC_CHANNEL_CONFIG, p);
		}
	};
	
}
