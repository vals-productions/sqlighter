package com.vals.a2ios.mobilighter.intf;

/**
 * This interface defines common operations on
 * Android/iOS widgets that behave same/similar
 * way on Android/iOS platforms.
 *
 * Created by vsayenko on 8/15/15.
 */
public interface Mobilighter {

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

    /**
     * Created by vsayenko on 8/20/15.
     */
    public static interface Navigator {
        public void navigateToScreen(String name, Object source);
        public void cleanParameters();
        public void setString(String name, String param);
        public String getString(String name);
        public void setObject(String name, Object param);
        public Object getObject(String name);
        public void setObject(Object param);
        public Object getObject();
    }

}
