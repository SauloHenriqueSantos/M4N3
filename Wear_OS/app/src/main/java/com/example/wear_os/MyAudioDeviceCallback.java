package com.example.wear_os;

import android.media.AudioDeviceInfo;

public abstract class MyAudioDeviceCallback {

    public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
        // Implementação do método onAudioDevicesAdded
    }

    public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
        // Implementação do método onAudioDevicesRemoved
    }
}
