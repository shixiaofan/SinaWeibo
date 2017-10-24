package com.example.administrator.sinaweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import java.text.SimpleDateFormat;

public class AuthLogin extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "weibosdk";
    /** 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private Button obtain_token_via_sso;//sso授权（仅客户端）
    private Button obtain_token_via_web;//SSO授权（网页端）
    private Button obtain_token_via_signature;//SSO授权（客户端和网页端）
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_login);
        initView();
    }

    private void initView() {
        obtain_token_via_sso=(Button)findViewById(R.id.obtain_token_via_sso);
        obtain_token_via_web=(Button)findViewById(R.id.obtain_token_via_web);
        obtain_token_via_signature=(Button)findViewById(R.id.obtain_token_via_signature);
        obtain_token_via_sso.setOnClickListener(this);
        obtain_token_via_web.setOnClickListener(this);
        obtain_token_via_signature.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           case R.id.obtain_token_via_sso:
               mSsoHandler.authorizeClientSso(new SelfAuthListener());
               break;
            case R.id.obtain_token_via_web:
                mSsoHandler.authorizeWeb(new SelfAuthListener());
                break;
            case R.id.obtain_token_via_signature:
                mSsoHandler.authorize(new SelfAuthListener());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler!=null){
            mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
        }
    }

    private class SelfAuthListener implements WbAuthListener{

    @Override
    public void onSuccess(final Oauth2AccessToken oauth2Accesstoken) {
        AuthLogin.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAccessToken=oauth2Accesstoken;
                if (mAccessToken.isSessionValid()){
                    updateTokenView(false);
                    AccessTokenKeeper.writeAccessToken(AuthLogin.this, mAccessToken);
                    Toast.makeText(AuthLogin.this,
                            "授权成功", Toast.LENGTH_SHORT).show();
                }
            }


        });
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(AuthLogin.this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }
    }

    @Override
    public void cancel() {
        Toast.makeText(AuthLogin.this,
                "取消授权", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        Toast.makeText(AuthLogin.this,
                "授权失败", Toast.LENGTH_SHORT).show();
    }
} private void updateTokenView(boolean b) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = "aaaaaaa";
        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));

        String message = String.format(format, mAccessToken.getToken(), date);
        if (b) {
            message = "bbbbbbbbbbbbbbbb"+ "\n" + message;
        }
        mTokenText.setText(message);
    }
    }