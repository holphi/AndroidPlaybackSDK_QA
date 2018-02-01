package com.foo.dope.test;

import junit.framework.Assert;

import com.foo.infra.Player.PlayerState;
import com.foo.dope.test.util.CommonUtil;
import com.foo.test.annotation.High;
import com.foo.test.annotation.Testlink;

/*
 * This suite is mainly used to quick check specified nodes are integrated well
 * */
public class DpfNodesCheck extends BaseTests
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
	@Testlink(Id="IF-161", Title="DDP: Play a ddp content")
	public void testIF_161() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("gotye_ddp_13s.mp4"));
		CommonUtil.sleep(5);
		
		Assert.assertEquals("The player state should be playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
	
	@High
	@Testlink(Id="IF-161", Title="NGC: Play an AC-4 content")
	public void testIF_162() throws Exception
	{
		playerWrapper.startPlayback(testSignals_MP4.get("trim_ac4_13s.mp4"));
		CommonUtil.sleep(5);
		
		Assert.assertEquals("The player state should be playing state", PlayerState.PLAYING, playerWrapper.getState());
	}
}
