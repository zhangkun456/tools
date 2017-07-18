package demo.entry.zk.com.youkuentrydemo;

import android.app.Application;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * @author kun.zhang
 * @version 9.0.0
 *          17/6/21 14:39
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ZXingLibrary.initDisplayOpinion(this);
    }
}
