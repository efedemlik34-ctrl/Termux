package com.diyaz.app.lan;

import java.io.IOException;

/** Simple host/client facade for a private DİYAZ LAN voice room. */
public class LanVoiceRoom {
    private final LanRoomServer server = new LanRoomServer();
    private LanAudioPeer peer;
    private int port;

    public int host() throws IOException {
        port = server.start(socket -> {
            try { socket.close(); } catch (IOException ignored) {}
        });
        return port;
    }

    public void connect(String hostAddress) throws IOException {
        peer = new LanAudioPeer();
        peer.start(hostAddress);
    }

    public void stop() {
        if (peer != null) peer.stop();
        server.stop();
    }

    public int getPort() { return port; }
}
