package com.dolby.dope.test;

import android.test.UiThreadTest;
import android.util.Log;
import android.view.Surface;

import com.dolby.infra.Player;
import com.dolby.infra.Player.IllegalStateException;
import com.dolby.infra.Player.IllegalArgumentException;

import com.dolby.infra.Player.PlayerState;
import com.dolby.infra.Player.Value;
import com.dolby.infra.Player.MediaInfo;
import com.dolby.infra.Player.MediaInfo.MediaType;
import com.dolby.infra.Player.Version;

import com.dolby.infra.Player.InstantiationException;
import com.dolby.infra.Player.ErrorStateListener;
import com.dolby.infra.Player.InvalidValueException;
import com.dolby.infra.Player.PrepareStateListener;
import com.dolby.infra.Player.PropertyNotFoundException;
import com.dolby.infra.Player.StreamChangedListener;
import com.dolby.infra.Player.CompletionListener;

import com.dolby.application.infra.app.AndroidPlayer;

import com.dolby.dope.test.util.CommonUtil;
import com.dolby.dope.test.util.CommonUtil.ErrorFlag;

public class PlayerWrapper 
{
	private final String TAG = "PlayerWrapper";
	
	private AndroidPlayer mPlayerActivity = null;
	private Player mPlayer = null;
	
	
	//Intermediate variables for sending operation requests on UI threads;
	private boolean mIsAudioOnlyFile = false;
	private String mFilePath;
	private String mUrl;
	private boolean mLooping = false;
	private String mVersion = "";
	
	private Value mPropertyValue = null;
	private MediaInfo mMediaInfo = null;
	private int mSelectedTrackId = -1;
	private int mSelectedStreamId = -1;
	
	//Intermediate interface for event handling;
	private ErrorStateListener mErrorListener = null;
	private PrepareStateListener mPrepareListener = null;
	private CompletionListener mCompletionListener = null;
	private StreamChangedListener mStreamChangedListener = null;
	
	//The second parameter is temporary 
	public PlayerWrapper(AndroidPlayer testActivity, boolean isAudioOnlyFile)
	{
		this.mPlayerActivity = testActivity;
		
		this.mPlayer = testActivity.getPlayerInstance();
	}

	/*public void testCreateWithParams(String url)
	{
		Log.i(TAG, String.format("Player.create(url:=%s", url));
		
		this.mUrl = url;
		
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						mPlayer = Player.create(mUrl);
					}catch (InstantiationException e) 
					{
						Log.i(TAG, "An InstantiationException is thrown when calling create(String url)");
						CommonUtil.setErrorFlag(ErrorFlag.INSTANTIATION_ERROR_FLAG, true);
					}
				}
			}
		));
		
		this.mPlayer = mPlayerActivity.getPlayerInstance();
	}*/
	
	public void testCreate()
	{
		Log.i(TAG, "Player.create()");

		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						mPlayer = Player.create();
					}catch (InstantiationException e) 
					{
						Log.i(TAG, "An InstantiationException is thrown when calling create()");
						CommonUtil.setErrorFlag(ErrorFlag.INSTANTIATION_ERROR_FLAG, true);
					}
				}
			}
		));
		
		this.mPlayer = mPlayerActivity.getPlayerInstance();
	}
	
	//Check if the instance of surface view is created;
	public boolean isSurfaceViewCreated()
	{
		Log.i(TAG, String.format("isSurfaceViewCreated=%b", mPlayerActivity.isSurfaceViewCreated()));
		
		return mPlayerActivity.isSurfaceViewCreated();
	}
	
	@UiThreadTest
	public void setSource(String url)
	{
		Log.i(TAG, String.format("Player.setSource(url:=%s).", url));
		
		this.mUrl = url;

		try
		{
			mPlayer.setMediaUri(mUrl);
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling setSource().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}catch (IllegalArgumentException ex)
		{
			Log.i(TAG, "An IllegalArgumentException is thrown when calling setSource().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void prepare()
	{
		Log.i(TAG, "Player.prepare()");
		
		try
		{
			mPlayer.prepare();
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling prepare().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	public PlayerState getState()
	{
		Log.i(TAG, "Player.getState()");
		
		PlayerState state = PlayerState.ERROR;
		
		try
		{
			state = mPlayer.getState();
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalState error is thrown when callling prepare().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return state;
	}
	
	public long getPlaybackDuration()
	{
		Log.i(TAG, "Player.getDuration()");
		
		long duration = -9999;
		try
		{
			duration = mPlayer.getPlaybackDuration();
		}catch(IllegalStateException e)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling getDuration().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return duration;
	}
	
	@UiThreadTest
	public void start()
	{
		Log.i(TAG, String.format("Player.start()."));
		
		try 
		{
			mPlayer.start();
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling start().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	public void stop()
	{
		Log.i(TAG, String.format("Player.stop()"));
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						mPlayer.stop();
					}catch (IllegalStateException ex)
					{
						Log.i(TAG, "An IllegalStateException is thrown when calling stop().");
						CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
					}
				}
			}
		));
	}
		
	public void release()
	{
		Log.i(TAG, String.format("Player.release()."));
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayer.release();
				}
			}
		));
	}
	
	public void pause()
	{
		Log.i(TAG, String.format("Player.pause()."));
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					try 
					{
						mPlayer.pause();
					}catch(IllegalStateException ex)
					{
						Log.i(TAG, "An IllegalStateException is thrown when calling pause().");
						CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
					}
				}
			}
		));
	}
	
	@UiThreadTest
	public void seekTo(int milliseconds)
	{
		Log.i(TAG, String.format("Player.seekTo(milliseconds=%d).", milliseconds));
		
		try
		{
			mPlayer.seekTo(milliseconds);
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling seekTo().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}catch(IllegalArgumentException ex)
		{
			Log.i(TAG, "An InvalidValueException is thrown when calling seekTo().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void switchStream(int trackId, int streamId)
	{
		Log.i(TAG, String.format("Player.switchStream(trackId=%d, streamId=%d", trackId, streamId));
		
		try
		{
			mPlayer.switchStream(trackId, streamId);
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling selectStream().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}catch(IllegalArgumentException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling selectStream().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
		}
	}
	

	public boolean canSeek()
	{	
		boolean isSeekable = false;
		
		try 
		{
			isSeekable = mPlayer.canSeek();
		}catch(IllegalStateException e) 
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling isSeekable().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return isSeekable;
	}
	
	/*public boolean hasAudio() throws IllegalStateException
	{
		boolean hasAudio = false;
		
		try 
		{
			hasAudio = mPlayer.hasAudio();
		}catch(IllegalStateException e) 
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling hasAudio().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return hasAudio;
	}
	
	public boolean hasVideo()
	{
		boolean hasVideo = false;
		
		try 
		{
			hasVideo = mPlayer.hasVideo();
		}catch(IllegalStateException e) 
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling hasVideo().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return hasVideo;
	}*/
	
	public long getPlaybackPosition()
	{
		long currTime = -9999;
		
		try 
		{
			currTime = mPlayer.getPlaybackPosition();
		}catch(IllegalStateException e) 
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling PlayingbackPosition.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return currTime;
	}
	
	@UiThreadTest
	public void addPrepareStateListener(PrepareStateListener prepareListener)
	{
		this.mPrepareListener = prepareListener;
		
		Log.i(TAG, "Player.addPreapreListener()");
		
		try 
		{
			mPlayer.addPrepareStateListener(mPrepareListener); 
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling addPrepareListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void removePrepareStateListener(PrepareStateListener prepareListener)
	{
		this.mPrepareListener = prepareListener;
		Log.i(TAG, "Player.removePrepareListener()");
		
		try 
		{
			mPlayer.removePrepareStateListener(mPrepareListener); 
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling removePrepareListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void addCompletionListener(CompletionListener completionListener)
	{
		this.mCompletionListener = completionListener;
		Log.i(TAG, "Player.addCompletionListener()");
		
		try 
		{
			mPlayer.addCompletionListener(mCompletionListener);
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling addCompletionListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void removeCompletionListener(CompletionListener completionListener)
	{
		this.mCompletionListener = completionListener;
		Log.i(TAG, "Player.removeCompletionListener()");
		
		try 
		{
			mPlayer.removeCompletionListener(mCompletionListener);
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling removeCompletionListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void addErrorStateListener(ErrorStateListener errorMessageListener)
	{
		this.mErrorListener = errorMessageListener;
		Log.i(TAG, "Player.addErrorMessageListener()");
		
		try 
		{
			mPlayer.addErrorStateListener(mErrorListener);
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling addErrorListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void addStreamChangedListener(StreamChangedListener streamChangedListener)
	{
		this.mStreamChangedListener = streamChangedListener;
		Log.i(TAG, "Player.addStreamChangedListener()");
		
		try 
		{
			mPlayer.addStreamChangedListener(mStreamChangedListener);
			
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling addErrorListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void removeErrorStateListener(ErrorStateListener errorMessageListener)
	{
		this.mErrorListener = errorMessageListener;
		Log.i(TAG, "Player.removeErrorMessageListener()");
		
		try 
		{
			mPlayer.removeErrorStateListener(mErrorListener);
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling removeErrorListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public void removeStreamChangedListener(StreamChangedListener streamChangedListener)
	{
		this.mStreamChangedListener = streamChangedListener;
		
		Log.i(TAG, "Player.removeStreamChangedListener()");
		
		try 
		{
			mPlayer.removeStreamChangedListener(mStreamChangedListener);
			
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling removeStreamChangedListener.");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
	}
	
	@UiThreadTest
	public boolean setProperty(String name, Value value)
	{
		boolean ret = false;
		Log.i(TAG, String.format("Player.setProperty(name:=%s, value:=%s)", name, value.toString()));
		
		try
		{
			mPlayer.setProperty(name, value);
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling setProperty().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		catch(PropertyNotFoundException ex)
		{
			Log.i(TAG, "An PropertyNotFoundException is thrown when calling setProperty().");
			CommonUtil.setErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG, true);
		}catch(InvalidValueException ex)
		{
			Log.i(TAG, "An InvalidValueError is thrown when calling setProperty().");
			CommonUtil.setErrorFlag(ErrorFlag.INVALID_VALUE_ERROR_FLAG, true);
		}
				
		return ret;
	}
	
	@UiThreadTest
	public Value getProperty(String name)
	{
		this.mPropertyValue = null;
		Log.i(TAG, "Player.getProperty()");
		
		try 
		{
			mPropertyValue = mPlayer.getProperty(name);	
		}catch (IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling getProperty().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		catch(PropertyNotFoundException ex)
		{
			Log.i(TAG, "An PropertyNotFoundException is thrown when calling getProperty().");
			CommonUtil.setErrorFlag(ErrorFlag.PROP_NOT_FOUND_ERROR_FLAG, true);
		}
		
		return this.mPropertyValue;
	}

	@UiThreadTest
	public MediaInfo getMediaInfo()
	{
		this.mMediaInfo = null;
		Log.i(TAG, "Player.getMediaInfo()");
		
		try
		{
			mMediaInfo = mPlayer.getMediaInfo();
		}catch(IllegalStateException ex)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling getMediaInfo().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return this.mMediaInfo;
	}
	
	@UiThreadTest
	public int getPlayingTrack(MediaType trackType)
	{
		Log.i(TAG, String.format("Player.getSelectedTrack(trackType=%d)", trackType.ordinal()));
		
		this.mSelectedTrackId = -1;
		
		try
		{
			mSelectedTrackId = mPlayer.getPlayingTrack(trackType);
		}catch(IllegalStateException e)
		{
			Log.i(TAG, "An IllegalStateException is thrown when calling getSelectedTrack().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		return this.mSelectedTrackId;
	}
	
	@UiThreadTest
	public int getPlayingStream(int trackId)
	{
		Log.i(TAG, String.format("Player.getSelectedStream(trackId=%d)", trackId));
		
		this.mSelectedStreamId = -1;
		try 
		{
			mSelectedStreamId = mPlayer.getPlayingStream(trackId);
		}catch (IllegalArgumentException e) 
		{
			Log.i(TAG, "An IllegalArgumentException is thrown when calling getPlayingStream().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_ARGUMENT_ERROR_FLAG, true);
		}catch (IllegalStateException e) {
			
			Log.i(TAG, "An IllegalStateException is thrown when calling getPlayingStream().");
			CommonUtil.setErrorFlag(ErrorFlag.ILLEGAL_STATE_ERROR_FLAG, true);
		}
		
		return this.mSelectedStreamId;
	}
	
	public Version getVersion()
	{
		return mPlayer.getVersion();
	}
	
	public void startPlayback(String url)
	{
		Log.i(TAG, "startPlayback() doesn't belong to Player API.");
		this.mUrl = url;
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayerActivity.startMediaPlayback(mUrl);
				}
			}
		));
	}
	
	public void pausePlayback()
	{
		Log.i(TAG, "pausePlayback() doesn't belong to Player API.");
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayerActivity.pause();
				}
			}
		));
	}
	
	public void resumePlayback()
	{
		Log.i(TAG, "resumePlayback() doesn't belong to Player API.");
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayerActivity.resume();
				}
			}
		));
	}
	
	public void stopPlayback()
	{
		Log.i(TAG, "stopPlayback() doesn't belong to Player API.");
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayerActivity.stopMediaPlayback();
				}
			}
		));
	}
	
	public void rmOnPreparedListener()
	{
		mPlayerActivity.runOnUiThread
		(
			new Thread(new Runnable()
			{
				public void run()
				{
					mPlayerActivity.rmOnPreparedListenerForPlayer();
				}
			}	
		));
	}
	
	//Call AndroidPlayer.finish();
	public void finish()
	{
		Log.i(TAG, "Call AndroidPlayer.finish()");
		mPlayerActivity.finish();
	}

}