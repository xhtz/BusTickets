package com.jivosite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;

public class JivoSdk {

    private WebView webView;
    private ProgressDialog progr;
    private String language;
    public JivoDelegate delegate = null;

    public JivoSdk(WebView webView){
        this.webView = webView;
        this.language = "";

    }

    public JivoSdk(WebView webView, String language){
        this.webView = webView;
        this.language = language;

    }

    public void prepare(){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)delegate).getWindowManager().getDefaultDisplay().getMetrics(dm);
        final float density = dm.density;

        ViewTreeObserver.OnGlobalLayoutListener list = new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousHeightDiff = 0;
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                webView.getWindowVisibleDisplayFrame(r);

                int heightDiff = webView.getRootView().getHeight() - r.bottom;
                int pixelHeightDiff = (int)(heightDiff / density);
                if (pixelHeightDiff > 100 && pixelHeightDiff != previousHeightDiff) { // if more than 100 pixels, its probably a keyboard...
                    //String msg = "S" + Integer.toString(pixelHeightDiff);
                    execJS("window.onKeyBoard({visible:false, height:0})");
                }
                else if ( pixelHeightDiff != previousHeightDiff && ( previousHeightDiff - pixelHeightDiff ) > 100 ){
                    //String msg = "H";
                    execJS("window.onKeyBoard({visible:false, height:0})");
                }
                previousHeightDiff = pixelHeightDiff;
            }
        };

        webView.getViewTreeObserver().addOnGlobalLayoutListener(list);

        //создаем спиннер
        progr = new ProgressDialog(webView.getContext());
        progr.setTitle("JivoSite");
        progr.setMessage("Загрузка...");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        //пробрасываем JivoInterface в Javascript
        webView.addJavascriptInterface(new JivoInterface(webView), "JivoInterface");
        webView.setWebViewClient(new MyWebViewClient());

        if (this.language.length() > 0){
            webView.loadUrl("file:///android_asset/html/index_"+this.language+".html");
        }else{
            webView.loadUrl("file:///android_asset/html/index.html");
        }

    }

    public class JivoInterface{

        private WebView mAppView;
        public JivoInterface  (WebView appView) {
            this.mAppView = appView;
        }

        @JavascriptInterface
        public void send(String name, String data){
            if (delegate != null){
                delegate.onEvent(name, data);
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.toLowerCase().indexOf("jivoapi://") == 0){
                if (url.toLowerCase().indexOf("jivoapi://url.click/") == 0){
                    String link = url.substring(20);
                    if (delegate != null){
                        delegate.onEvent("url.click", link);
                    }

                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    //startActivity(browserIntent);
                    return true;
                }

                if (url.toLowerCase().indexOf("jivoapi://agent.set") == 0){
                    webView.loadUrl("javascript:JivoInterface.send('agent.set',agentName())");
                    return true;
                }

                String event = url.substring(10);
                if (delegate != null){
                    delegate.onEvent(event, url);
                    return true;
                }


                return true;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            progr.dismiss();
        }

    }

    public void execJS(String script){
        webView.loadUrl("javascript:" + script);
    }

}