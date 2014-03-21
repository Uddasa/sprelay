package com.uddasa.sprelay.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * This class provides Wifi Access Point basic functions.
 *
 * This class needs the following permissions:<ul>
 * <li>CHANGE_WIFI_STATE;</li>
 * <li>ACCESS_WIFI_STATE.</li>
 * </ul>
 * @author Yankel Scialom
 */
public class WifiApManager {
    private WifiManager wifiMan;
    protected Method setWifiApEnabledMethod, isWifiApEnabledMethod;
    protected final static int MAX_ITER = 10;
    WifiManager.WifiLock lock;
    /**
     * Creates a new WifiApManager from an instanced WifiManager.
     * @param wifiMan a WifiManager obtain by {@code (WifiManager) this.getSystemService(Context.WIFI_SERVICE)}.
     */
    public WifiApManager(WifiManager wifiMan) {
        this.wifiMan = wifiMan;
        getHiddenMethods();
    }

    /**
     */
    private void getHiddenMethods() {
        try {
            setWifiApEnabledMethod = wifiMan.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            isWifiApEnabledMethod = wifiMan.getClass().getMethod("isWifiApEnabled");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return whether WiFi Access Point is enabled or disabled.
     * @return {@code true} if WiFi Access Point is enabled
     */
    public boolean isWifiApEnabled() {
        try {
            return (Boolean)isWifiApEnabledMethod.invoke(wifiMan);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return whether WiFi is enabled or disabled.
     * @return {@code true} if WiFi is enabled
     * @see WifiManager#isWifiEnabled()
     */
    public boolean isWifiEnabled() {
        return wifiMan.isWifiEnabled();
    }

    /**
     * Start AccessPoint mode with the specified
     * configuration. If the radio is already running in
     * AP mode, update the new configuration.
     * Note that starting in Access Point mode disables station
     * mode operation
     * @param conf SSID, security and channel details as
     *		part of WifiConfiguration;
     * @param enabled ignored.
     * @return {@code true} if the operation succeeds, {@code false} otherwise.
     */
    public boolean setWifiApEnabled(WifiConfiguration conf, boolean enabled) {
        try {
            return (Boolean) setWifiApEnabledMethod.invoke(wifiMan, conf, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    /** Toggles the Wifi Access Point radio.<ul>
     * <li>If Access Point mode Wifi is enabled, this method disables it and returns;</li>
     * <li>if Station mode Wifi is enabled, this method enables the Access Point mode.</li>
     * </ul>
     * @param ssid The SSID of the Access Point.
     * @returns {@code true} if the operation succeeds, {@code false} otherwise.
     */
    public boolean toggleWifi(String ssid) {
        // WifiConfiguration creation:
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

        // If AP Wifi is enabled, disables it and returns:
        if(isWifiApEnabled()) {
            //lock.release();
            setWifiApEnabled(conf, false);
            int maxIter = MAX_ITER;
            while (isWifiApEnabled() && maxIter-- >= 0) {
                try {Thread.sleep(500);} catch (Exception e) {}
            }
            return isWifiApEnabled();
        }

        // If standard Wifi is enabled, disables it:
        if (isWifiEnabled()) {
            //if (lock.isHeld())
                //lock.release();
            if (wifiMan.setWifiEnabled(false)) {
                int maxIter = MAX_ITER;
                while (wifiMan.isWifiEnabled() && maxIter-- >= 0) {
                    try {Thread.sleep(500);} catch (Exception e) {}
                }
            }
            if (isWifiEnabled()) {
                return false;
            }
        }

        // Enables AP Wifi
        try {
            if (! setWifiApEnabled(conf, true)) {
                System.out.println("setWifiApEnabledMethod failed.");
                return false;
            }
            int maxIter = MAX_ITER;
            while (! isWifiApEnabled() && maxIter-- > 0) {
                try {Thread.sleep(500);} catch (Exception e) {}
            }
            // Lock wifi if succeeded
            if (isWifiApEnabled()) {
                //lock = wifiMan.createWifiLock(WifiManager.WIFI_MODE_FULL, "SPRelay");
                //lock.acquire();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }
}