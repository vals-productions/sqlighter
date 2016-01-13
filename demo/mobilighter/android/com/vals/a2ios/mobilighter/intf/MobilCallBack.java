package com.vals.a2ios.mobilighter.intf;

/**
 * Created by vsayenko on 8/5/15.
 */
public interface MobilCallBack<T> {

    public void onCallBack(String result) throws Exception;

    public void onError(Throwable e, Integer responseStatus, String errorContent);

}
