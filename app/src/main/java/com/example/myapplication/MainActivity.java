package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userInfo, userPassword;
    Button userLogin;
    CheckBox rememberPassword;
    private CommonVideoView mCommonVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT); //给页面设置一个透明背景色，去掉videoview播放本地视频，短暂黑屏(也好像没用)
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏(并没有什么卵用)
        setContentView(R.layout.activity_main);
        initView();
        playVideoView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_name = preferences.getString("user_name", "");
        String user_password = preferences.getString("user_password", "");
        userInfo.setText(user_name);
        userPassword.setText(user_password);
    }

    private void playVideoView() {
        mCommonVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video)); // 获取raw下视频文件路径
        mCommonVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // 打算去掉播放时短暂黑屏，但是并没有效果。
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        if (i != MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            mCommonVideoView.setBackgroundColor(Color.TRANSPARENT);
                        }
                        return false;
                    }
                });
            }
        });
        mCommonVideoView.start();
        mCommonVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 播放完成之后重新开始播放，循环播放
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCommonVideoView.start();
            }
        });

    }

    private void initView() {
        userInfo = (EditText) this.findViewById(R.id.user_info);
        userPassword = (EditText) this.findViewById(R.id.user_password);
        rememberPassword = (CheckBox) this.findViewById(R.id.checkBox);
        mCommonVideoView = (CommonVideoView) this.findViewById(R.id.video_view);
        userLogin = (Button) this.findViewById(R.id.user_login);
        userLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_login:
                if (config()) {
                    if (isChecked()) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putString("user_name", userInfo.getText().toString());
                        editor.putString("user_password", userPassword.getText().toString());
                        editor.apply();
                    }
                    startActivity();
                } else {
                    Toast.makeText(this, "用户名或密码不正确", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void startActivity() {
        Intent intent = new Intent();
        intent.setClass(this, Main2Activity.class);
        intent.putExtra("successful", true);
        startActivity(intent);
        finish();
    }

    private boolean config() {
        String info = userInfo.getText().toString();
        String passwords = userPassword.getText().toString();
        return info.equals("liurenyi") && passwords.equals("123456");
    }

    //判断checkbox是否被选中
    private boolean isChecked() {
        return rememberPassword.isChecked();
    }

    /**
     * 切换回来重新加载播放
     */
    @Override
    protected void onRestart() {
        playVideoView();
        super.onRestart();
    }

    /**
     * 停止播放
     */
    @Override
    protected void onStop() {
        if (mCommonVideoView != null) {
            mCommonVideoView.stopPlayback();
        }
        super.onStop();
    }

    /**
     * 当前界面被销毁时候，停止播放，且把mCommonVideoView置空
     */
    @Override
    protected void onDestroy() {
        if (mCommonVideoView != null) {
            mCommonVideoView.stopPlayback();
            mCommonVideoView = null;
        }
        super.onDestroy();
    }

}
