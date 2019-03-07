package reactnative.hotspot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.provider.Settings;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;

import reactnative.hotspot.hotspotmanager.ClientScanResult;
import reactnative.hotspot.hotspotmanager.FinishScanListener;
import reactnative.hotspot.hotspotmanager.WifiApManager;

public class HotspotManager {
    private WifiApManager wifi;
    private peersList callback;
    static WifiConfiguration config;

    interface peersList {
        void onPeersScanned(ArrayList<ClientScanResult> peers);
    }

    public HotspotManager(Context context) {
        wifi = new WifiApManager(context);
    }

    public void setPermission() {
        wifi.showWritePermissionSettings(false);
    }

    public void enable(Context c, Callback suc,Callback err){
        getPermission(c);
        wifi.setWifiApEnabled(true,suc,err);
    }

    public void disable(Context c, Callback suc, Callback err){
        getPermission(c);
        wifi.setWifiApEnabled(false,suc,err);
    }

    public void getPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public boolean isCreated(ReadableMap info) {
        config = new WifiConfiguration();
        if (info.hasKey("SSID") && info.hasKey("password")) {
            config.SSID = info.getString("SSID");
            config.preSharedKey = info.getString("password");
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedKeyManagement.set(4);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            if (info.hasKey("protocols")) {
                switch (info.getInt("protocols")) {
                    case HotspotModule.protocols.WPA:
                        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        break;
                    case HotspotModule.protocols.RSN:
                        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        break;
                    default: {
                        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    }
                    break;
                }
            }
            if (info.hasKey("securityType")) {
                switch (info.getInt("securityType")) {
                    case HotspotModule.security.IEEE8021X:
                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                        break;
                    case HotspotModule.security.WPA_EAP:
                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                        break;
                    case HotspotModule.security.WPA_PSK:
                        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                        break;
                    default:
                        config.allowedKeyManagement.set(4);
                        break;
                }
            }
            if (info.hasKey("authAlgorithm")) {
                switch (info.getInt("authAlgorithm")) {
                    case HotspotModule.authAlgorithm.LEAP:
                        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.LEAP);
                        break;
                    case HotspotModule.authAlgorithm.OPEN:
                        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                        break;
                    default:
                        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                        break;
                }
            }
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
            if (wifi.setWifiApConfiguration(config)) return true;
            else return false;
        } else {
            return false;
        }
    }

    public WifiConfiguration getConfig() {
        if (wifi.isWifiApEnabled()) {
            return wifi.getWifiApConfiguration();
        } else {
            return null;
        }
    }

    private void peersList() {
        if (wifi.isWifiApEnabled()) {
            wifi.getClientList(true, new FinishScanListener() {
                @Override
                public void onFinishScan(ArrayList<ClientScanResult> clients) {
                    callback.onPeersScanned(clients);
                }
            });
        }
    }

    public void setPeersCallback(peersList callback) {
        this.callback = callback;
        peersList();
    }
}
