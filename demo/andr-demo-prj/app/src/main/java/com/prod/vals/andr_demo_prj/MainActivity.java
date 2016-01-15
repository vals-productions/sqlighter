package com.prod.vals.andr_demo_prj;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.vals.a2ios.mobilighter.impl.MobilighterImpl;
import com.vals.a2ios.mobilighter.intf.Mobilighter;
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
        String dbPath =
                this.getApplication().getApplicationContext().getFilesDir()
                        .getParentFile().getPath() + "/databases/";
        db.setDbPath(dbPath);
        db.setDbName("sqlite.sqlite");
        boolean isDbFileDeployed = db.isDbFileDeployed();
        if(!isDbFileDeployed) {
            System.out.println("DB file is not deployed");
        } else {
            System.out.println("DB file is deployed");
        }
        db.setContext(this);
        db.setOverwriteDb(true);
        try {
            db.copyDbOnce();
            db.openIfClosed();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Bootstrap.getInstance().setSqLighterDb(db);

        /*
            Mobilighter initialization
         */
        Mobilighter mobilighter = new MobilighterImpl();
        mobilighter.setContext(this);
        Bootstrap.getInstance().setMobilighter(mobilighter);

        /*
            Connect Ui controls.
         */
        TextView titleText = (TextView) findViewById(R.id.demo_titleTextView);
        TextView mGreetSqlighterView = (TextView)findViewById(R.id.sqlighter_greet);
        TextView mSqlighterDetailsView = (TextView)findViewById(R.id.demo_sqlDemoStatTextView);
        Button mStartSQLighterButton = (Button) findViewById(R.id.demo_startSqlDemoButton);

        TextView mGreetAmfibianView = (TextView)findViewById(R.id.amfibian_greet);
        TextView mAmfibianDetailsView = (TextView)findViewById(R.id.demo_anDemoStatVextView);
        Button mStartAmfibianButton = (Button) findViewById(R.id.demo_startAnDemoButton);

        TextView mMobilighterCreditTextView = (TextView) findViewById(R.id.demo_mobilighterCreditTextView);

        /**
         * Everything else is Java -> J2ObjC -> Objective C
         */
        Demo.bindUi(
                titleText,
                mGreetSqlighterView, mSqlighterDetailsView, mStartSQLighterButton,
                mGreetAmfibianView, mAmfibianDetailsView, mStartAmfibianButton,
                mMobilighterCreditTextView
        );
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
