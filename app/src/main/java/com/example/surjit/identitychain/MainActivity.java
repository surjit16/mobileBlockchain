package com.example.surjit.identitychain;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;
    private TextView mTextView;

    private NetworkInfo networkInfo;
    private ConnectivityManager connMgr;
    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        mProgressBar = findViewById(R.id.progressBar);
        mTextView = findViewById(R.id.textView);
        webSettings = webView.getSettings();
        webView.setWebViewClient(new MyWebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl("https://uidchain.000webhostapp.com");

        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        loadWebsite();
    }

    private boolean checkNetworkConnection() {
        networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void loadWebsite()
    {
        if (!checkNetworkConnection()) {
            mProgressBar.setVisibility(View.GONE);
            showSnackBar(R.string.no_internet_connection, "Retry");
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            webView.loadUrl("https://uidchain.000webhostapp.com");
        }


    }

    public void showSnackBar(int messID, String action_name) {
        mProgressBar.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), getResources().getString(messID), Snackbar.LENGTH_INDEFINITE);
        if (action_name != null) {
            snackbar.setAction(action_name, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadWebsite();
                }
            });
        }
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.setDuration(5000);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                loadWebsite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += " Do you want to continue anyway?";

            builder.setTitle("SSL Certificate Error");
            builder.setMessage(message);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
