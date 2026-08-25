package com.diyaz.app.lan;

import android.content.Context;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Minimal LAN room signaling endpoint. Audio transport is intentionally separate. */
public class LanRoomServer {
    public interface Listener { void onClient(Socket socket); }
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private ServerSocket server;
    private volatile boolean running;

    public int start(Listener listener) throws IOException {
        if (running) return server.getLocalPort();
        server = new ServerSocket(0);
        running = true;
        pool.execute(() -> {
            while (running) {
                try { Socket s = server.accept(); pool.execute(() -> listener.onClient(s)); }
                catch (IOException ignored) { if (running) break; }
            }
        });
        return server.getLocalPort();
    }
    public void stop() {
        running = false;
        try { if (server != null) server.close(); } catch (IOException ignored) {}
        pool.shutdownNow();
    }
}
