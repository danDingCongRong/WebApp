package com.danding.webapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danding.webapp.R;
import com.danding.webapp.utils.MToast;
import com.danding.webapp.utils.NetworkUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by baidu on 15/9/25.
 */
public class WebViewActivity extends Activity {

    private final String DEFAULT_URL = "http://www.baidu.com";

    private WebView webView;

    private ProgressBar progressBar;

    private TextView tvMessage;

    private WebChromeClient webChromeClient;

    private WebViewClient webViewClient;

    private ImageView imageView;

    private Button btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        initView();
        setWebView();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });
    }

    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);

        webViewClient = new BaseWebViewClient();
        webChromeClient = new BaseWebChromeClient();
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(DEFAULT_URL);

        //        webView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        //
        //        webView.loadUrl("file:///android_asset/config.html");
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    public void showLoading() {
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText("加载中...");
    }

    public void showLoadComplete() {
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    public void showLoadError() {
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText("抱歉，网页加载出现异常");
    }

    private void selectPicture() {
        String[] fromSource = new String[]{"拍照", "手机相册"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
        builder.setTitle("选择图片来源");
        builder.setItems(fromSource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            launchCamera();
                        } else {
                            MToast.show(getApplicationContext(), "抱歉，没有检测到可用的SDK");
                        }
                        break;
                    case 1:
                        launchAlbum();
                        break;
                    default:
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    private final int REQUEST_CODE_FROM_CAMERA = 101;
    private final int REQUEST_CODE_FROM_ALBUM = 102;

    private final String imageCachePath = "mnt/sdcard/img.jpg";

    // 拍摄图片
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imageCachePath)));
        startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
    }

    // 选择图片
    private void launchAlbum() {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FROM_CAMERA) {
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.containsKey("data")) {
                    Bitmap bitmap = (Bitmap) bundle.get("data");

                    if (bitmap == null) {
                        MToast.show(getApplicationContext(), "bitmap==null");
                    } else {
                        MToast.show(getApplicationContext(), "bitmap!=null");
                    }
                    imageView.setImageBitmap(bitmap);
                }

                //                Bitmap bitmap = BitmapFactory.decodeFile(imageCachePath);
                //                if (bitmap == null) {
                //                    MToast.show(getApplicationContext(), "bitmap==null");
                //                } else {
                //                    MToast.show(getApplicationContext(), "bitmap!=null");
                //                }
                //                imageView.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_CODE_FROM_ALBUM) {
                Uri uri = data.getData();
                InputStream imgInputStream = null;
                try {
                    imgInputStream = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (imgInputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(imgInputStream);
                    imageView.setImageBitmap(bitmap);
                    if (bitmap == null) {
                        MToast.show(getApplicationContext(), "bitmap==null");
                    } else {
                        MToast.show(getApplicationContext(), "bitmap!=null");
                    }
                }
            }
        }
    }

    class BaseWebViewClient extends WebViewClient {

        public BaseWebViewClient() {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                webView.loadUrl(url);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            boolean isConnected = NetworkUtils.getInstance(getApplicationContext()).isConnected();
            if (isConnected) {
                showLoading();
            } else {
                showLoadError();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            boolean isConnected = NetworkUtils.getInstance(getApplicationContext()).isConnected();
            if (isConnected) {
                showLoadComplete();
            } else {
                showLoadError();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            MToast.show(getApplicationContext(), "errorCode=" + errorCode);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    class BaseWebChromeClient extends WebChromeClient {

        public BaseWebChromeClient() {
            super();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            super.onJsAlert(view, url, message, result);

            result.confirm();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    private Handler mHandler = new Handler();

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        public void clickOnAndroid() {
            mHandler.post(new Runnable() {
                public void run() {
                    webView.loadUrl("javascript:wave()");
                }
            });

        }
    }

}
