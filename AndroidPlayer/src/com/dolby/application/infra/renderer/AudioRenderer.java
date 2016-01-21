package com.dolby.application.infra.renderer;

import com.dolby.infra.Player;


/**
 * Android audio renderer class allows reading audio date from the player engine.
 * <p>
 * This class wraps several JNI calls that supply the client with PCM audio data.
 * </p>
 */
public final class AudioRenderer {
    /*
     * Disallow instantiation of the renderer through hiding its constructor.
     * If someone try to instantiate via reflection, throw an exception
     */
    private AudioRenderer() {
    	throw new RuntimeException("Cannot instantiate static class " + AudioRenderer.class.getName());
    }

    /** Reads buffer of audio PCM data of specified size
     *
     * This method access player engine through JNI connection
     *
     * @param player		Instance of the player this renderer shall draw data from.
     * @param audioData		Reference to the buffer audio data is copied to
     * @param sizeInBytes	Size of the buffer to read in bytes
     *
     * @return 				0 if read is successful, value greater than 0 otherwise
     */
    public native static final int audioBufferRead(Player player, byte[] audioData, int sizeInBytes);
}
