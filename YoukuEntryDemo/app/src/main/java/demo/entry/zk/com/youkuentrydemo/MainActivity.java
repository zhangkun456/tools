package demo.entry.zk.com.youkuentrydemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1001;
    private String mLastUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_scan) {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
//            openPage("http://www.baidu.com");
        }else if(view.getId() == R.id.button_scan_last){
            openPage(mLastUrl);
        } else if (view.getId() == R.id.button_test) {
            Intent intent = new Intent();
            intent.setData(Uri.parse("youku://planet/planet_test"));
            startActivity(intent);
        } else if (view.getId() == R.id.button_search) {
            Intent intent = new Intent();
            intent.setData(Uri.parse("youku://planet/search_common"));
            startActivity(intent);
        } else if (view.getId() == R.id.button_home) {
            Intent intent = new Intent();
//            intent.setData(Uri.parse("youku://planet/planet_home_page"));
            String url = "youku://planet/planet_home_page";
//            String url = "youku://yk_user_homepage?id=830151666";
//            String url = "youku://planet/my_followed_community";
            intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
            startActivity(intent);
        } else if (view.getId() == R.id.button_my_planet) {
            Intent intent = new Intent();
            intent.setData(Uri.parse("youku://planet/my_followed_community?userId=830151666"));
//            intent.setData(Uri.parse("youku://planet/my_followed_community?userId=830154779"));
//            intent.setClassName("com.youku.phone","com.zijunlin.zxing.CaptureResultAcitvity");
            startActivity(intent);
        } else if (view.getId() == R.id.button_postdetail) {
            Intent intent = new Intent();
//            String url = "youku://planet/fans_post_detail?id=269208";
            String url = "youku://planet/fans_post_detail?id=536190";
            intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
            startActivity(intent);
        } else if (view.getId() == R.id.button_stardetail) {
            Intent intent = new Intent();
            String url = "youku://planet/fans_star_coming?id=134";
            intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
            startActivity(intent);
        } else if (view.getId() == R.id.button_tuan) {
            Intent intent = new Intent();
            String url = "youku://planet/fans_guardian_home?id=174";
            intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
            startActivity(intent);
        } else if (view.getId() == R.id.button_test1) {
            Intent intent = new Intent();
            String url = "youku://planet/my_community";
            intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
            startActivity(intent);
        }
    }

    private void openPage(String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("youku://planet/planet_test?url=" + url));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    mLastUrl = result;
                    openPage(result);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
