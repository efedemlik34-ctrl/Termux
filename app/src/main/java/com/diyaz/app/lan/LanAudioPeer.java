package com.diyaz.app.lan;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lightweight LAN voice transport for a private DİYAZ room.
 * Raw PCM is used intentionally: no external server or account is required.
 * This is suitable for a trusted local network, not the public internet.
 */
public class LanAudioPeer {
    public static final int PORT = 45872;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO;
    private static final int CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running;
    private DatagramSocket socket;
    private AudioRecord recorder;
    private AudioTrack player;

    public void start(String peerAddress) throws IOException {
        stop();
        socket = new DatagramSocket();
        socket.setReuseAddress(true);
        InetAddress peer = InetAddress.getByName(peerAddress);
        int inSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN, ENCODING);
        int outSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT, ENCODING);
        if (inSize <= 0 || outSize <= 0) throw new IOException("Audio device unavailable");
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE, CHANNEL_IN, ENCODING, inSize * 2);
        player = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE, CHANNEL_OUT, ENCODING, outSize * 2, AudioTrack.MODE_STREAM);
        recorder.startRecording();
        player.play();
        running = true;
        executor.execute(() -> capture(peer));
        executor.execute(this::playback);
    }

    private void capture(InetAddress peer) {
        byte[] buffer = new byte[640];
        while (running) {
            int n = recorder.read(buffer, 0, buffer.length);
            if (n > 0) {
                try { socket.send(new DatagramPacket(buffer, n, peer, PORT)); }
                catch (IOException ignored) { if (running) break; }
            }
        }
    }

    private void playback() {
        byte[] buffer = new byte[2048];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                player.write(packet.getData(), packet.getOffset(), packet.getLength());
            } catch (IOException ignored) { if (running) break; }
        }
    }

    public void stop() {
        running = false;
        try { if (recorder != null) { recorder.stop(); recorder.release(); } } catch (Exception ignored) {}
        try { if (player != null) { player.stop(); player.release(); } } catch (Exception ignored) {}
        if (socket != null) socket.close();
        recorder = null; player = null; socket = null;
    }
}
