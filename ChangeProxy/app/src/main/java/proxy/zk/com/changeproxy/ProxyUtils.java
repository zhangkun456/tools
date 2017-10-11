package proxy.zk.com.changeproxy;

import android.content.Context;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author kun.zhang
 * @version 9.0.0
 *          17/6/12 14:35
 */

public class ProxyUtils {

    public static void setHttpPorxySetting(Context context, String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
        ProxyInfo mInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInfo = ProxyInfo.buildDirectProxy(host, port);
        }
        if (config != null && mInfo != null) {
            Class clazz = Class.forName("android.net.wifi.WifiConfiguration");
            Class parmars = Class.forName("android.net.ProxyInfo");
            Method method = clazz.getMethod("setHttpProxy", parmars);
            method.invoke(config, mInfo);
            Object mIpConfiguration = getDeclaredFieldObject(config, "mIpConfiguration");

            setEnumField(mIpConfiguration, "STATIC", "proxySettings");
            setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);
            //save the settings
            int result = wifiManager.updateNetwork(config);
            if (result > 0) {
                wifiManager.disconnect();
                wifiManager.reconnect();
                Toast.makeText(context, "设置代理成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "设置代理失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 取消代理设置
     */
    public static void unSetHttpProxy(Context context)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration configuration = getCurrentWifiConfiguration(wifiManager);
        ProxyInfo mInfo = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInfo = ProxyInfo.buildDirectProxy(null, 0);
        }
        if (configuration != null && mInfo != null) {
            Class clazz = Class.forName("android.net.wifi.WifiConfiguration");
            Class parmars = Class.forName("android.net.ProxyInfo");
            Method method = clazz.getMethod("setHttpProxy", parmars);
            method.invoke(configuration, mInfo);
            Object mIpConfiguration = getDeclaredFieldObject(configuration, "mIpConfiguration");
            setEnumField(mIpConfiguration, "NONE", "proxySettings");
            setDeclardFildObject(configuration, "mIpConfiguration", mIpConfiguration);

            //保存设置
            int result = wifiManager.updateNetwork(configuration);
            if (result > 0) {
                wifiManager.disconnect();
                wifiManager.reconnect();
                Toast.makeText(context, "关闭代理成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "关闭代理失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    public static Object getDeclaredFieldObject(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setDeclardFildObject(Object obj, String name, Object object) {
        Field f = null;
        try {
            f = obj.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(obj, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 获取当前的Wifi连接
    public static WifiConfiguration getCurrentWifiConfiguration(WifiManager wifiManager) {
        if (!wifiManager.isWifiEnabled())
            return null;
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = wifiManager.getConnectionInfo().getNetworkId();
        // Log.d("当前wifi连接信息",wifiManager.getConnectionInfo().toString());
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur)
                configuration = wifiConfiguration;
        }
        return configuration;
    }
}
