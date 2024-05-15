package com.example.wear_os;

import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;




public class MainActivity extends AppCompatActivity {

    private AudioHelper audioHelper;
    private AudioManager audioManager;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o AudioHelper com o contexto atual
        audioHelper = new AudioHelper(this);

        // Inicializa o AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Inicializa o TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });

        // Inicializa o SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    // Processa o comando de voz
                    processVoiceCommand(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        // Verifica se há alto-falante integrado disponível
        boolean isSpeakerAvailable = audioHelper.audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);

        // Verifica se um fone de ouvido Bluetooth está conectado
        boolean isBluetoothHeadsetConnected = audioHelper.audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);

        // Registra o callback para detectar a adição ou remoção de dispositivos de áudio
        registerAudioDeviceCallback();

        // Inicia a escuta do SpeechRecognizer
        startSpeechRecognition();
    }

    private void registerAudioDeviceCallback() {
        if (audioManager != null) {
            audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {

                public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                    // Implementação do método onAudioDevicesAdded
                    for (AudioDeviceInfo device : addedDevices) {
                        if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                            // Um fone de ouvido Bluetooth acabou de ser conectado
                            Log.d("Bluetooth", "Bluetooth headset connected");
                        }
                    }
                }


                public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                    // Implementação do método onAudioDevicesRemoved
                    for (AudioDeviceInfo device : removedDevices) {
                        if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                            // Um fone de ouvido Bluetooth não está mais conectado
                            Log.d("Bluetooth", "Bluetooth headset disconnected");
                        }
                    }
                }
            }, null);
        }
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        speechRecognizer.startListening(intent);
    }

    private void processVoiceCommand(String command) {
        // Aqui você pode implementar a lógica para processar os comandos de voz
        // Por exemplo, você pode fornecer feedback auditivo com TextToSpeech
        textToSpeech.speak("Comando reconhecido: " + command, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private boolean audioOutputAvailable(int type, AudioDeviceInfo[] devices) {
        if (devices != null) {
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera recursos
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
