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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Private-LAN voice transport. The host relays small PCM packets to room members. */
public class LanAudioPeer {
    public static final int PORT = 45872;
    private static final int RATE = 16000;
    private static final int IN = AudioFormat.CHANNEL_IN_MONO;
    private static final int OUT = AudioFormat.CHANNEL_OUT_MONO;
    private static final int PCM = AudioFormat.ENCODING_PCM_16BIT;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final Map<String, Long> peers = new ConcurrentHashMap<>();
    private volatile boolean running;
    private volatile boolean host;
    private DatagramSocket socket;
    private AudioRecord recorder;
    private AudioTrack player;
    private InetAddress hostAddress;

    public void startHost() throws IOException { start(true, null); }
    public void startClient(String address) throws IOException { start(false, InetAddress.getByName(address)); }

    private void start(boolean asHost, InetAddress address) throws IOException {
        stop(); host = asHost; hostAddress = address;
        socket = asHost ? new DatagramSocket(PORT) : new DatagramSocket();
        int inSize = AudioRecord.getMinBufferSize(RATE, IN, PCM);
        int outSize = AudioTrack.getMinBufferSize(RATE, OUT, PCM);
        if (inSize <= 0 || outSize <= 0) throw new IOException("Audio device unavailable");
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, RATE, IN, PCM, inSize * 2);
        player = new AudioTrack(AudioManager.STREAM_VOICE_CALL, RATE, OUT, PCM, outSize * 2, AudioTrack.MODE_STREAM);
        recorder.startRecording(); player.play(); running = true;
        pool.execute(this::receiveLoop); pool.execute(this::captureLoop);
    }

    private void captureLoop() {
        byte[] data = new byte[640];
        while (running) {
            int n = recorder.read(data, 0, data.length);
            if (n <= 0) continue;
            try {
                if (!host) {
                    socket.send(new DatagramPacket(data, n, hostAddress, PORT));
                } else {
                    for (String key : peers.keySet()) {
                        String[] p = key.split(":", 2);
                        socket.send(new DatagramPacket(data, n, InetAddress.getByName(p[0]), Integer.parseInt(p[1])));
                    }
                }
            } catch (Exception ignored) { if (!running) break; }
        }
    }

    private void receiveLoop() {
        byte[] buffer = new byte[2048];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String key = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                if (host) {
                    peers.put(key, System.currentTimeMillis());
                    for (String peer : peers.keySet()) {
                        if (peer.equals(key)) continue;
                        String[] p = peer.split(":", 2);
                        socket.send(new DatagramPacket(packet.getData(), packet.getLength(), InetAddress.getByName(p[0]), Integer.parseInt(p[1])));
                    }
                }
                player.write(packet.getData(), packet.getOffset(), packet.getLength());
            } catch (IOException ignored) { if (!running) break; }
        }
    }

    public void stop() {
        running = false;
        try { if (recorder != null) { recorder.stop(); recorder.release(); } } catch (Exception ignored) {}
        try { if (player != null) { player.stop(); player.release(); } } catch (Exception ignored) {}
        if (socket != null) socket.close();
        peers.clear(); recorder = null; player = null; socket = null;
    }
}
