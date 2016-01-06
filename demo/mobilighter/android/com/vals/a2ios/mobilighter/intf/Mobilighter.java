package com.vals.a2ios.mobilighter.intf;

/**
 * This interface defines common operations on
 * Android/iOS widgets that behave same/similar
 * way on Android/iOS platforms.
 *
 * Created by vsayenko on 8/15/15.
 */
public interface Mobilighter {
    
    public static final String DATE_FORMAT_STR = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    public static final String MESSAGE_BOX_ERROR_TITLE = "Oops...";
//    public static final String MESSAGE_BOX_MESSAGE_TITLE = "Message";
    public static final String MESSAGE_BOX_OK_BUTTON = "Ok";

    public void setContext(Object context);

    public void showOkDialog(String title, String message);
    public void showOkDialog(String title, String message, MobilAction okAction);
    public void showConfirmDialog(String title, String message, MobilAction yesAction, MobilAction noAction);

    public void setPlaceholder(Object textWidget, String text);
    public void setText(Object textWidget, String text);
    public String getText(Object textWidget);

    public void hide(Object widget);
    public void show(Object widget);
    
    public void addActionListener(Object widget, MobilAction action);

    public String dateToString(Object date, String pattern);

}
