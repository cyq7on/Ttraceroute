package com.cyq7on.traceroute;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cyq7on.tranceroutlib.task.TraceTask;
import com.cyq7on.tranceroutlib.utils.LDNetUtil;

public class MainActivity extends AppCompatActivity {

    private Button btnTaceroute;
    private TextView tvResult;
    private EditText etUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTaceroute = (Button) findViewById(R.id.btn_traceroute);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etUrl = (EditText) findViewById(R.id.et_url);
        btnTaceroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                // 网络状态
                if (LDNetUtil.isNetworkConnected(context)) {
                    TraceTask pingTask = new TraceTask(context,etUrl.getText()+"",tvResult);
                    pingTask.doTask();
                } else {
                    Toast.makeText(context,"请检查网络",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
