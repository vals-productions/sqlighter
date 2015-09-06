package com.prod.vals.andr_demo_prj;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vals.a2ios.sqlighter.impl.SQLighterDbImpl;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * SQLite initialization
         */
        SQLighterDbImpl db = new SQLighterDbImpl();
        db.setDbPath("/data/data/com.prod.vals.andr_demo_prj/databases/");
        db.setDbName("sqlite.sqlite");
        db.setContext(this);
        db.setOverwriteDb(true);
        try {
            db.copyDbOnce();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        db.openIfClosed();
        Bootstrap.getInstance().setSqLighterDb(db);

        /**
         * Demo db operations with SQlighter
         */
        String greetingStr = Demo.dbOperations();

        TextView mGreetView = (TextView)findViewById(R.id.greet);
        mGreetView.setText(greetingStr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
