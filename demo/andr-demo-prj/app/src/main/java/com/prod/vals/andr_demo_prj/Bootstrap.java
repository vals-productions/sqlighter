package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;

/**
 * Bootstrap class ot initialize SQLite
 * and interfaces that give access to it.
 *
 * This class is j2objc'd into objective c
 * and you should see its counterpart in j2objc
 * demo prj.
 */
public class Bootstrap {
    private static Bootstrap bootstrap;
    public static synchronized Bootstrap getInstance() {
        if(bootstrap == null) {
            bootstrap = new Bootstrap();
        }
        return bootstrap;
    }
    private SQLighterDb sqLighterDb;

    public SQLighterDb getSqLighterDb() {
        return sqLighterDb;
    }

    public void setSqLighterDb(SQLighterDb sqLighterDb) {
        this.sqLighterDb = sqLighterDb;
    }
}
