package com.dhj.scalablenetcon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dhj.scalablenetcon.db.BaseDaoFactory;
import com.dhj.scalablenetcon.http.ScalableCon;
import com.dhj.scalablenetcon.http.fileNet.DownFileManager;
import com.dhj.scalablenetcon.http.interfaces.IDataListener;
import com.dhj.scalablenetcon.update.UpdateManager;

import java.util.Date;
import java.util.HashMap;
/**
 * Created by duanhuangjun on 17/2/27.
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "KING_DHJ";
    private String APPKEY = "050ad705f666f1f0084f779d585402c8";
    private String url = "http://v.juhe.cn/toutiao/index";
    TextView textView;
    UpdateManager updateManager;
    UserDao baseDao;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.content);
        updateManager=new UpdateManager();
        baseDao= BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
        findViewById(R.id.bnt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                down();
            }
        });
    }


    /**
     * 大文件下载
     * */
    private void down(){
        DownFileManager downFileService=new DownFileManager();
        downFileService.download("http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk");
    }


    /**
     *  1
     *  2
     * @param view
     */
    public  void login(View view)
    {

        User user=new User();
        user.setName("V00"+(i++));
        user.setPassword("123456");
        user.setName("张三"+i);
        user.setUser_id("N000"+i);
        baseDao.insert(user);
        updateManager.checkThisVersionTable(this);
        ScalableCon.sendRequest(null, url,NewsPager.class, new IDataListener<NewsPager>() {
            @Override
            public void onSuccess(NewsPager loginRespense) {
                Log.i(TAG,loginRespense.toString());
            }

            @Override
            public void onErro() {
                Log.i(TAG,"获取失败");
            }

        });
    }


    public void insert(View view)
    {
        Photo photo=new Photo();
        photo.setPath("data/data/my.jpg");
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        photo.setTime(dateFormat.format(new Date()));
        PhotoDao photoDao= BaseDaoFactory.getInstance().getUserHelper(PhotoDao.class,Photo.class);
        photoDao.insert(photo);
    }
    public void write(View view)
    {
        /**
         * 写入版本
         */
        updateManager.saveVersionInfo(this,"V002");

    }
    public void update(View view)
    {
        updateManager.checkThisVersionTable(this);

        updateManager.startUpdateDb(this);
    }


}
