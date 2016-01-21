package com.dolby.application.infra.app;

public interface Constants {

    int AUDIO_SAMPLING_RATE = 48000;
    int AUDIO_CHANNEL_COUNT = 2;
    int AUDIO_PCM_BYTES_PER_SAMPLE = 16;
    // Note: every time we read PCM data from the audio source, it advances the playback time
    // Audio playback position is the reference clock for A/V sync.
    // For smooth video playback it's important to read the audio by very small
    // chunks to make sure that the clock updates are fine-grained
    int AUDIO_TRACK_REQUEST_BYTES =
    // Amount of bytes of 1 second worth
    (AUDIO_CHANNEL_COUNT * AUDIO_PCM_BYTES_PER_SAMPLE * AUDIO_SAMPLING_RATE)
    // get the amount of bytes for 1 millisecond
    / 1000;

    int PLAYER_CONTROL_VIEW = 101;
    int PLAYER_VIDEO_VIEW = 102;
}
