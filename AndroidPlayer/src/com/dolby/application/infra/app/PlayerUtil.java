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

import java.io.File;

import android.graphics.Point;
import android.util.Log;

public class PlayerUtil {

    private static final String TAG = "PlayerUtil";

    public static boolean isStreaming(String mediapath) {
        return (mediapath.startsWith("http://") || mediapath.startsWith("rtsp://") || mediapath.startsWith("https://"));
    }

    public static boolean fileExists(String filepath)
    {
        Log.d(TAG, "fileExists(" + filepath + ")");
        if (filepath == null)
        {
            return false;
        }

        File f = new File(filepath);
        return f.exists();
    }

    public static Point getFitVideoFrameSize(int container_w, int container_h,
                                            int video_w, int video_h) {
        Point size = new Point();
        size.x = video_w;
        size.y = video_h;

        float ratio_w = container_w * 1.0f / video_w;
        float ratio_h = container_h * 1.0f / video_h;
        if (ratio_w < ratio_h) {
            size.x = container_w;
            size.y = (int)(ratio_w * video_h);
        } else {
            size.y = container_h;
            size.x = (int)(ratio_h * video_w);
        }

        return size;
    }
}
