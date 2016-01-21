package com.dolby.application.infra.debug;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PcmDump {

    private static final String TAG="PcmDump";
    OutputStream mOutStream = null;
    private static boolean isDumpEnabled = true;

    public static final void setEnableDump(final boolean enable) {
        isDumpEnabled = enable;
    }

    public PcmDump(String filePath) {

        if (isDumpEnabled) {
            init(filePath);
        }
    }
    private void init(String filePath) {
        try {
            mOutStream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            if (mOutStream != null) {
                try {
                    mOutStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean play(String file) {
        //TODO: Add playback code here later
        return true;
    }

    public boolean write(byte[] buffer, int offset, int size) {

        if (!isDumpEnabled) {
            return true;
        }

        if ( (null != buffer) && (size > 0) && (null != mOutStream) ) {
            try {
                mOutStream.write(buffer, offset, size);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean write(byte[] buffer) {

        if (!isDumpEnabled) {
            return true;
        }

        if ( (null != buffer) && (null != mOutStream) ) {
            try {
                mOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public boolean close() {
        if (mOutStream != null) {
            try {
                mOutStream.flush();
                mOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    mOutStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }
}
