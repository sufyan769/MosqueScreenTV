package com.mosque.salat.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;
    private long lastBackPressTime = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // منع إغلاق الشاشة أو الدخول في وضع السكون نهائياً
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // تفعيل وضع ملء الشاشة الكامل
        hideSystemUI();

        // إعداد الـ WebView
        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        // تفعيل التمرير والتركيز للتحكم بريموت التلفاز
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus(View.FOCUS_DOWN);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideSystemUI();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }
        });

        // تحميل ملف HTML المدمج محلياً داخل التطبيق
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // إخفاء أشرطة النظام ووضع ملء الشاشة الكامل المناسب للتلفاز
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    // التعامل مع أزرار ريموت التلفاز (منع الإغلاق الخاطئ + زر القائمة)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // الضغط على زر القائمة في الريموت يفتح نافذة الإعدادات
            if (webView != null) {
                webView.evaluateJavascript(
                    "(function(){ var el = document.getElementById('s4-shurooq-box') || document.getElementById('mosque-title'); if(el) el.click(); })();",
                    null
                );
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // التحقق من إغلاق نافذة الإعدادات إذا كانت مفتوحة داخل الصفحة
            if (webView != null) {
                webView.evaluateJavascript(
                    "(function(){ var modal = document.getElementById('settings-modal'); if(modal && modal.style.display !== 'none'){ var closeBtn = document.getElementById('close-settings'); if(closeBtn){ closeBtn.click(); return 'closed'; } } return 'none'; })();",
                    value -> {
                        if (!"\"closed\"".equals(value)) {
                            handleDoubleBackToExit();
                        }
                    }
                );
                return true;
            }
            handleDoubleBackToExit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void handleDoubleBackToExit() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < 2500) {
            finish();
        } else {
            lastBackPressTime = currentTime;
            Toast.makeText(this, "اضغط زر الرجوع مرة أخرى للخروج من الشاشة", Toast.LENGTH_SHORT).show();
        }
    }
}
