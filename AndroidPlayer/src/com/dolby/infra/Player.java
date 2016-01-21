package com.dolby.infra;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dolby.infra.Player.MediaInfo.MediaType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Android API to the Integration Framework Media Player.
 * <p>
 * This API provides functionality to prepare a media resource (local file system file or
 * a DASH streaming media) and playback the media resource. Functions including pausing,
 * seeking and stopping playback of the media.
 * </p>
 * <p>
 * The player operates as a state machine. The API allows this state to be queried as well
 * as other information about the media such as the duration, current playback position,
 * whether the media contains audio and/or video.
 * </p>
 * <p>
 * The playback characteristics of the player can be further refined by setting parameters.
 * The API provides a {@link #setProperty(String, Value) setProperty} method to allow
 * the client to modify the playback behavior.
 * </p>
 * <p>
 * A set of Listener interfaces associated with the player are defined so that the client can
 * register them with the player to be informed of asynchronous events and obtain information
 * about the status of the player.
 * </p>
 */
public final class Player
{
    private static final String TAG = Player.class.getSimpleName();

    /**
     * Player states.
     */
    public enum PlayerState
    {
        /** The player has been created and ready for use. */
        IDLE,
        /** The player has been initialized with a media resource, ready to
        be prepared for playback. */
        INITIALIZED,
        /** The player is preparing for playback. This is a transitional state. */
        PREPARING,
        /** The player is ready to begin playback. Media information can be queried. */
        PREPARED,
        /** The player is currently playing media. */
        PLAYING,
        /** The player is paused. */
        PAUSED,
        /** The player is stopped. Playback cannot be resumed. The player requires initializing and preparing. */
        STOPPED,
        /** The player has entered an unrecoverable error state */
        ERROR;

        private static PlayerState fromInt(int id) {
            if ((id >= 0) && (id < mValuesCache.length)) {
                return mValuesCache[id];
            } else {
                return ERROR;
            }
        }

        /*
         * Enum.values() is expensive operation.
         * Therefore we keep static cache and reuse it all the time
         */
        private static PlayerState[] mValuesCache = PlayerState.values();
    }

    /**
     * Exception that is thrown when creating a Player instance fails.
     */
    public static class InstantiationException extends Exception { }

    /**
     * Exception that is thrown when calling a Player method on a Player instance that is in
     * an unsupported state.
     */
    public static class IllegalStateException extends Exception { }

    /**
     * Exception that is thrown when calling a Player method on a Player instance with an incorrect
     * argument.
     */
    public static class IllegalArgumentException extends Exception { }

    /**
     * An exception that indicates that the property does not exist.
     */
    public static class PropertyNotFoundException extends Exception { }

    /**
     * An exception that indicates setting an invalid value for the property.
     */
    public static class InvalidValueException extends Exception { }

    //
    // Throw InstantiationError exception from the JNI layer.
    //
    private void playerThrowInstantiationException(String message) throws InstantiationException
    {
        throw new InstantiationException();
    }

    //
    // Throw IllegalStateException exception from the JNI layer.
    //
    private void playerThrowIllegalStateException(String message) throws IllegalStateException
    {
        throw new IllegalStateException();
    }

    //
    // Throw IllegalArgumentException exception from the JNI layer.
    //
    private void playerThrowIllegalArgumentException(String message) throws IllegalArgumentException
    {
        throw new IllegalArgumentException();
    }

    //
    // Throw PropertyNotFoundException exception from the JNI layer.
    //
    private void playerThrowPropertyNotFoundException(String message) throws PropertyNotFoundException
    {
        throw new PropertyNotFoundException();
    }

    //
    // Throw InvalidValueException exception from the JNI layer.
    //
    private void playerThrowInvalidValueException(String message) throws InvalidValueException
    {
        throw new InvalidValueException();
    }

    //
    // Declare private native JNI methods.
    //
    private native final int playerCreate(Object player_this);
    private native final void playerRelease();
    private static native final void playerGetVersion(Map<String, Value> itemBundle);
    private native final void playerPrepare() throws IllegalStateException;
    private native final void playerStart() throws IllegalStateException;
    private native final void playerPause() throws IllegalStateException;
    private native final void playerSeekTo(long milliseconds) throws IllegalStateException, IllegalArgumentException;
    private native final void playerStop() throws IllegalStateException;
    private native final void playerSetMediaUri(String uri) throws IllegalStateException, IllegalArgumentException;
    private native final int playerGetState() throws IllegalStateException;
    private native final boolean playerCanSeek() throws IllegalStateException;
    private native final long playerGetPlaybackPosition() throws IllegalStateException;
    private native final long playerGetPlaybackDuration() throws IllegalStateException;
    private native final String playerGetProperty(String name) throws PropertyNotFoundException, IllegalStateException;
    private native final void playerSetProperty(String name, String value) throws InvalidValueException, PropertyNotFoundException, IllegalStateException;
    private native final void playerSelectTrack(int trackIdx) throws IllegalArgumentException, IllegalStateException;
    private native final void playerDeselectTrack(int trackIdx) throws IllegalArgumentException, IllegalStateException;
    private native final int playerGetPlayingTrack(int trackType) throws IllegalStateException;
    private native final void playerSwitchStream(int trackIdx, int streamIdx) throws IllegalArgumentException, IllegalStateException;
    private native final int playerGetPlayingStream(int trackIdx) throws IllegalArgumentException, IllegalStateException;


    //This method prepares communication channel between JNI and the player
    private static native final void engineInit();

    /**
     * Create a new player instance.
     *
     * @return      New instance of the Player object.
     *
     * @throws Player.InstantiationException
     *              Thrown when there is a problem initializing the player.
     */
    public static Player create() throws InstantiationException
    {
        Player player;
        try
        {
            player = new Player();
            player.playerCreate(new WeakReference<Player>(player));
        }
        catch (Exception e)
        {
            throw new InstantiationException();
        }
        return player;
    }

    /**
     * Release resources used by the player.
     *
     * This method may be called on a player instance in any state.
     */
    public void release()
    {
        playerRelease();
    }

    /**
     * Set the media resource.
     *
     * On success, the player will transition to the INITIALIZED state.
     *
     * @param uri   A URI specifying the location of the media to play.
     *              Use empty string if not provided.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player is in one of the following states:
     *              PREPARING, PREPARED, PAUSED, PLAYING
     *
     * @throws Player.IllegalArgumentException
     *              Thrown if the scheme in the URI is non-existent or not supported.
     *              Only the following schemes are supported: file, http
     *
     */
    public void setMediaUri(String uri) throws IllegalStateException, IllegalArgumentException
    {
        playerSetMediaUri(uri);
    }

    /**
     * Begin initial steps in preparation for starting playback.
     *
     * This method is non-blocking. This call transitions the player into the
     * PREPARING state and the play cannot be interrupted during this time.
     * Once preparation is complete, an asynchronous callback
     * to the PrepareStateListener's onPrepared() handler is executed. The
     * player would have transitioned into the PREPARED state. Once this
     * handler has been invoked, it is legal to call start().
     *
     * If the media is accessed over a network, but the network is unavailable,
     * then calling prepare() will cause the player to enter the ERROR
     * state, and will call the onError callback of any attached
     * ErrorStateListener objects.
     *
     * If the media is accessed over a network, and the network connection is lost
     * and is not re-connected within a timeout of 1 minute, the player will enter
     * the ERROR state, and will call the onError attached ErrorStateListener objects.
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, PREPARING, PREPARED, PLAYING, PAUSED, STOPPED, ERROR
     */
    public void prepare() throws IllegalStateException
    {
        playerPrepare();
    }

    /**
     * Begin playback.
     *
     * Transitions the player to the PLAYING state.
     *
     * This method requires the player has been prepared by calling prepare()
     * and the PrepareStateListener's onPrepared() handler has been received.
     *
     * If playback of the media reaches the end, the player will call the
     * CompletionListener::onCompletion callback of any attached CompletionListener
     * objects.
     *
     * If start() is invoked with streaming content without network connectivity, the player
     * shall transition to the ERROR state and onError callback shall be invoked.
     *
     * If after start() is invoked with a streaming content, the network connectivity is lost
     * and is not re-connected within a timeout of one minute, player shall cease to download
     * streaming content, and, after consuming the already downloaded content, transition
     * to the Error state with onError callback notified.
     *
     * If after start() is invoked with a streaming content, the corresponding streaming server
     * loses response and is not revived within a timeout of one minute, player shall cease to
     * download streaming content, and, after consuming the already downloaded content,
     * transition to the ERROR state with onError callback invoked.
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     */
    public void start() throws IllegalStateException
    {
        playerStart();
    }

    /**
     * Pause playback.
     *
     * Transitions the player to the PAUSED state.
     *
     * If the player is in PLAYING state, then calling pause() will pause
     * playback.
     *
     * If the player is in Player::STATE_PAUSED then calling pause() will have
     * no effect.
     *
     * Pause() does not pause network activity undertaken by the player.
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, PREPARED, STOPPED, ERROR
     */
    public void pause() throws IllegalStateException
    {
        playerPause();
    }

    /**
     * Stop playback.
     *
     * This method transitions the player to the STOPPED state.
     *
     * This function will block until playback has stopped, and all network
     * activity undertaken by the player will cease.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player is in one of the following states:
     *              IDLE, PREPARING, ERROR
     */
    public void stop() throws IllegalStateException
    {
        playerStop();
    }

    /**
     * Seek to a specified position in the playing media.
     *
     * If the media is not seekable, i.e. if the result of Player::canSeek is false,
     * then calling Player::seekTo shall have no effect.
     *
     * If the value of the argument is negative, the function shall leave the
     * player's state unaltered.
     *
     * If the value of the milliseconds argument is greater than the duration of
     * the media resource, then the function shall leave the player's state unaltered.
     *
     * On success, the player shall seek to the specified position.
     *
     * @param   ms The number of milliseconds from the start of the media to seek to.
     *
     * @throws  Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     *
     * @throws  Player.IllegalArgumentException
     *          Thrown when the value of milliseconds argument is negative or greater than
     *          {@link #getPlaybackDuration() getPlaybackDuration}.
     */
    public void seekTo(long ms) throws IllegalStateException, IllegalArgumentException
    {
        playerSeekTo(ms);
    }

    /**
     * Get the current state of the player.
     *
     * @see PlayerState
     *
     * @return The player instance's current state.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player has already been released.
     *
     * @see Player#release()
     */
    public PlayerState getState() throws IllegalStateException
    {
        try
        {
            return PlayerState.fromInt(playerGetState());
        }
        catch (Exception e)
        {
            return PlayerState.ERROR;
        }
    }

    /**
     * Media Information pertaining to the current media resource.
     *
     * @return  MediaInfo object containing populated media information.
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     */
    public MediaInfo getMediaInfo() throws IllegalStateException
    {
        return new MediaInfo(this);
    }

    /*
     * This method is invoked from JNI code to populate given Map object with key-value pairs
     * The intricacy here is that the value is of Value type that cannot be constructed in JNI
     */
    private static void addItemToMap(Map<String, Value> items, String key, String rawValue) {
        Value value = new Value(rawValue);
        items.put(key, value);
    }

    /* This method acquires copy of MediaInfo native object */
    private static native void playerMediaInfoAcquire(Player player);
    /* This method releases the copy of MediaInfo native object effectively calling delete on it */
    private static native void playerMediaInfoRelease(Player player);
    /* Populates given map with media properties of the movie (high-level) */
    private static native final void playerMediaInfoGetMediaInfo(Player player, Map<String, Value> itemBundle);
    /* Method populates media properties specific to particular track */
    private static native final void playerMediaInfoGetTrackInfo(Player player, int trackIdx, Map<String, Value> itemBundle);
    /* Method populates media properties of particular stream within a track */
    private static native final void playerMediaInfoGetStreamInfo(Player player, int trackIdx, int strmIdx, Map<String, Value> itemBundle);

    /**
     * Class for Player to return media resource information.
     *
     * The class provides hierarchical representation of media information.
     * Returned media information contains a list of tracks, each track has streams and substreams.
     */
    public static final class MediaInfo
    {
        /* internal presentation of the media information is a map of key-value pairs*/
        private Map<String, Value> mItems;
        /* Array of track information objects */
        private TrackInfo[] mTracks = new TrackInfo[0];

        private void ASSERT(boolean expr, String success, String error) {
            if (expr) {
                Log.d("ASSERT", success);
            } else {
                throw new RuntimeException("ASSERT: " + error);
            }
        };

        /*
         * MediaInfo class has private constructor which implies that the class
         * Is not meant to be instantiated outside of the Player instance
         *
         * Initialize media information including track information of
         * tracks present and stream information for streams present in
         * each track present.
         */
        private MediaInfo(Player player)
        {
            mItems = new HashMap<String, Value>();

            //
            // Tests for MediaInfo.MediaType enum.
            //
            Player.MediaInfo.MediaType mediaType = Player.MediaInfo.MediaType.UNKNOWN;
            mediaType = Player.MediaInfo.MediaType.fromInt(0);
            ASSERT(Player.MediaInfo.MediaType.UNKNOWN == mediaType, "MediaType UNKNOWN OK", "MediaType UNKNOWN NG: " + mediaType);
            mediaType = Player.MediaInfo.MediaType.fromInt(1);
            ASSERT(Player.MediaInfo.MediaType.AUDIO == mediaType, "MediaType AUDIO OK", "MediaType AUDIO NG: " + mediaType);
            mediaType = Player.MediaInfo.MediaType.fromInt(2);
            ASSERT(Player.MediaInfo.MediaType.VIDEO == mediaType, "MediaType VIDEO OK", "MediaType VIDEO NG: " + mediaType);
            mediaType = Player.MediaInfo.MediaType.fromInt(4);
            ASSERT(Player.MediaInfo.MediaType.SUBTITLE == mediaType, "MediaType SUBTITLE OK", "MediaType SUBTITLE NG: " + mediaType);

            /* ask JNI to get copy of mediaInfo object */
            playerMediaInfoAcquire(player);
            /* Fill the map with values */
            playerMediaInfoGetMediaInfo(player, mItems);

            if (mItems.containsKey("MEDIA_INFO_NUM_TRACKS")) {
                int numTracks = mItems.get("MEDIA_INFO_NUM_TRACKS").toInt();

                if (numTracks > 0) {
                    mTracks = new TrackInfo[numTracks];
                }
            }

            for (int trkIdx = 0; trkIdx < mTracks.length; ++trkIdx) {
                /* populate the track list */
                mTracks[trkIdx] = new TrackInfo(player, trkIdx);
            }

            /* tell JNI to release the mediaInfo object */
            playerMediaInfoRelease(player);
        }

        /**
         * Type of the media
         */
        public enum MediaType {
            /*
             * Order must be kept in sync with C++ track type enum values.
             */

            /** Track type is unknown */
            UNKNOWN,
            /** Track type is audio */
            AUDIO,
            /** Track type is video */
            VIDEO,
            /** Track type is subtitles */
            SUBTITLE;

            /*
             * Actual enum values differ to those in C++. In C++ the
             * enum values increment as powers of 2.
             */
            private static MediaType fromInt(int id) {
                int index = 0;
                int temp = id;
                while (temp > 0)
                {
                    index++;
                    if ((temp & 0x1) == 0x1)
                        break;
                    temp = (temp >> 1);
                }

                if ((index >= 0) && (index < mValuesCache.length)) {
                    return mValuesCache[index];
                } else {
                    return UNKNOWN;
                }
            }

            /*
             * Enum.values() is expensive operation.
             * Therefore we keep static cache and reuse it all the time
             */
            private static MediaType[] mValuesCache = MediaType.values();
        }

         /**
         * Gets the URI of the media resource.
         *
         * @return URI of the media resource, or empty string
         * if no movie URI is present
         */
        public String getMovieUri() {
            String uri="";

            if (mItems.containsKey("MEDIA_INFO_MOVIE_URI")) {
                uri = mItems.get("MEDIA_INFO_MOVIE_URI").toString();
            }

            return uri;
        }

        /**
         * Gets the duration of the media.
         *
         * @return Duration in ticks, if no duration is available
         * (for example, if streaming live content), 0 is returned.
         */
        public long getMovieDuration() {
            long result = 0;

            if (mItems.containsKey("MEDIA_INFO_MOVIE_DURATION")) {
                result = mItems.get("MEDIA_INFO_MOVIE_DURATION").toLong();
            }

            return result;
        }

        /**
         * Gets the movie timescale
         *
         * @return Movie timescale in ticks per second.
         */
        public int getMovieTimescale() {
            int result = 0;

            if (mItems.containsKey("MEDIA_INFO_MOVIE_TIMESCALE")) {
                result = mItems.get("MEDIA_INFO_MOVIE_TIMESCALE").toInt();
            }

            return result;
        }

        /**
         * Gets number of logical tracks (adaptation sets for DASH) in the media resource
         *
         * @return Number of tracks
         */
        public int getNumTracks() {
            return mTracks.length;
        }

        /**
         * Gets track information for the particular track in media resource
         *
         * @param trackIdx index of the track being requested
         *
         * @return Track information.
         *
         * @throws Player.IllegalArgumentException if trackIdx is invalid
         */
        public TrackInfo getTrackInfo(int trackIdx) throws IllegalArgumentException {
            if ((trackIdx >= 0) && (trackIdx < mTracks.length)) {
                return mTracks[trackIdx];
            } else {
                throw new IllegalArgumentException();
            }
        }

        /**
         * Check if the media contains audio tracks.
         *
         * @return true if the media contains audio tracks. Otherwise false.
         */
        public boolean hasAudio()
        {
            for (int trackIdx = 0; trackIdx < getNumTracks(); ++trackIdx) {
                try {
                    if (getTrackInfo(trackIdx).getType() == MediaType.AUDIO) {
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        /**
         * Check if the media contains video tracks.
         *
         * @return true if the media contains video tracks. Otherwise false.
         */
        public boolean hasVideo()
        {
            for (int trackIdx = 0; trackIdx < getNumTracks(); ++trackIdx) {
                try {
                    if (getTrackInfo(trackIdx).getType() == MediaType.VIDEO) {
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        /**
         * Encapsulates media information of a stream within a track.
         */
        public static final class TrackInfo {

            private Map<String, Value> mItems;
            private StreamInfo[] mStreams = new StreamInfo[0];

            /*
             * Private constructor implies that the class is not meant
             * to be instantiated outside of the Player instance
             */
            private TrackInfo(Player player, int trackIdx) {
                mItems = new HashMap<String, Value>();

                playerMediaInfoGetTrackInfo(player, trackIdx, mItems);

                if (mItems.containsKey("TRACK_INFO_NUM_STREAMS")) {
                    int numStreams = mItems.get("TRACK_INFO_NUM_STREAMS").toInt();

                    if (numStreams > 0) {
                        mStreams = new StreamInfo[numStreams];
                    }
                }

                for (int strmIdx = 0; strmIdx < mStreams.length; ++strmIdx) {
                    mStreams[strmIdx] = new StreamInfo(player, trackIdx, strmIdx);
                }
            };

            /**
             * Gets the media track type.
             *
             * @return MediaTrack type which indicates if the track is video, audio, timed text.
             * When the type is unknown, MediaType UNKNOWN is returned.
             *
             * @see MediaType
             */
            public MediaType getType() {
                MediaType result = MediaType.UNKNOWN;
                if (mItems.containsKey("TRACK_INFO_TYPE")) {
                    result = MediaType.fromInt(mItems.get("TRACK_INFO_TYPE").toInt());
                }
                return result;
            }

            /**
             * Gets the language code of the track.
             *
             * @return Language code in either ISO-639-1 or ISO-639-2 format.
             * When the language is unknown, ISO-639-2 language code, "und", is returned.
             */
            public String getLanguage() {
                String result = "und";
                if (mItems.containsKey("TRACK_INFO_LANGUAGE")) {
                    result = mItems.get("TRACK_INFO_LANGUAGE").toString();
                }
                return result;
            }

            /**
             * Gets number of streams (representations for DASH) in the track.
             *
             * @return Number of streams
             */
            public int getNumStreams() {
                return mStreams.length;
            }

            /**
             * Gets Stream information for the particular stream in the track.
             *
             * @param streamIdx index of the stream being requested
             *
             * @return Stream information
             *
             * @throws Player.IllegalArgumentException if streamIdx is invalid
             */
            public StreamInfo getStreamInfo(int streamIdx) throws IllegalArgumentException {
                if ((streamIdx >= 0) && (streamIdx < mStreams.length)) {
                    return mStreams[streamIdx];
                } else {
                    throw new IllegalArgumentException();
                }
            }

            /**
             * Encapsulates media information of a stream within a track.
             */
            public static final class StreamInfo {
                private Map<String, Value> mItems;

                /*
                 * Private constructor implies that the class is not meant
                 * to be instantiated outside of the Player instance
                 */
                private StreamInfo(Player player, int trackIdx, int strmIdx) {
                    mItems = new HashMap<String, Value>();

                    playerMediaInfoGetStreamInfo(player, trackIdx, strmIdx, mItems);
                };

                /**
                 * Gets bandwidth of the stream.
                 *
                 * @return Bandwidth value in bps, or 0 if not applicable/unavailable.
                 */
                public int getBandwidth()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_BANDWIDTH")) {
                        result = mItems.get("STREAM_INFO_BANDWIDTH").toInt();
                    }
                    return result;
                }

                /**
                 * Gets codecs used to encode the stream.
                 *
                 * @return Value conforms to simp-list or fancy-list productions of RFC6381
                 * Section 3.2, or empty string when not applicable/unavailable.
                 */
                public String getCodecs()
                {
                    String result = "";
                    if (mItems.containsKey("STREAM_INFO_CODECS")) {
                        result = mItems.get("STREAM_INFO_CODECS").toString();
                    }
                    return result;
                }

                /**
                 * Gets number of audio channels in the stream.
                 *
                 * @return Channel count value in [1-16], or 0 if not applicable/unavailable.
                 */
                public int getNumAudioChannels()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_AUDIO_CHANNEL_COUNT")) {
                        result = mItems.get("STREAM_INFO_AUDIO_CHANNEL_COUNT").toInt();
                    }
                    return result;
                }

                /**
                 * Gets audio sampling rate of the stream.
                 *
                 * @return Audio sampling rate value in Hz, or 0 if not applicable/unavailable.
                 */
                public int getAudioSamplingRate()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_AUDIO_SAMPLING_RATE")) {
                        result = mItems.get("STREAM_INFO_AUDIO_SAMPLING_RATE").toInt();
                    }
                    return result;
                }

                /**
                 * Gets encoded frame width of the video stream.
                 *
                 * @return Encoded frame width in pixels, or 0 if not applicable/unavailable.
                 */
                public int getVideoFrameWidth()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_ENC_VID_FRAME_WIDTH")) {
                        result = mItems.get("STREAM_INFO_ENC_VID_FRAME_WIDTH").toInt();
                    }
                    return result;
                }

                /**
                 * Gets encoded frame height of the video stream.
                 *
                 * @return Encoded frame height in pixels, or 0 if not applicable/unavailable.
                 */
                public int getVideoFrameHeight()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_ENC_VID_FRAME_HEIGHT")) {
                        result = mItems.get("STREAM_INFO_ENC_VID_FRAME_HEIGHT").toInt();
                    }
                    return result;
                }

                /**
                 * Gets pixel aspect ratio in horizontal direction of the video stream.
                 *
                 * @return Pixel aspect ratio X value, where X:Y is the pixel aspect ratio.
                 * Otherwise, returns 0 if not applicable/unavailable.
                 */
                public int getVideoPixelAspectRatioX()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_VID_PAR_X")) {
                        result = mItems.get("STREAM_INFO_VID_PAR_X").toInt();
                    }
                    return result;
                }

                /**
                 * Gets pixel aspect ratio in vertical direction of the video stream.
                 *
                 * @return Pixel aspect ratio Y value, where X:Y is the pixel aspect ratio.
                 * Otherwise, 0 if not applicable/unavailable.
                 */
                public int getVideoPixelAspectRatioY()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_VID_PAR_Y")) {
                        result = mItems.get("STREAM_INFO_VID_PAR_Y").toInt();
                    }
                    return result;
                }

                /**
                 * Gets frame rate numerator F of the video stream, where the frame rate is F/D
                 * and D is the frame rate denominator.
                 *
                 * @see #getVideoFrameRateDenominator()
                 *
                 * @return Frame rate numerator value, or 0 if not applicable/unavailable.
                 */
                public int getVideoFrameRateNumerator()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_VID_FRAME_RATE_NUM")) {
                        result = mItems.get("STREAM_INFO_VID_FRAME_RATE_NUM").toInt();
                    }
                    return result;
                }

                /**
                 * Gets frame rate denominator D of the video stream, where the frame rate is F/D
                 * and F is the frame rate numerator.
                 *
                 * @see #getVideoFrameRateNumerator()
                 *
                 * @return Frame rate denominator value, or 0 if not applicable/unavailable.
                 */
                public int getVideoFrameRateDenominator()
                {
                    int result = 0;
                    if (mItems.containsKey("STREAM_INFO_VID_FRAME_RATE_DEN")) {
                        result = mItems.get("STREAM_INFO_VID_FRAME_RATE_DEN").toInt();
                    }
                    return result;
                }
            }
        }
    }


    /**
     * Returns the index of the audio, video, or subtitle track currently playing.
     *
     * The return value is an index that can be used
     * in calls to {@link MediaInfo#getTrackInfo(int)}
     *
     * @param trackType type of the track being requested
     * ({@link MediaInfo.MediaType#AUDIO}, {@link MediaInfo.MediaType#VIDEO}, {@link MediaInfo.MediaType#SUBTITLE})
     *
     * @return an index of playing track, -1 if no track of specified type is playing
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     *
     * @see MediaInfo.TrackInfo#getType()
     */
    public int getPlayingTrack(MediaType trackType) throws IllegalStateException
    {
        // Converting from enum to int, therefore enum order is important
        return playerGetPlayingTrack(trackType.ordinal());
    }


    /**
     * Switch to a specified stream within a specified track.
     *
     * If the specified track is not selected, switching stream for that track
     * will have no effect.
     *
     * If the specified stream is already selected for the specified track,
     * calling this API will have no effect.
     *
     * This API is non-blocking, the switch will take effect
     * asynchronously. When switching is complete any attached
     * StreamChangedListener objects will have their onStreamChanged callbacks called.
     *
     * This API will perform stream switching at the next segment boundary.
     * Because segment files of the new stream will have to be fetched from the
     * server, this will introduce a delay in playback. The length of the delay is
     * dependent on the bandwidth condition and playback position of the
     * current segment.
     *
     * The default stream is always the stream at index zero.
     *
     * @param trackIdx  Index of track that is currently selected,
     *                  on which the stream switch is to occur on
     * @param streamIdx Index of stream to switch stream to.
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     *
     * @throws Player.IllegalArgumentException
     *          Thrown when one of the arguments is out of range.
     */
    public void switchStream(int trackIdx, int streamIdx) throws IllegalArgumentException, IllegalStateException
    {
        playerSwitchStream(trackIdx, streamIdx);
    }

    /**
     * Returns the index of the stream currently playing.
     *
     * The return value is an index that can be used
     * in calls to {@link MediaInfo.TrackInfo#getStreamInfo(int)}
     *
     * @param trackIdx Index of the track being requested
     *
     * @return The index of the playing stream
     *
     * @throws Player.IllegalStateException
     *          Thrown when the player is in one of the following states:
     *          IDLE, INITIALIZED, PREPARING, STOPPED, ERROR
     *
     * @throws Player.IllegalArgumentException
     *          Thrown when given an invalid trackIdx
     */
    public int getPlayingStream(int trackIdx) throws IllegalArgumentException, IllegalStateException
    {
        return playerGetPlayingStream(trackIdx);
    }

    public static Version getVersion() {
        int major = 0;
        int minor = 0;
        int patch = 0;
        String text = "";

        Map<String, Value>  items = new HashMap<String, Value>();

        playerGetVersion(items);

        if (items.containsKey("VERSION_MAJOR_NUMBER")) {
            major = items.get("VERSION_MAJOR_NUMBER").toInt();
        }
        if (items.containsKey("VERSION_MINOR_NUMBER")) {
            minor = items.get("VERSION_MINOR_NUMBER").toInt();
        }
        if (items.containsKey("VERSION_PATCH_NUMBER")) {
            patch = items.get("VERSION_PATCH_NUMBER").toInt();
        }
        if (items.containsKey("VERSION_READABLE_TEXT")) {
            text = items.get("VERSION_READABLE_TEXT").toString();
        }

        return new Version(major, minor, patch, text);
    }
    /**
     * Class for Player to return Integration Framework version information.
     *
     * The class provides major, minor and patch version.
     */
    public static final class Version
    {
        /** Major version */
        public final int MAJOR;
        /** Minor version */
        public final int MINOR;
        /** Patch version */
        public final int PATCH;
        /** Human readable version with extra info. Not intended to be parsed,
            order of fields in string is arbitrary.*/
        public final String READABLE_TEXT;
        /*
         * Version class has private constructor which implies that the class
         * Is not meant to be instantiated outside of the Player instance
         *
         * Initialize Version including major, minor and patch version
         * of Integration Framework API used.
         */
        private Version(int major, int minor, int patch, String readable_text) {
            MAJOR = major;
            MINOR = minor;
            PATCH = patch;
            READABLE_TEXT = readable_text;
        }

        /**
         * Human readable version with extra info. Not intended to be parsed,
         * order of fields in string is arbitrary.
         */
        @Override
        public String toString() {
            return READABLE_TEXT;
        }
    }

    /**
     * Set the value for a property of the player by name.
     *
     * Setting properties allows for customizing player behavior.
     *
     * @param name  Identifier for the property to modify.
     * @param value The value of the property to set.
     *
     * @throws Player.InvalidValueException         Thrown when provided value was out of range
     *                                              or the wrong type.
     * @throws Player.PropertyNotFoundException     Thrown when non-existent property name
     *                                              has been referenced.
     * @throws Player.IllegalStateException         Thrown when called in an illegal state for
     *                                              the specified property.
     */
    public void setProperty(String name, Value value) throws InvalidValueException,
                                                                PropertyNotFoundException,
                                                                IllegalStateException
    {
        if (null == value)
        {
            throw new InvalidValueException();
        }

        playerSetProperty(name, value.toString());
    }

    /**
     * Get the value of a property of the player by name.
     *
     * @param name  Identifier of the property.
     *
     * @return      The value corresponding to the specified property.
     *
     * @throws Player.IllegalStateException     Thrown when called in an illegal state
     *                                          for the specified property.
     * @throws Player.PropertyNotFoundException Thrown when non-existent property name
     *                                          has been referenced.
     */
    public Value getProperty(String name) throws PropertyNotFoundException,
                                                 IllegalStateException
    {
        return new Value(playerGetProperty(name));
    }

    /**
     * Encapsulates a value of a specific type.
     * Used for conveying information throughout this API.
     *
     * Provides methods for getting and setting values of
     * different basic types and collection of basic types.
     */
    public static class Value
    {
        private static final String VALUE_SEPARATOR = ",";

        /**
         * Default constructor. Defaults to value of zero.
         */
        public Value()
        {
            rawValue = "0";
        }

        /**
         * Constructor.
         *
         * @param value    The value to initialize object to.
         */
        public <T> Value(T value)
        {
            rawValue = String.valueOf(value);
        }

        /**
         * Constructor.
         *
         * @param values    The list of values to initialize object to.
         */
        public <T> Value(List<T> values)
        {
            rawValue = "";
            boolean needSeparator = false;
            for (T value : values)
            {
                if (needSeparator)
                {
                    rawValue += VALUE_SEPARATOR;
                }

                rawValue += String.valueOf(value);
                needSeparator = true;
            }
        }

        /**
         * Returns if this instance has no value.
         *
         * @return true if this instance has no value, false otherwise.
         */
        public boolean isEmpty()
        {
            return rawValue.isEmpty();
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return byte value.
         */
        public byte toByte()
        {
            return Byte.parseByte(rawValue);
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return long value.
         */
        public long toLong()
        {
            return Long.parseLong(rawValue);
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return integer value.
         */
        public int toInt()
        {
            return Integer.parseInt(rawValue);
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return String value.
         */
        @Override
        public String toString()
        {
            return rawValue;
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return List of Byte values that comprises the object's value.
         */
        public List<Byte> toByteValues()
        {
            List<Byte> list = null;

            String[] strings = rawValue.split(VALUE_SEPARATOR);

            if (0 != strings.length)
            {
                list = new ArrayList<Byte>(strings.length);
                for(String val : strings)
                {
                    if (!val.isEmpty()) {
                        list.add((byte)Integer.parseInt(val));
                    }
                }
            }

            return list;
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return List of Integer values that comprises the object's value.
         */
        public List<Integer> toIntValues()
        {
            List<Integer> list = null;

            String[] strings = rawValue.split(VALUE_SEPARATOR);

            if (0 != strings.length)
            {
                list = new ArrayList<Integer>(strings.length);
                for(String val : strings)
                {
                    if (!val.isEmpty()) {
                        list.add(Integer.parseInt(val));
                    }
                }
            }

            return list;
        }

        /**
         * Returns value contained within this {@link Value} object.
         *
         * @return List of Strings that comprises the object's value.
         */
        public List<String> toStringValues()
        {
            List<String> list = null;

            String[] strings = rawValue.split(VALUE_SEPARATOR);

            if (0 != strings.length)
            {
                list = new ArrayList<String>(strings.length);
                Collections.addAll(list, strings);
            }

            return list;
        }

        private String rawValue;
    }

    /**
     * Check if the media allows random seeking to arbitrary position.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player has been released.
     *
     * @return true if the input media allows random seeking. Otherwise false.
     *         An example of an unseekable media is streaming with live profile.
     */
    public boolean canSeek() throws IllegalStateException
    {
        return playerCanSeek();
    }

    /**
     * Get the current playback position relative to the
     * start of the media in milliseconds.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player has been released.
     *
     * @return Current playback time in milliseconds.
     *          0, if the player is not in state PLAYING or PAUSED.
     *         -1, if the time exceeds the max value of a Java int type.
     */
    public long getPlaybackPosition() throws IllegalStateException
    {
        return playerGetPlaybackPosition();
    }

    /**
     * Get the duration of the media in milliseconds.
     *
     * @throws Player.IllegalStateException
     *              Thrown when the player has been released.
     *
     * @return Media duration in milliseconds.
     *          0, if the player is not in state PREPARED, PLAYING or PAUSED.
     *         -1, if the duration exceeds the max value of a Java int type.
     */
    public long getPlaybackDuration() throws IllegalStateException
    {
        return playerGetPlaybackDuration();
    }

    /**
     * Enum to encapsulate errors that occur within Player
     */
    public enum Error
    {
        /** Default error code within Integration Framework player */
        GENERIC,

        /** Unknown error */
        UNKNOWN;

        private static Error fromInt(int id) {
            if ((id >= 0) && (id < mValuesCache.length)) {
                return mValuesCache[id];
            } else {
                return UNKNOWN;
            }
        }

        /*
         * Enum.values() is expensive operation.
         * Therefore we keep static cache and reuse it all the time
         */
        private static Error[] mValuesCache = Error.values();
    };

    /**
     * Listener for monitoring a Player instance's state transition into the PREPARED state.
     */
    public interface PrepareStateListener
    {
        /**
         * Handle transition of the player into the PREPARED state.
         *
         * @param player    The instance that is being observed.
         */
        void onPrepared(Player player);
    }

    private List<PrepareStateListener> prepareStateListeners = new ArrayList<PrepareStateListener>();

    /**
     * Register listener to monitor player state transition to PREPARED state.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void addPrepareStateListener(PrepareStateListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            prepareStateListeners.add(listener);
        }
    }

    /**
     * Unregister listener from monitoring player state transition to PREPARED state.
     *
     * @param listener  The listener implementation to unregister.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void removePrepareStateListener(PrepareStateListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            prepareStateListeners.remove(listener);
        }
    }


    /**
     * Listener for monitoring when playback reaches the end of the media.
     */
    public interface CompletionListener
    {
        /**
         * Handler which is called to notify that the end of media has been reached during playback.
         *
         * @param player    The instance that is being observed.
         */
        void onCompletion(Player player);
    }

    private List<CompletionListener> completionListeners = new ArrayList<CompletionListener>();

    /**
     * Register listener to monitor when playback reaches the end of the media.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void addCompletionListener(CompletionListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            completionListeners.add(listener);
        }
    }

    /**
     * Unregister listener from monitoring when playback reaches the end of the media.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void removeCompletionListener(CompletionListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            completionListeners.remove(listener);
        }
    }


    /**
     * Listener for notification that the stream being played has changed.
     */
    public interface StreamChangedListener
    {
        /**
         * Notify that the player is now playing a new stream.
         *
         *
         * @param player        The instance that this message is emitted by.
         * @param trackIdx      The index of the track containing the changed stream.
         * @param streamIdx     The index of the now playing stream.
         *
         */
        void onStreamChanged(Player player, int trackIdx, int streamIdx);
    }

    private List<StreamChangedListener> streamChangedListeners = new ArrayList<StreamChangedListener>();

    /**
     * Register listener to receive notification of the stream changed.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void addStreamChangedListener(StreamChangedListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            streamChangedListeners.add(listener);
        }
    }

    /**
     * Unregister listener from receiving notification of the stream changed.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void removeStreamChangedListener(StreamChangedListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            streamChangedListeners.remove(listener);
        }
    }

    /**
     * Listener for notifying that a non-recoverable error has occurred.
     */
    public interface ErrorStateListener
    {
        /**
         * Handler which is called when an error has occurred.
         *
         * @param player    The instance that the error occurred in.
         * @param error     The information regarding the error occurred.
         */
        void onError(Player player, Error error);
    }

    private List<ErrorStateListener> errorStateListeners = new ArrayList<ErrorStateListener>();

    /**
     * Register listener to be notified that a non-recoverable error has occurred.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void addErrorStateListener(ErrorStateListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            errorStateListeners.add(listener);
        }
    }

    /**
     * Unregister listener from being notified that a non-recoverable error has occurred.
     *
     * @param listener  The listener implementation to register.
     *
     * @throws Player.IllegalStateException
     *                  Thrown when the player has been released.
     */
    public void removeErrorStateListener(ErrorStateListener listener) throws IllegalStateException
    {
        if (listener != null)
        {
            errorStateListeners.remove(listener);
        }
    }

    // Load the native library.
    static
    {
        System.loadLibrary("dope");
        engineInit();
    }

    private EventHandler mEventHandler;
    private int mNativeContext; // accessed by native methods
    private int mListenerContext; // accessed by native methods
    private int mNativeMediaInfo; // accessed by native methods

    private Player()
    {
        Looper looper;
        if ((looper = Looper.myLooper()) != null)
        {
            mEventHandler = new EventHandler(this, looper);
        }
        else if ((looper = Looper.getMainLooper()) != null)
        {
            mEventHandler = new EventHandler(this, looper);
        }
        else
        {
            mEventHandler = null;
        }
    }

    /* Do not change these values without updating their counterparts
     * in include/player.h!
     */
    private static final int MEDIA_PREPARED = 0;
    private static final int MEDIA_PLAYBACK_COMPLETE = 1;
    private static final int MEDIA_BUFFERING_UPDATE = 2;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_STREAM_CHANGE = 200;

    private class EventHandler extends Handler
    {
        private Player mPlayer;

        public EventHandler(Player player, Looper looper)
        {
            super(looper);
            mPlayer = player;
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (0 == mPlayer.mNativeContext)
            {
                Log.w(TAG, "mediaplayer went away with unhandled events");
                return;
            }
            switch(msg.what)
            {
            case MEDIA_PREPARED:
            {
                for (int i = 0; i < prepareStateListeners.size(); ++i)
                {
                    prepareStateListeners.get(i).onPrepared(mPlayer);
                }
                break;
            }
            case MEDIA_PLAYBACK_COMPLETE:
            {
                for (int i = 0; i < completionListeners.size(); ++i)
                {
                    completionListeners.get(i).onCompletion(mPlayer);
                }
                break;
            }
            case MEDIA_ERROR:
            {
                Log.e(TAG, "Error (" + msg.arg1 + "," + msg.arg2 + ")");

                for (int i = 0; i < errorStateListeners.size(); ++i)
                {
                    errorStateListeners.get(i).onError(mPlayer, Error.GENERIC);
                }
                break;
            }
            case MEDIA_STREAM_CHANGE:
            {
                for (int i = 0; i < streamChangedListeners.size(); ++i)
                {
                    streamChangedListeners.get(i).onStreamChanged(mPlayer, msg.arg1, msg.arg2);
                }
                break;
            }
            default:
                Log.e(TAG, "Unknown message type " + msg.what);
                break;
            }
        }
    }

    /*
     * Called from native code when an interesting event happens.  This method
     * just uses the EventHandler system to post the event back to the main app thread.
     * We use a weak reference to the original MediaPlayer object so that the native
     * code is safe from the object disappearing from underneath it.  (This is
     * the cookie passed to native_setup().)
     */
    private static void postEventFromNative(Object player_ref, int what, int arg1, int arg2, String message)
    {
        Player player = (Player)((WeakReference<?>)player_ref).get();
        if (null == player)
        {
            return;
        }

        // DOPE-601: To make it platform agnostic, we need to use thread instead of handler
        // And should not use Message.
        // This part will have to be addressed if we support other platforms that run JVM
        if (null != player.mEventHandler)
        {
            Message m = player.mEventHandler.obtainMessage(what, arg1, arg2, message);
            player.mEventHandler.sendMessage(m);
        }
    }
}

