package com.vals.a2ios.mobilighter.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
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

    protected Context context;
    protected Activity activity;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat();

    @Override
    public void setContext(Object context) {
        if(context instanceof Context) {
            this.context = (Context) context;
        }
        if(context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    @Override
    public Object getContext() {
        return context;
    }

    public void showOkDialog(final String title, final String message, final MobilAction okAction) {
        activity.runOnUiThread(new Runnable() {
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }

    @Override
    public void showConfirmDialog(final String title, final String message, final MobilAction actionYes, final MobilAction actionNo) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            actionYes.onAction(null);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            actionNo.onAction(null);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }

    public void setPlaceholder(Object textWidget, String text) {
        if (textWidget != null && textWidget instanceof TextView) {
            TextView et = (TextView)textWidget;
            et.setHint(text);
        }
    }
    public void setText(final Object textWidget, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textWidget != null && textWidget instanceof Button) {
                    Button b = (Button)textWidget;
                    b.setText(text);
                } else  if (textWidget != null && textWidget instanceof TextView) {
                    TextView et = (TextView)textWidget;
                    et.setText(text);
                }
            }
        });
    }
    public String getText(Object textWidget) {
        if (textWidget != null && textWidget instanceof TextView) {
            TextView et = (TextView)textWidget;
            return et.getText().toString();
        }
        return null;
    }
    
    public void addActionListener(Object widget, final MobilAction action) {
        if (widget != null && widget instanceof Button) {
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
        if (widget != null && widget instanceof View) {
            View et = (View)widget;
            et.setVisibility(View.VISIBLE);
        }
    }

    public void hide(Object widget) {
        if (widget != null && widget instanceof View) {
            View et = (View)widget;
            et.setVisibility(View.INVISIBLE);
        }
    }


    public void setEnabled(Object widget, boolean isEnabled) {
        if(widget != null && widget instanceof View) {
            View et = (View)widget;
            et.setEnabled(isEnabled);
        }
    }

    public void clearButtonActionListeners() {

    }

    public boolean isOn(Object toggleButton) {
        if(toggleButton != null && toggleButton instanceof ToggleButton) {
            ToggleButton tb = (ToggleButton) toggleButton;
            return tb.isChecked();
        }
        return false;
    }

    @Override
    public void setOn(Object toggleButton, boolean isOn) {
        if(toggleButton != null && toggleButton instanceof ToggleButton) {
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

    @Override
    public void setFont(Object widget, Object font, Object size) {
        if(widget != null) {
            TextView et = (TextView)widget;
            if (font != null) {
                Typeface f = (Typeface) font;
                et.setTypeface(f);
            }
            if (size != null) {
                Integer i = (Integer)size;
                et.setTextSize(i);
            }
        }
    }

    @Override
    public void setTextColor(Object widget, float r, float g, float b, float a) {
        if(widget instanceof TextView) {
            TextView o = (TextView) widget;
            o.setTextColor(Color.argb((int)a, (int)r, (int)g, (int)b));
        }
    }

}
