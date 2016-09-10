package com.vals.a2ios.mobilighter.intf;

import java.util.List;
import java.util.Map;

/**
 * A wrapper around java.net.HttpURLConnection
 *
 * Created by vsayenko on 1/8/16.
 */
public interface MobilCall {

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    /**
     *
     * @param baseUrl
     */
    void setBaseUrl(String baseUrl);

    /**
     *
     * @param relativeUrl
     */
    void setRelativeUrl(String relativeUrl);

    /**
     *
     * @param callBack
     */
    void setCallBack(MobilCallBack<?> callBack);

    /**
     *
     * @param name
     * @param value
     */
    void addHeader(String name, String value);

    /**
     *
     * @param name
     */
    void setName(String name);

    /**
     *
     * @return
     */
    MobilCallBack<?> getCallBack();

    /**
     *
     * @param c
     */
    void setCookieHeader(String c);

    /**
     *
     * @return
     */
    int getResponseStatus();

    /**
     *
     * @param method
     */
    void setMethod(String method);

    /**
     *
     * @throws Exception
     */
    void remoteCallMakeOnNewThread() throws Exception;

    void remoteCallMake(boolean isBlocking) throws Exception;

    void remoteCallMakeAndWait() throws Exception;
    /**
     *
     * @param name
     * @param value
     */
    void addUrlParameter(String name, Object value);

    /**
     *
     * @param json
     */
    void setJsonPayload(String json);

    /**
     *
     * @param name
     * @param value
     */
    void addParameter(String name, Object value);

    /**
     *
     * @param name
     * @param value
     * @param map
     */
    void addParameter(String name, Object value, Map<String, Object> map);

    /**
     *
     * @param paramMap
     */
    void setParamMap(Map<String, Object> paramMap);

    /**
     *
     * @return
     */
    public List<Throwable> getThrowableList();

    /**
     *
     */
    public void clearThrowableList();

}
