package com.vals.a2ios.mobilighter.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vals.a2ios.mobilighter.intf.MobilCallBack;
import com.vals.a2ios.mobilighter.intf.MobilCall;

/**
 * Created by vsayenko on 8/5/15.
 */
public class MobilCallImpl implements MobilCall {

    protected URL url;

    protected String baseUrl = "";
    protected String relativeUrl = "";
    protected String urlParamString;
    protected String method = "GET";
    protected String name = "default";
    protected HttpURLConnection connection;
    protected StringBuilder stringBuilder;
    protected Map<String, Object> paramMap = new HashMap<>();
    protected Map<String, Object> urlParamMap = new HashMap<>();
    protected String jsonPayload;
    protected String charset = "UTF-8";
    protected int responseStatus;
    protected String responseContent;
    protected String cookie = null;
    protected MobilCallBack<?> callBack;
    protected OutputStream outputStream;
    protected Map<String, String> httpHeaders = new HashMap<>();
    protected List<Throwable> throwableList;

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    @Override
    public void setCallBack(MobilCallBack<?> callBack) {
        this.callBack = callBack;
    }

    @Override
    public void addHeader(String name, String value) {
        httpHeaders.put(name, value);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    protected void setHeader(String name, String value) {
        if(name != null && value != null) {
            connection.setRequestProperty(name, value);
        }
    }

    private void specHeaders() {
        if (httpHeaders.size() > 0) {
            Set<String> set = httpHeaders.keySet();
            for (String hname: set) {
                String value = httpHeaders.get(hname);
                if(!isEmpty(value)) {
                    setHeader(hname, value);
                }
            }
        }
    }

    public static boolean isEmpty(String s1) {
        if (s1 == null || "".equals(s1.trim())) {
            return true;
        }
        return false;
    }


    @Override
    public MobilCallBack<?> getCallBack() {
        return callBack;
    }

    public void clearCookie() {
        cookie = null;
    }

    @Override
    public void setCookieHeader(String c) {
         setHeader("Cookie", c);
    }

    @Override
    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    protected void connect() throws Exception {
        connection.connect();
    }


    protected boolean isError(int responseStatus) {
        return responseStatus >= 400 && responseStatus < 600;
    }

    protected void consumeResponse() throws Exception {
        try {
            responseStatus = connection.getResponseCode();
            if (isError(responseStatus)) {
                InputStream errorStream = connection.getErrorStream();
                responseContent = readStream(errorStream);
            } else {
                responseContent = readStream(connection.getInputStream());
            }
            readResponseCookie(connection);
        } catch (Throwable t) {
//            t.printStackTrace(); // TODO
            throwableList.add(t);
//            lastException = t;
        }
    }

    protected void readResponseCookie(HttpURLConnection connection) {
        if(connection != null) {
            String headerName = null;
            String headerValue = null;
            for (int i = 0; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                if("Set-Cookie".equals(headerName)) {
                    headerValue = connection.getHeaderField(i);
                }
            }
            if (headerValue != null) {
                cookie = headerValue;
            }
        }
    }

    public void addParameter(String name, Object value, Map<String, Object> map) {
        if (value != null && value instanceof String) {
            try {
                value = URLEncoder.encode((String) value, charset);
            } catch (UnsupportedEncodingException e) {
                return;
            }
        }
        map.put(name, value);
    }

    @Override
    public void addUrlParameter(String name, Object value) {
        this.addParameter(name, value, urlParamMap);
    }

    public void addParameter(String name, Object value) {
        this.addParameter(name, value, paramMap);
    }

    private String readStream(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }

    @Override
    public void setJsonPayload(String json) {
        this.jsonPayload = json;
    }

    private void writePostBody(String bodyStr) throws Exception {
        if (method.equalsIgnoreCase("POST")) {
            outputStream = connection.getOutputStream();
            outputStream.write(bodyStr.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
    
    private String buildUrlParams(Map<String, Object> paramMap) {
    	return getParametersStr(paramMap);
    }

    public static String getParametersStr(Map<String, Object> paramMap) {
        if (paramMap.size() > 0) {
//			String firstAdd = "";
//			firstAdd = "?";
            StringBuilder buf = new StringBuilder();
            Set<String> keys = paramMap.keySet();
            for (String key: keys) {
//				buf.append(buf.length() == 0 ? firstAdd : "&");
                buf.append("&");
                buf.append(key).append("=").append(paramMap.get(key));
            }
            return buf.toString();
        }
        return "";
    }


    protected boolean processServerResponse(boolean isLastCall) throws Exception {
        if (isError(responseStatus)) {
            if (isLastCall) {
                finishWithError(null);
            }
            return false;
        } else {
            finishWithSuccess();
            return true;
        }
    }

    protected void finishWithError(Exception e) {
        callBack.onError(e, responseStatus, responseContent);
    }
    protected void finishWithSuccess() throws Exception {
        callBack.onCallBack(responseContent);
    }

    public static String actualUrl(String url, String paramString) {
        StringBuilder sb = new StringBuilder(url);
        if (!isEmpty(paramString)) {
            sb.append('?');
            if(paramString.startsWith("&")) {
                sb.append(paramString.substring(1));
            } else {
                sb.append(paramString);
            }
        }
        return sb.toString();
    }

    public static String concatUrl(String url1, String url2) {
        if (url1.endsWith("/")) {
            url1 = url1.substring(0, url1.length() - 1);
        }
        if(url2.startsWith("/")) {
            url2 = url2.substring(1);
        }
        return url1 + "/" + url2;
    }

    protected void beforeConnect() {
    }

    protected void prepareAndConnect() throws Exception {
        urlParamString = "";
        String rootUrl = concatUrl(baseUrl, relativeUrl);
        if ("get".equalsIgnoreCase(method)) {
            urlParamString = buildUrlParams(urlParamMap);
            url = new URL(MobilCallImpl.actualUrl(rootUrl, urlParamString));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept-Charset", charset);
            specHeaders();
            beforeConnect();
            connect();
        } else if("post".equalsIgnoreCase(method)) {
            urlParamString = buildUrlParams(urlParamMap);
            url = new URL(MobilCallImpl.actualUrl(rootUrl, urlParamString));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method); //post
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            setHeader("Cookie", cookie);
            specHeaders();
            if (jsonPayload != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                beforeConnect();
                writePostBody(jsonPayload);
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                beforeConnect();
                writePostBody(buildUrlParams(paramMap)); // writes to body if not url cameraParams
            }
        }
    }

    @Override
    public void remoteCallMake(boolean isBlocking) throws Exception {
        if(isBlocking) {
            remoteCallMakeAndWait();
        } else {
            remoteCallMakeOnNewThread();
        }
    }

    @Override
    public void remoteCallMakeAndWait() throws Exception {
        prepareAndConnect();
        consumeResponse();
        processServerResponse(true);
    }

    @Override
    public void remoteCallMakeOnNewThread() throws Exception {
        MobilCallThreadImpl at = new MobilCallThreadImpl(callBack, this);
        at.start();
    }


//    @Override
//    public boolean remoteCallMakeWithRetry() {
//        try {
//            int tryCount = 0;
//            boolean processResult;
//            do {
//                if (tryCount > 0) {
//                    Thread.sleep(retryDelay);
//                }
//                tryCount++;
//                remoteCallMakeAndWait();
//                processResult = processServerResponse(tryCount < tryCountMax);
//            } while(tryCount < tryCountMax && processResult == false);
//            return processResult;
//        } catch (Exception e) {
//            lastException = e;
//            if(callBack != null) {
//                finishWithError(e);
//            }
//            return false;
//        }
//    }

    /**
     * Created by vsayenko on 8/5/15.
     */
    public static class MobilCallThreadImpl extends Thread {
        private MobilCallBack callback;
        private MobilCallImpl netCall;
        private int tryCount = 0;

        public MobilCallThreadImpl(MobilCallBack callback, MobilCallImpl netCall) {
            this.callback = callback;
            this.netCall = netCall;
        }

        @Override
        public void run() {
            try {
                netCall.remoteCallMakeAndWait();
            } catch (Throwable t) {
                t.printStackTrace(); // TODO -
                if(callback != null) {
                    callback.onError(t, null, null);
                }
            }
        }
    }

    public List<Throwable> getThrowableList() {
        return throwableList;
    }

    public void clearThrowableList() {
        throwableList.clear();
    }
}
