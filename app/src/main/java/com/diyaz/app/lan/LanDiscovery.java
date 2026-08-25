package com.diyaz.app.lan;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.util.ArrayList;
import java.util.List;

/** Discovers DİYAZ rooms on the same local network using Android NSD. */
public class LanDiscovery {
    public interface Listener { void onRoomFound(String name, String host, int port); }
    private final NsdManager nsd;
    private final List<NsdManager.DiscoveryListener> listeners = new ArrayList<>();
    private Listener listener;

    public LanDiscovery(Context context) { nsd = (NsdManager) context.getSystemService(Context.NSD_SERVICE); }
    public void setListener(Listener l) { listener = l; }

    public void start() {
        stop();
        NsdManager.DiscoveryListener d = new NsdManager.DiscoveryListener() {
            @Override public void onDiscoveryStarted(String serviceType) {}
            @Override public void onDiscoveryStopped(String serviceType) {}
            @Override public void onServiceFound(NsdServiceInfo info) {
                nsd.resolveService(info, new NsdManager.ResolveListener() {
                    @Override public void onServiceResolved(NsdServiceInfo result) {
                        if (listener != null && result.getHost() != null) listener.onRoomFound(result.getServiceName(), result.getHost().getHostAddress(), result.getPort());
                    }
                    @Override public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {}
                });
            }
            @Override public void onServiceLost(NsdServiceInfo serviceInfo) {}
            @Override public void onStartDiscoveryFailed(String serviceType, int errorCode) { nsd.stopServiceDiscovery(this); }
            @Override public void onStopDiscoveryFailed(String serviceType, int errorCode) { nsd.stopServiceDiscovery(this); }
        };
        listeners.add(d);
        nsd.discoverServices("_diyaz._tcp", NsdManager.PROTOCOL_DNS_SD, d);
    }
    public void stop() { for (NsdManager.DiscoveryListener d : listeners) { try { nsd.stopServiceDiscovery(d); } catch (Exception ignored) {} } listeners.clear(); }
}
