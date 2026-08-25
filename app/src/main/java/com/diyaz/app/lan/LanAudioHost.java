package com.diyaz.app.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Local Wi-Fi voice relay for a private DİYAZ room. */
public class LanAudioHost {
    public static final int PORT = 45872;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<String, InetAddress> peers = new ConcurrentHashMap<>();
    private volatile boolean running;
    private DatagramSocket socket;

    public void start() throws IOException {
        stop();
        socket = new DatagramSocket(PORT);
        socket.setReuseAddress(true);
        running = true;
        executor.execute(() -> {
            byte[] buffer = new byte[2048];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    InetAddress sender = packet.getAddress();
                    peers.put(sender.getHostAddress(), sender);
                    for (InetAddress peer : peers.values()) {
                        if (!peer.equals(sender)) {
                            DatagramPacket out = new DatagramPacket(packet.getData(), packet.getOffset(), packet.getLength(), peer, PORT);
                            socket.send(out);
                        }
                    }
                } catch (IOException ignored) { if (running) break; }
            }
        });
    }

    public void stop() {
        running = false;
        if (socket != null) socket.close();
        peers.clear();
    }
}
