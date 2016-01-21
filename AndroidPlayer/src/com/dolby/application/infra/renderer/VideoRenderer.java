package com.dolby.application.infra.renderer;

import com.dolby.infra.Player;

import android.view.Surface;

/**
 * Android video renderer class for rendering frames onto Android devices.
 * <p>
 * The API allows the client to specify the entity to render the video onto. The
 * {@link #setDisplay(android.view.Surface, int, int) setDisplay} method can be
 * used to specify the {@link android.view.Surface} object to render video frames.
 * </p>
 */
public class VideoRenderer
{
    private static final String TAG = "VideoRenderer";

    //
    // Instance of Player associated with this renderer.
    //
    private Player mPlayer = null;

    //
    // Declare private native JNI methods.
    //
    private native final int videoRendererSetDisplay(Player player, Object surface, int width, int height);
    private native final void videoRendererRefreshSurface(Player player);
    private native long videoGetTimeUs(Player player);

    /**
     * Construct an instance of VideoRenderer that is associated with Player instance "player".
     *
     * @param player        Instance of player this renderer shall render frames from.
     *
     * @throws Exception    If an invalid Player instance is provided, this will be thrown.
     */
    public VideoRenderer(Player player) throws Exception
    {
        if (null == player)
        {
            throw new Exception("A valid Player instance must be provided.");
        }
        mPlayer = player;
    }

    /**
     * Set the entity to render video frames onto.
     *
     * @param surface   The destination where video frames are rendered.
     * @param width     Width of the video frames in pixels.
     * @param height    Height of the video frames in pixels.
     *
     * @throws Exception
     *                  Thrown if either width or height is non-positive.
     *                  Thrown if the provided surface is not valid.
     *
     * @return          Returns 0 on success, otherwise error occurred.
     */
    public int setDisplay(Surface surface, int width, int height) throws Exception
    {
        if (0 >= width || 0 >= height)
        {
            throw new Exception("Both the width and height must be positive.");
        }
        else if (null == surface)
        {
            throw new Exception("An invalid surface was provided.");
        }
        return videoRendererSetDisplay(mPlayer, surface, width, height);
    }

    /**
     * Causes renderer to draw current active video frame on rendering surface
     */
    public void refreshSurface()
    {
        videoRendererRefreshSurface(mPlayer);
    }
}

