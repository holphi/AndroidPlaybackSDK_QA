/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2013 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/

package com.dolby.application.infra.app;

import android.os.AsyncTask;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dolby.application.*;
import com.dolby.application.infra.debug.PcmDump;
import com.dolby.application.infra.renderer.VideoRenderer;
import com.dolby.infra.Player;
import com.dolby.infra.Player.CompletionListener;
import com.dolby.infra.Player.IllegalStateException;
import com.dolby.infra.Player.IllegalArgumentException;
import com.dolby.infra.Player.MediaInfo;
import com.dolby.infra.Player.PrepareStateListener;
import com.dolby.infra.Player.ErrorStateListener;
import com.dolby.infra.Player.PlayerState;
import com.dolby.infra.Player.Value;

public class AndroidPlayer extends Activity implements SurfaceHolder.Callback,
                                                       PrepareStateListener,                                                   
                                                       CompletionListener,
                                                       OnSeekBarChangeListener,
                                                       TimeSource
{
    private class RefreshLoopTask extends AsyncTask<Void, Void, Void> {

        private class SurfaceRefresher implements Runnable {
            public void run() {
                    AndroidPlayer.this.mVideoRenderer.refreshSurface();
                synchronized (this) {
                    this.notify();
                }
            }
        }

        protected Void doInBackground(Void... urls) {
            while (!isCancelled())
            {
                try {
                    SurfaceRefresher sr = new SurfaceRefresher();
                    synchronized (sr) {
                        AndroidPlayer.this.runOnUiThread(sr);
                        sr.wait();
                    }
                    // call refresh
                    final long sixty_fps_ms = (long) (1000.0 / 60.0);
                    Thread.sleep(sixty_fps_ms);
                } catch(InterruptedException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }
	
	
    private RefreshLoopTask mRefreshLoop;
    
	private static final String TAG = "INFRA_AndroidPlayer";


    // Controls if the app plays video or not
    private final boolean VIDEO_PLAYBACK_ENABLED = true;
    // Activates audio dumping to a pcm file
    private final boolean AUDIO_DUMP_ENABLED = false;
    // Stores URI to audio file
    private String mAudioTrack = "file:///sdcard/dolby/DOPE/media/Silent_640x480p_25fps_h264_51_192_ac4.mp4";
    // Stores video URI
    private String mVideoTrack = "/sdcard/dolby/DOPE/media/Silent_640x480p_25fps_h264_51_192_ac4.mp4";
    // File name for pcm dump
    private final String _debugAudioDumpFileName = "/sdcard/java.dump";
    
    
    private boolean isSurfaceViewCreated;

    /*
     * Currently Integration Framework does not decode and play video streams.
     * Video decoding and rendering is sideloaded into MediaCodec based solution
     * VideoPlayer module will use MediaCodec to decode and render video frames
     * It will also use playback position (time) from the Integration Framework
     * for proper A/V sync
     */
    // Reference to the Integration Framework based player.   
    private Player mInfraPlayer;
    //Injected by QA team
    public Player getPlayerInstance()
    {
    	return this.mInfraPlayer;
    }
    
    // Reference to the video playback based on MediaCodec API
    private VideoRenderer mVideoRenderer;
    
    // VIDEO
    // Flag used to indicate if the content has video
    private boolean _hasVideo = false;
    // Reference to the SurfaceView that will be used for video rendering
    private SurfaceView _videoSurface = null;
    //Injested by QA team
    public boolean isSurfaceViewCreated()
    {
    	return this.isSurfaceViewCreated;
    }

    //This variable will hold a reference to seek bar;
    private SeekBar seekBar;
    //This variable will hold a reference to time indicator, will use it to represent current time
    private TextView timeLabel;
    
    // In order to asynchronously update the progress bar, we create special
    // object called Handler
    // This is basically a message queue associated with the activity thread.
    private Handler mHandler = new Handler();
    // Define Runnable that will update progress and time
    // Runnable is an object that can do something inside the run() method.
    private Runnable mProgressRunnable = new Runnable() {
    	
	    @Override
	    public void run() {
	        if(mInfraPlayer != null){
	        	// Updating the progress position based on current playback position
	            long mCurrentPosition = 0;
	            long duration = 0;
	            
	            try {
					mCurrentPosition = mInfraPlayer.getPlaybackPosition();
					duration = mInfraPlayer.getPlaybackDuration();
				} catch (IllegalStateException e) {
					Log.e(TAG, "Failed to read current time", e);
				}

	            seekBar.setProgress((int)mCurrentPosition);
	            
	            //Let's inverse the progress
	            mCurrentPosition = duration - mCurrentPosition;

	            // convert progress position in milliseconds into minutes and seconds.
	            long minutes = mCurrentPosition/60000;
	            long seconds = (mCurrentPosition - minutes * 60000)/1000;
	            
	            String min;
	            String sec;
	            if (minutes < 10) {
	            	min = "0" + minutes;
	            } else{
	            	min = String.valueOf(minutes);
	            }

	            if (seconds < 10) {
	            	sec = "0" + seconds;
	            } else{
	            	sec = String.valueOf(seconds);
	            }
	            
	            // Here we use java variable to update the text in IU element.
	    		timeLabel.setText(min + ":" + sec);
	        }
	        
	        // Our runnable re-schedules itself with 1 second delay
	        mHandler.postDelayed(this, 1000);
	    }   	
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        //Keep screen on to prevent device from going to sleep mode while we are watching the movie
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.main);

        _videoSurface = (SurfaceView)findViewById(R.id.Videoview);
        _videoSurface.setVisibility(View.VISIBLE);
        
        //Link variable and he seek bar UI;
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        //Assign a listener, when user drags the seek bar, the activity will be notified
        timeLabel = (TextView)findViewById(R.id.idCurTime);
        
        _videoSurface.getHolder().addCallback(this);

        // create native engine
        try
        {
        	// Creating Integration Framework player
            mInfraPlayer = Player.create();
            
            mInfraPlayer.addPrepareStateListener(this);
            mInfraPlayer.addCompletionListener(this);
            
            mVideoRenderer = new VideoRenderer(mInfraPlayer);
        }
        catch (Exception e)
		{
			Log.e(TAG, "Unable to create Player instance. " + e.getMessage());
		}
        
        Log.d(TAG, "/onCreate()");
    }
    
    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()");
        // stop playback
        stopMediaPlayback();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");

        if (mRefreshLoop != null) {
            final boolean cancelled = mRefreshLoop.cancel(true);
            if (!cancelled) {
                Log.e(TAG, "Could not cancel refresh loop.");
            }
        }

        try 
        {
            mInfraPlayer.removePrepareStateListener(this);
            mInfraPlayer.removeCompletionListener(this);
        } catch (Exception e) 
        {
            e.printStackTrace();
        }

        mInfraPlayer.release();
    }
    
    //
    // Begin initial steps in preparation for starting playback.
    //
    public void prepare() throws IllegalStateException
    {
    	Log.d(TAG, "prepare()");
        
        // Call prepare and wait for onPrepared() to start the playback
        mInfraPlayer.prepare();
    }
    
    //
    // Begin playback.
    //
    public void start() throws IllegalStateException
    {
        Log.d(TAG, "start()");
        
        mRefreshLoop = new RefreshLoopTask(); 
        mRefreshLoop.execute();
        
        // start audio playback
        mInfraPlayer.start();
        
		// Tell the seekbar what is the maximum progress (file duration)
		seekBar.setMax((int)mInfraPlayer.getPlaybackDuration());
		//delete all pending updates of the seekbar
		mHandler.removeCallbacks(mProgressRunnable);
		// post new seekbar update
		mHandler.post(mProgressRunnable);
		
		//Initiate timeout mechanism
		//startUiTimeout();
    }
    
    //Pause playback
    public void pause()
    {
        Log.d(TAG, "pause()");
        // we don't need to try to update progress bar when we are in paused
        // state
        mHandler.removeCallbacks(mProgressRunnable);

        //stopUiTimeout();
        try
        {
        	mInfraPlayer.pause();
        }catch(IllegalStateException e)
        {
        	Log.e(TAG,"An illegal state exception is thrown when invoking pause()");
        }
    }
    
    //Resume playback
    public void resume()
    {
    	Log.d(TAG, "resume()");
        
    	try
    	{
    		mInfraPlayer.start();
    	}catch(IllegalStateException ex)
    	{
    		Log.e(TAG,"An illegal state exception is thrown when invoking resume()");
    	}
    	
		mHandler.removeCallbacks(mProgressRunnable);
		// post new seekbar update
		mHandler.post(mProgressRunnable);
		
		//startUiTimeout();
    }
    
    public void stop() throws IllegalStateException 
    {
        Log.d(TAG, "stop()");
        
        mHandler.removeCallbacks(mProgressRunnable);
        seekBar.setProgress(0);
        timeLabel.setText("--:--");

        if (mRefreshLoop != null) {
            final boolean cancelled = mRefreshLoop.cancel(true);
            if (!cancelled) {
                Log.e(TAG, "Could not cancel refresh loop.");
            }
        }
        

        // stop audio player
        mInfraPlayer.stop();
    }
    
    //Injected by QA team
    public void addOnPreparedListenerForPlayer()
    {
    	try {
			mInfraPlayer.addPrepareStateListener(this);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //Injected by QA team
    public void rmOnPreparedListenerForPlayer()
    {
    	try {
			mInfraPlayer.removePrepareStateListener(this);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void updateMediaInfo() 
    {
        Player player = mInfraPlayer;

        try 
        {
            MediaInfo mediaInfo = player.getMediaInfo();
            _hasVideo = mediaInfo.hasVideo(); //TODO: mediaInfo.hasVideo() (tracks not implemented for local yet);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Illegal state error", e);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged()");
        
        try {
            // TODO: Why do we hardcode this instead of using holder, width and height????
            mVideoRenderer.setDisplay(_videoSurface.getHolder().getSurface(), 960,720);
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged() failed to set display!");
            finish();
        }
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        this.isSurfaceViewCreated = true;
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
    }

    public void startMediaPlayback()
    {
        Log.d(TAG, "startMediaPlayback()");

        try
        {
        	// Set audio file.
        	mInfraPlayer.setMediaUri(mAudioTrack);
        	
            /*if (VIDEO_PLAYBACK_ENABLED) {
                // Set video file to play
                mVideoPlayer.setDataSource(mVideoTrack);
            }*/
        	
        	prepare();
        }
        catch (IllegalStateException e) {
            Log.d(TAG, "start() : Invalid state to call this method.");
        	stopMediaPlayback();
        	return;
        }
        catch (Exception e) {
            Log.d(TAG, "start() : Failed to playback media.");
            stopMediaPlayback();
            return;
        }

        Log.d(TAG, "/startMediaPlayback()");
    }
    
    public void startMediaPlayback(String url)
    {
        Log.d(TAG, "startMediaPlayback()");

        try
        {
        	// Set audio file.
        	mInfraPlayer.setMediaUri(url);
        	
            /*if (VIDEO_PLAYBACK_ENABLED) {
                // Set video file to play (With file:\\ prefix removed)
                mVideoPlayer.setDataSource(url.substring(7));
            }*/
        	
        	prepare();
        }
        catch (IllegalStateException e) {
            Log.d(TAG, "start() : Invalid state to call this method.");
        	stopMediaPlayback();
        	return;
        }
        catch (Exception e) {
            Log.d(TAG, "start() : Failed to playback media.");
            stopMediaPlayback();
            return;
        }

        Log.d(TAG, "/startMediaPlayback()");
    }
    

    public void stopMediaPlayback()
    {
    	Log.d(TAG, "stopMediaPlayback()");
    	
        try
        {
        	stop();
        }
        catch (IllegalStateException e)
        {
            Log.d(TAG, "stopMediaPlayback() : Invalid player state when calling stop().");
        }

    }

    public boolean isAudioPlaying() 
    {
    	try {
			return (mInfraPlayer.getState() == PlayerState.PLAYING) || (mInfraPlayer.getState() == PlayerState.PAUSED);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }

    public boolean isVideoPlaying() 
    {
    	return true;  // Hack: assuming always true while debugging YUV playback;
    }

    public boolean isPlaying() 
    {
        return isAudioPlaying() || isVideoPlaying();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK))  {
        	
            Log.d(TAG, "onKeyDown(KEYCODE_BACK)");

            if (isPlaying()) {
                // Update play/pause button.
                stopMediaPlayback();

                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /*
     * This callback is delivered from Integration Framework player engine
     * 
     * Client is notified that the engine is ready for playback
     * 
     * @see
     * com.dolby.dope.Player.PrepareListener#onPrepared(com.dolby.dope.Player)
     */
    @Override
    public void onPrepared(Player mInfraPlayer) 
    {
    	/*Log.d(TAG, "onPrepared()");
    	try
        {
    		
    		//updateMediaInfo(); //To check current player has video stream
    		//_hasVideo = mInfraPlayer.hasVideo();
    		
    		_hasVideo = true;
    		
            // Call videoPlayer.setDataSource after prepare has completed
            // before we can configure video player we need to make sure that video container has been parsed
        	if (_hasVideo) {
                // Set video file to play
                mVideoPlayer.setDataSource(mVideoTrack);
            }
        	
            start();
        }
        catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    	try 
    	{
			mInfraPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    /*
     * This callback is delivered from Integration Framework player engine
     * 
     * Client is notified that audio playback has completed
     * 
     * @see
     * com.dolby.dope.Player.CompletionListener#onCompletion(com.dolby.dope.
     * Player)
     */    
	@Override
	public void onCompletion(Player mInfraPlayer) {
        Log.d(TAG, "onCompletion()");		
		stopMediaPlayback();
	}
	
    /*
     * This callback is generated by a seekbar UI element
     * 
     * The callback is given every time the progress bar level has changed
     * 
     * @see
     * android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android
     * .widget.SeekBar, int, boolean)
     */	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// Make sure this is not a regular progress update that we do every second
		// but the update initiated by the user (dragging the seekbar)
        if(mInfraPlayer != null && fromUser) {
            try 
            {
                // SeekTo is not yet supported, therefore calling this API will
                // not work properly				
				mInfraPlayer.seekTo(progress);
			}catch(IllegalArgumentException e)
			{
				e.printStackTrace();
			}catch (IllegalStateException e) {
				e.printStackTrace();
			}
        }
	}
	
    /*
     * Callback from a SeekBar element telling that user has started a touch
     * motion
     * 
     * @see
     * android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android
     * .widget.SeekBar)
     */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}
	
    /*
     * Callback from a SeekBar element telling that user has just finished a
     * touch motion
     * 
     * @see
     * android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android
     * .widget.SeekBar)
     */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {	
	}
	
	 /*
     * This method is to implement TimeSource interface that is used by Video
     * Player.
     * 
     * The method returns current playback time to be used for A/V sync
     * 
     * @see com.dolby.dope.TimeSource#getTime()
     */
    @Override
    public long getTime() {
        long time = 0;

        if (null != mInfraPlayer) {
            try {
                time = mInfraPlayer.getPlaybackPosition();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return time;
    }
    
}