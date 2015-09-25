package com.danding.webapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danding.webapp.R;
import com.danding.webapp.utils.NetworkUtils;

/**
 * Created by baidu on 15/9/25.
 */
public class CoreWebView extends RelativeLayout {

    private View rootView;

    private WebView webView;

    private ProgressBar progressBar;

    private TextView tvMessage;

    private WebChromeClient webChromeClient;

    private WebViewClient webViewClient;

    public CoreWebView(Context context) {
        super(context);

        initView(context);
        setWebView();
    }

    public CoreWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
        setWebView();
    }

    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_webview, null, false);
        webView = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
    }

    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);

        webViewClient = new BaseWebViewClient();
        webChromeClient = new BaseWebChromeClient();
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    public void showLoading() {
        webView.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tvMessage.setVisibility(VISIBLE);
        tvMessage.setText("加载中...");
    }

    public void showLoadComplete() {
        webView.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tvMessage.setVisibility(GONE);
    }

    public void showLoadError() {
        webView.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tvMessage.setVisibility(GONE);
        tvMessage.setText("抱歉，网页加载出现异常");
    }

    class BaseWebViewClient extends WebViewClient {

        public BaseWebViewClient() {
            super();
        }

        /**
         * Give the host application a chance to take over the control when a new
         * url is about to be loaded in the current WebView. If WebViewClient is not
         * provided, by default WebView will ask Activity Manager to choose the
         * proper handler for the url. If WebViewClient is provided, return true
         * means the host application handles the url, while return false means the
         * current WebView handles the url.
         * This method is not called for requests using the POST "method".
         *
         * @param view The WebView that is initiating the callback.
         * @param url  The url to be loaded.
         * @return True if the host application wants to leave the current WebView
         * and handle the url itself, otherwise return false.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("baidu")) {
                webView.loadUrl(url);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        /**
         * Notify the host application that a page has started loading. This method
         * is called once for each main frame load so a page with iframes or
         * framesets will call onPageStarted one time for the main frame. This also
         * means that onPageStarted will not be called when the contents of an
         * embedded frame changes, i.e. clicking a link whose target is an iframe.
         *
         * @param view    The WebView that is initiating the callback.
         * @param url     The url to be loaded.
         * @param favicon The favicon for this page if it already exists in the
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            boolean isConnected = NetworkUtils.getInstance(getContext()).isConnected();
            if (isConnected) {
                showLoading();
            } else {
                showLoadError();
            }
        }

        /**
         * Notify the host application that a page has finished loading. This method
         * is called only for main frame. When onPageFinished() is called, the
         * rendering picture may not be updated yet. To get the notification for the
         * new Picture, use {@link WebView.PictureListener#onNewPicture}.
         *
         * @param view The WebView that is initiating the callback.
         * @param url  The url of the page.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            boolean isConnected = NetworkUtils.getInstance(getContext()).isConnected();
            if (isConnected) {
                showLoadComplete();
            } else {
                showLoadError();
            }
        }

        /**
         * Report an error to the host application. These errors are unrecoverable
         * (i.e. the main resource is unavailable). The errorCode parameter
         * corresponds to one of the ERROR_* constants.
         *
         * @param view        The WebView that is initiating the callback.
         * @param errorCode   The error code corresponding to an ERROR_* value.
         * @param description A String describing the error.
         * @param failingUrl  The url that failed to load.
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            Toast.makeText(getContext(), "errorCode=" + errorCode, Toast.LENGTH_LONG).show();
        }

        /**
         * Give the host application a chance to handle the key event synchronously.
         * e.g. menu shortcut key events need to be filtered this way. If return
         * true, WebView will not handle the key event. If return false, WebView
         * will always handle the key event, so none of the super in the view chain
         * will see the key event. The default behavior returns false.
         *
         * @param view  The WebView that is initiating the callback.
         * @param event The key event.
         * @return True if the host application wants to handle the key event
         * itself, otherwise return false
         */
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    class BaseWebChromeClient extends WebChromeClient {

        public BaseWebChromeClient() {
            super();
        }

        /**
         * Tell the host application the current progress of loading a page.
         *
         * @param view        The WebView that initiated the callback.
         * @param newProgress Current page loading progress, represented by
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        /**
         * Tell the client to display a javascript alert dialog.  If the client
         * returns true, WebView will assume that the client will handle the
         * dialog.  If the client returns false, it will continue execution.
         *
         * @param view    The WebView that initiated the callback.
         * @param url     The url of the page requesting the dialog.
         * @param message Message to be displayed in the window.
         * @param result  A JsResult to confirm that the user hit enter.
         * @return boolean Whether the client will handle the alert dialog.
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        /**
         * Tell the client to display a confirm dialog to the user. If the client
         * returns true, WebView will assume that the client will handle the
         * confirm dialog and call the appropriate JsResult method. If the
         * client returns false, a default value of false will be returned to
         * javascript. The default behavior is to return false.
         *
         * @param view    The WebView that initiated the callback.
         * @param url     The url of the page requesting the dialog.
         * @param message Message to be displayed in the window.
         * @param result  A JsResult used to send the user's response to
         *                javascript.
         * @return boolean Whether the client will handle the confirm dialog.
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        /**
         * Tell the client to display a prompt dialog to the user. If the client
         * returns true, WebView will assume that the client will handle the
         * prompt dialog and call the appropriate JsPromptResult method. If the
         * client returns false, a default value of false will be returned to to
         * javascript. The default behavior is to return false.
         *
         * @param view         The WebView that initiated the callback.
         * @param url          The url of the page requesting the dialog.
         * @param message      Message to be displayed in the window.
         * @param defaultValue The default value displayed in the prompt dialog.
         * @param result       A JsPromptResult used to send the user's reponse to
         *                     javascript.
         * @return boolean Whether the client will handle the prompt dialog.
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

}
