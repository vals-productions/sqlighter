package com.vals.a2ios.mobilighter.intf;

/**
 * This interface defines common operations on
 * Android/iOS widgets that behave same/similar
 * way on Android/iOS platforms.
 *
 * Created by vsayenko on 8/15/15.
 */
public interface Mobilighter {
    /**
     *
     * @param context
     */
    public void setContext(Object context);

    /**
     *
     * @param title
     * @param message
     */
    public void showOkDialog(String title, String message);

    /**
     *
     * @param title
     * @param message
     * @param okAction
     */
    public void showOkDialog(String title, String message, MobilAction okAction);

    /**
     *
     * @param title
     * @param message
     * @param yesAction
     * @param noAction
     */
    public void showConfirmDialog(String title, String message, MobilAction yesAction, MobilAction noAction);

    /**
     *
     * @param textWidget
     * @param text
     */
    public void setPlaceholder(Object textWidget, String text);

    /**
     *
     * @param textWidget
     * @param text
     */
    public void setText(Object textWidget, String text);

    /**
     *
     * @param textWidget
     * @return
     */
    public String getText(Object textWidget);

    /**
     *
     * @param widget
     */
    public void hide(Object widget);

    /**
     *
     * @param widget
     */
    public void show(Object widget);

    /**
     *
     * @param widget
     * @param action
     */
    public void addActionListener(Object widget, MobilAction action);

    /**
     *
     * @param date
     * @param pattern
     * @return
     */
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
