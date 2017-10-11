package proxy.zk.com.changeproxy;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author kun.zhang
 * @version 9.0.0
 *          17/8/4 16:27
 * @date 2017/08/04
 */

public class ProxyNewUtils {
     static  WifiConfiguration  GetCurrentWifiConfiguration(WifiManager manager) {
        if (!manager.isWifiEnabled())
            return null;

        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = manager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur)
                configuration = wifiConfiguration;
        }

        return configuration;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setWifiProxySettings5(Context context, String host, int port) {
        //get the current wifi configuration
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = GetCurrentWifiConfiguration(manager);
        if (null == config)
            return;

        try {
            //linkProperties is no longer in WifiConfiguration
            Class proxyInfoClass = Class.forName("android.net.ProxyInfo");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyInfoClass;
            Class wifiConfigClass = Class.forName("android.net.wifi.WifiConfiguration");
            Method setHttpProxy = wifiConfigClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            //Method 1 to get the ENUM ProxySettings in IpConfiguration
            Class ipConfigClass = Class.forName("android.net.IpConfiguration");
            Field f = ipConfigClass.getField("proxySettings");
            Class proxySettingsClass = f.getType();

            //Method 2 to get the ENUM ProxySettings in IpConfiguration
            //Note the $ between the class and ENUM
            //Class proxySettingsClass = Class.forName("android.net.IpConfiguration$ProxySettings");

            Class[] setProxySettingsParams = new Class[1];
            setProxySettingsParams[0] = proxySettingsClass;
            Method setProxySettings = wifiConfigClass.getDeclaredMethod("setProxySettings", setProxySettingsParams);
            setProxySettings.setAccessible(true);


            ProxyInfo pi = ProxyInfo.buildDirectProxy(host, port);
            //Android 5 supports a PAC file
            //ENUM value is "PAC"
            //ProxyInfo pacInfo = ProxyInfo.buildPacProxy(Uri.parse("http://localhost/pac"));

            //pass the new object to setHttpProxy
            Object[] params_SetHttpProxy = new Object[1];
            params_SetHttpProxy[0] = pi;
            setHttpProxy.invoke(config, params_SetHttpProxy);

            //pass the enum to setProxySettings
            Object[] params_setProxySettings = new Object[1];
            params_setProxySettings[0] = Enum.valueOf((Class<Enum>) proxySettingsClass, "STATIC");
            setProxySettings.invoke(config, params_setProxySettings);

            //save the settings
            int result =  manager.updateNetwork(config);
            if (result > 0) {
                manager.disconnect();
                manager.reconnect();
                Toast.makeText(context, "设置代理成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "设置代理失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
