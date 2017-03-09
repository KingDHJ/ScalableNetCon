package com.dhj.scalablenetcon.http.fileNet.interfaces;

import com.dhj.scalablenetcon.http.interfaces.IHttpListener;
import com.dhj.scalablenetcon.http.interfaces.IHttpService;

/**
 * Created by duanhuangjun on 17/2/27.
 */
public interface IDownLitener  extends IHttpListener {

    void setHttpServive(IHttpService httpServive);


    void  setCancleCalle();


    void  setPuaseCallble();

}
