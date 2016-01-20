package com.vals.a2ios.mobilighter.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vals.a2ios.mobilighter.intf.MobilAction;
import com.vals.a2ios.mobilighter.intf.Mobilighter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Android implementation of Mobilighter interface.
 *
 * Created by vsayenko on 8/15/15.
 */
public class MobilighterImpl implements Mobilighter {

    protected Activity context;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat();

    public MobilighterImpl() {
    }

    @Override
    public void setContext(Object context) {
        this.context = (Activity)context;
    }

    public void showOkDialog(final String title, final String message, final MobilAction okAction) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // DialogUtil.showOkMessage(message, context, okAction);
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                okAction.onAction(null);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
    }

    @Override
    public void showOkDialog(final String title, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //DialogUtil.showOkMessage(error, context);
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                        ay.onYes();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public void showConfirmDialog(final String title, final String message, final MobilAction actionYes, final MobilAction actionNo) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // DialogUtil.showConfirmDialog(error, context, actionYes, actionNo);
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                actionNo.onAction(null);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                actionYes.onAction(null);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

//    @Override
//    public void scheduleNextStart() {
//        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(context, MyAppReceiver.class);
//        intent.putExtra("onetime", Boolean.FALSE);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi);
//    }

    public void setPlaceholder(Object textWidget, String text) {
        if (textWidget instanceof TextView) {
            TextView et = (TextView)textWidget;
            et.setHint(text);
        }
    }
    public void setText(Object textWidget, String text) {
        if (textWidget instanceof TextView) {
            TextView et = (TextView)textWidget;
            et.setText(text);
        }
    }
    public String getText(Object textWidget) {
        if (textWidget instanceof TextView) {
            TextView et = (TextView)textWidget;
            return et.getText().toString();
        }
        return null;
    }
    
    public void addActionListener(Object widget, final MobilAction action) {
        if (widget instanceof Button) {
                final Button b = (Button) widget;
                b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                    action.onAction(b);
                        }
                });
        }
    }

    public void show(Object widget) {
        if (widget instanceof View) {
            View et = (View)widget;
            et.setVisibility(View.VISIBLE);
        }
    }

    public void hide(Object widget) {
        if (widget instanceof View) {
            View et = (View)widget;
            et.setVisibility(View.INVISIBLE);
        }
    }

    public void clearButtonActionListeners() {

    }

    @Override
    public String dateToString(Object date, String pattern) {
        if (date != null && date instanceof Date) {
            Date d = (Date) date;
            dateFormat.applyPattern(pattern);
            return dateFormat.format(d);
        }
        return " no date";
    }
//    @Override
//    public String dateTimeToString(Object date) {
//        if (date != null && date instanceof Date) {
//            Date d = (Date) date;
//            return dateTimeFormat.format(d);
//        }
//        return " no date ";
//    }

}
