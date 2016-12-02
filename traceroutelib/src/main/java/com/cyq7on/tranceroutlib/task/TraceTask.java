package com.cyq7on.tranceroutlib.task;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cyq7on.tranceroutlib.listener.LDNetDiagnoListener;
import com.cyq7on.tranceroutlib.service.LDNetDiagnoService;
import com.cyq7on.tranceroutlib.utils.DeviceUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by xuzhou on 2016/8/1.
 */
public class TraceTask extends BaseTask implements LDNetDiagnoListener {
    String url;
    TextView resultTextView;
    Context context;
    String result;
    private int count = 0;
    private InetAddress[] inetAddress ;
    private boolean isArrive = false;

    public TraceTask(Context context, final String url, TextView resultTextView){
        super(url, resultTextView);
        this.context = context;
        this.url = url;
        this.resultTextView = resultTextView;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inetAddress = InetAddress.getAllByName(url);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public Runnable getExecRunnable() {
        return execRunnable;
    }

    public Runnable execRunnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 1; i < 30; i++) {
                LDNetDiagnoService _netDiagnoService = new LDNetDiagnoService(context.getApplicationContext(),
                        "NetworkDiagnosis", "网络诊断应用", DeviceUtils.getVersion(context), "",
                        "", url, "", "",
                        "", "", TraceTask.this);
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(true);
                _netDiagnoService.hop = i;
                _netDiagnoService.execute();
            }
        }
    };

    public void setResult(String result) {
        Pattern pattern = Pattern.compile("(?<=rom )[\\w\\W]+(?=\\n\\n)");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            if (resultTextView == null) {
                return;
            }
            resultTextView.post(new updateResultRunnable(matcher.group(0) + "\n"));
        }
    }

    @Override
    public void OnNetDiagnoFinished(String log) {

    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        if(!isArrive){
            for (int i = 0; i < inetAddress.length; i++) {
                String addresss = inetAddress[i].getHostAddress();
                Log.d("inetAddress", addresss);
                if(log.contains(addresss)){
                    isArrive = true;
                    break;
                }
            }
        }
        Log.d("OnNetDiagnoUpdated", log);
        ++ count;
        Log.d("OnNetDiagnoUpdated", "count:" + count);
        if(count == 29){
            if(isArrive){
                Log.d("OnNetDiagnoUpdated", "finish");
                Toast.makeText(context,"完成",Toast.LENGTH_SHORT).show();
            }else{
                Log.d("OnNetDiagnoUpdated", "not finish");
            }
        }
//        if (log.contains(":")) {
//            String[] info = log.split(":");
//        }
        if (resultTextView == null) {
            return;
        }
        resultTextView.post(new updateResultRunnable(log));
    }
}
