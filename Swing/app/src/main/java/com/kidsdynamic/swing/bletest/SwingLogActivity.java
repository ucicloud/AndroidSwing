package com.kidsdynamic.swing.bletest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.vise.log.inner.Tree;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SwingLogActivity extends AppCompatActivity {

    public static StringBuffer logBuffer = new StringBuffer(4096);

    public static class StringTree extends Tree {

        @Override
        protected void log(int type, String tag, String message) {
            String timeSecond = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
            synchronized (logBuffer) {
                logBuffer.append(timeSecond + " " + message + "\n");
            }
        }
    }

    private TextView textView;
    private static final String TAG = "maple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing_ble_log);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SWING LOG");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        textView = (TextView) findViewById(R.id.tv_log);
        //textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        textView.setText(logBuffer.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_ble_log, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_clear:
                {
                    synchronized (logBuffer) {
                        logBuffer.setLength(0);
                    }
                    textView.setText(logBuffer.toString());
                }
                break;
            }
            return true;
        }
    };
}
