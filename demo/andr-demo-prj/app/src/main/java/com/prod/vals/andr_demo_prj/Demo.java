package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

/**
 * This class is being converted into
 * iOS module.
 *
 * It represent some business logics that is using
 * SQLite db access, that, after being j2objc'd
 * produces same results.
 *
 */
public class Demo {
    /**
     * Prints the single SQL result record
     * @param rs
     */
    private static void print(SQLighterRs rs) {
        Long pk = rs.getLong(0);
        String e = rs.getString(1);
        String n = rs.getString(2);
        byte[] dataBytes = rs.getBlob(3);
        String dataString = null;
        if (dataBytes != null) {
            dataString = new String(dataBytes);
        }
        Number h = rs.getDouble(4);
        System.out.println("pk: " + pk + ", email: " + e + ", name: " + n + ", blob data: " + dataString + ", height: " + h );
    }

    private static void printUserTable(String title, SQLighterDb db) {
        System.out.println(title);
        SQLighterRs rs = db.executeSelect("select id, email, name, data, height from user");
        while (rs.hasNext()) {
            print(rs);
        }
        rs.close();
    }

    /**
     * Demo Db operations with SQLighter
     */
    public static String dbOperations() {
        String greetingStr = null;
        try {
            SQLighterRs rs = null;
            SQLighterDb db = Bootstrap.getInstance().getSqLighterDb();
            printUserTable("initial state ", db);

            String dataStr = "Hello, sqlighter!";
            byte[] data = dataStr.getBytes();
            db.addParam("user name 5");
            db.addParam("qw@er.ty1");
            db.addParam(data);
            db.addParam(5.67);
            db.executeChange("insert into user( name, email, data, height) values (?, ?, ?, ?)");

            db.addParam("qw@er.ty1");
            System.out.println("check if the record was inserted");
            rs = db.executeSelect("select id, email, name, data, height from user where email = ?");
            while (rs.hasNext()) {
                print(rs);
            }
            rs.close();

            db.addParamNull();
            db.addParam("qw@er.ty1");
            db.executeChange("update user set email = ? where email = ?");

            printUserTable("after update state 1 ", db);

            db.addParam("user@email.com");
            db.addParam("qw@er.ty1");
            db.executeChange("update user set email = ? where email is null or email = ?");

            System.out.println("after update state 2");
            rs = db.executeSelect("select id, email, name, data, height from user");
            while (rs.hasNext()) {
                print(rs);
                String s = rs.getString(1);
                if (!"user@email.com".equals(s)) {
                    Number id = rs.getLong(0);
                    db.addParam("inloop@email.com");
                    db.addParam(id.longValue());
                    db.executeChange("update user set email = ? where id = ?");
                }
            }
            rs.close();

            db.addParam(2);
            db.executeChange("delete from user where id = ?");

            printUserTable("after delete state", db);

            db.executeChange("create table address(id integer primary key autoincrement unique, name text, user_id integer)");
            db.addParam("123 main str, walnut creek, ca");
            db.addParam(1);
            db.executeChange("insert into address(name, user_id) values(?, ?)");

            System.out.println("after address creation/population");
            rs = db.executeSelect("select a.user_id, u.email, u.name, u.data, u.height, a.name from user u, address a "
                    + "where a.user_id = u.id");
            while (rs.hasNext()) {
                print(rs);
                System.out.println(" address: " + rs.getString(5));
            }
            rs.close();

            /**
             * Transaction handling demo
             */
            db.beginTransaction();

            db.addParam("trans@email.com");
            db.addParam("inloop@email.com");
            db.executeChange("update user set email = ? where email = ?");
            printUserTable("inside transaction", db);

            db.rollbackTransaction(); // or...
            // db.commitTransaction();

            printUserTable("after transaction commit or rollback", db);

            /**
             * Retrieving greeting string
             */
            db.addParam(5.67);
            rs = db.executeSelect("select data from user where height = ?");
            if (rs.hasNext()) {
                byte[] greet = rs.getBlob(0);
                greetingStr =  new String(greet);
            }
            rs.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return greetingStr;
    }

}
