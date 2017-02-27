package com.dhj.scalablenetcon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dhj.scalablenetcon.http.ScalableCon;
import com.dhj.scalablenetcon.http.interfaces.IDataListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "KING_DHJ";
    private String url = "https://api.aopayun.com/aopa_api/servlet/api.htm/getAdvertListByClassService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("classTypes", "3001");
        ScalableCon.sendRequest(map, url, ElvesModel.class, new IDataListener<ElvesModel>() {

            @Override
            public void onSuccess(ElvesModel elvesModel) {
                Log.i(TAG,elvesModel.toString());
            }

            @Override
            public void onFail(int code) {

            }
        });
    }


}
