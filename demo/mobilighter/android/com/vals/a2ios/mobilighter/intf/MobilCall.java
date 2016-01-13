package com.vals.a2ios.mobilighter.intf;

import java.util.Map;

/**
 * Created by vsayenko on 1/8/16.
 */
public interface MobilCall {

    void setBaseUrl(String baseUrl);

    void setRelativeUrl(String relativeUrl);

    void setCallBack(MobilCallBack<?> callBack);

    void addHeader(String name, String value);

    MobilCallBack<?> getCallBack();

    void setCookieHeader(String c);

    int getResponseStatus();

    void setMethod(String method);

    void remoteCallMakeOnThread() throws Exception;

    void addUrlParameter(String name, Object value);

    void setJsonPayload(String json);

    void addParameter(String name, Object value);

    void addParameter(String name, Object value, Map<String, Object> map);

    void setParamMap(Map<String, Object> paramMap);

//    boolean remoteCallMakeWithRetry();

}
