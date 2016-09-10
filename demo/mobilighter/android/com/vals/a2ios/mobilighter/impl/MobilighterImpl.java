package com.vals.a2ios.mobilighter.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vals.a2ios.mobilighter.intf.MobilAction;
import com.vals.a2ios.mobilighter.intf.Mobilighter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

//    public MobilighterImpl() {
//    }

    @Override
    public void setContext(Object context) {
        if(context instanceof Activity) {
            this.context = (Activity) context;
        }
    }

    @Override
    public Object getContext() {
        return context;
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
//        Intent intent = new Intent(context, AppReceiver.class);
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
    public void setText(final Object textWidget, final String text) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textWidget instanceof Button) {
                    Button b = (Button)textWidget;
                    b.setText(text);
                } else  if (textWidget instanceof TextView) {
                    TextView et = (TextView)textWidget;
                    et.setText(text);
                }
            }
        });
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


    public void setEnabled(Object widget, boolean isEnabled) {
        if(widget instanceof View) {
            View et = (View)widget;
            et.setEnabled(isEnabled);
        }
    }

    public void clearButtonActionListeners() {

    }

    public boolean isOn(Object toggleButton) {
        if(toggleButton instanceof ToggleButton) {
            ToggleButton tb = (ToggleButton) toggleButton;
            return tb.isChecked();
        }
        return false;
    }

    @Override
    public void setOn(Object toggleButton, boolean isOn) {
        if(toggleButton instanceof ToggleButton) {
            ToggleButton tb = (ToggleButton) toggleButton;
            tb.setChecked(isOn);
        }
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

    private ProgressDialog dialog = null;

    @Override
    public void showWaitPopup(String title, String message) {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void hideWaitPopup() {
        dialog.dismiss();
    }

    public void runOnUiThread(final MobilAction action) {
        Activity context = (Activity)getContext();
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                action.onAction(null);
            }
        });
    }

    public String readFile(String assetFileName) {
        try {
            InputStream schemaIs = context.getAssets().open(assetFileName);
            StringBuilder sb = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(schemaIs);
            BufferedReader br = new BufferedReader(isr);
            String fileLine = "";
            do {
                fileLine = br.readLine();
                if (fileLine != null) {
                    sb.append(fileLine + "\n");
                }
            } while (fileLine != null);
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }


}
