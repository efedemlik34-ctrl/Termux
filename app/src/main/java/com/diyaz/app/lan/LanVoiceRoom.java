package com.diyaz.app.lan;

import java.io.IOException;

/** Host/client facade for a private DİYAZ LAN voice room. */
public class LanVoiceRoom {
    private final LanAudioPeer audio = new LanAudioPeer();

    public int host() throws IOException {
        audio.startHost();
        return LanAudioPeer.PORT;
    }

    public void connect(String hostAddress) throws IOException {
        audio.startClient(hostAddress);
    }

    public void stop() { audio.stop(); }
}
