package com.welling.kinghacker.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.welling.kinghacker.tools.SystemTool;

import java.lang.reflect.Field;

/**
 * Created by KingHacker on 4/13/2016.
 *
 */
public class MTWebViewActivity extends AppCompatActivity {
    WebView webView;
    String url;
    private ProgressBar loading;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        Bundle bundle = getIntent().getExtras();
        //接收url值
        url = bundle.getString("webUrl");
        forceShowOverflowMenu();
        initWebView();

    }

    private void initWebView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));

        }
        setContentView(R.layout.layout_web);
        webView = (WebView)findViewById(R.id.webView);
        loading = (ProgressBar)findViewById(R.id.loading);
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    loading.setVisibility(View.GONE);
                } else {
                    if (View.GONE == loading.getVisibility()) {
                        loading.setVisibility(View.VISIBLE);
                    }
                    loading.setProgress(newProgress);
                    Log.i("web",newProgress+"");
                }
                super.onProgressChanged(view, newProgress);
            }

        });

    }
    /**
     * 如果设备有物理菜单按键，需要将其屏蔽才能显示OverflowMenu
     */
    private void forceShowOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void openWithOtherWeb(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_item, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        switch (item.getItemId()){
            case android.R.id.home:
                goBack();
                return true;
            case R.id.openWithOther:
                openWithOtherWeb();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void goBack() {

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                goBack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
