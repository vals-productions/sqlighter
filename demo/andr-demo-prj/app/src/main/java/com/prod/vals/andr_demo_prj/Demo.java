package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.impl.AnObjectImpl;
import com.vals.a2ios.amfibian.impl.AnOrmImpl;
import com.vals.a2ios.amfibian.impl.AnUpgradeImpl;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.amfibian.intf.AnUpgrade;
import com.vals.a2ios.mobilighter.intf.MobilAction;
import com.vals.a2ios.mobilighter.intf.Mobilighter;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is being converted into iOS module. It represents some business
 * logic that utilizes SQLite db access. Produces the same output in iOS.
 */
public class Demo extends DemoBase {

    /**
     * Demo sequence of Db operations with SQLighter.
     * @return - greeting string to be displayed at the screen
     */
    public static void sqlighterOperations() {
        String greetingStr = null;
        try {
            resetTestCounters();
            SQLighterRs rs = null;
            SQLighterDb db = Bootstrap.getInstance().getSqLighterDb();
            /**
             * initial database structure
             *
             * CREATE TABLE "user" (
             * `name`  TEXT,
             * `email` TEXT,
             * `id`    INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
             * `data`  BLOB,
             * `height`    REAL
             * );
             *
             * initial database content.
             *
             * INSERT INTO user(name, email, data, height) values ('user 1', 'user1@email.com', null, 1.4);
             * INSERT INTO user(name, email, data, height) values ('user 2', 'user2@email.com', null, null);
             * INSERT INTO user(name, email, data, height) values ('user 3', 'user3@email.com', null, 4.89);
             * INSERT INTO user(name, email, data, height) values ('user 4', null, null, null);
             */
            printUserTable("initial state ", db);

            String userName = "user 5";
            String userEmail = "user5@email.com";
            Double userHeight = 5.67;
            String blobString = "Hello, SQLighter!";
            Long insertedId = 5l;
            /**
             * Let's insert greeting record in User table
             */
            db.addParam(userName);
            db.addParam(userEmail);
                // BLOB column
                String dataStr = blobString;
                byte[] data = dataStr.getBytes();
            db.addParam(data); // data
            db.addParam(userHeight); // height
            Long rowId = db.executeChange("insert into user( name, email, data, height) values (?, ?, ?, ?)");
            System.out.println("Inserted id: " + rowId);

            /**
             * Let's query what we just inserted
             */
            db.addParam(userEmail); // select records with email == "user5@example.com"
            System.out.println("check if the record was inserted");
            rs = db.executeSelect("select id, email, name, data, height from user where email = ?");
            startTest("insert/select test");
            while (rs.hasNext()) {
                finishTest(verifyRecord(rs, userName, userEmail, userHeight, blobString, insertedId));
            }
            rs.close();

            /**
             * Lets make one more update
             */
            db.addParamNull(); // set email as null
            db.addParam(userEmail);
            Long alteredRows = db.executeChange("update user set email = ? where email = ?");
            System.out.println("Updated row count: " + alteredRows);

            checkTest("update row count", alteredRows.equals(1l));

            System.out.println("check if null was set");
            db.addParam(insertedId); // id ==
            rs = db.executeSelect("select email from user where id = ?");
            startTest("null handling");
            while (rs.hasNext()) {
                finishTest(rs.isNull(0));
            }
            rs.close();

            printUserTable("after update state 1 ", db);

            /**
             * More complex update where clause. Set user 5's email
             * back to original value
             */
            db.addParam(userEmail);
            db.addParam(userEmail);
            alteredRows = db.executeChange("update user set email = ? where email is null or email = ?");

            System.out.println("Updated row count: " + alteredRows);

            checkTest("batch update", alteredRows.equals(2l));

            /**
             * And verify table content again
             */
            System.out.println("after update state 2");
            rs = db.executeSelect("select id, email, name, data, height from user");
            int counter = 0;
            while (rs.hasNext()) {
                print(rs);
                String s = rs.getString(1);
                if (!userEmail.equals(s)) {
                    /**
                     * Test nested query execution
                     */
                    Number id = rs.getLong(0);
                    db.addParam("inloop@email.com");
                    db.addParam(id.longValue());
                    alteredRows = db.executeChange("update user set email = ? where id = ?");
                    counter += alteredRows;
                }
            }
            checkTest("nested query update", counter == 3); // id == 1, 2, 3
            rs.close();

            /**
             * Delete example
             */
            db.addParam(2); // delete records where id == 2
            alteredRows = db.executeChange("delete from user where id = ?");
            System.out.println("Deleted rows: " + alteredRows);
            checkTest("delete test", alteredRows.equals(1l));
            printUserTable("after delete state", db);

            /**
             * Create table example
             */
            alteredRows =
                    db.executeChange("create table address(" +
                    "id integer primary key autoincrement unique, " +
                    "name text, " +
                    "user_id integer, " +
                    "update_date text)");
            /**
             * Add some table data
             */
            db.addParam("123 main str, walnut creek, ca");
            db.addParam(1);
            Date dateNow = new Date();
            System.out.println("Date now: " + dateNow.toString());
            db.addParam(new Date());
            db.executeChange("insert into address(name, user_id, update_date) values(?, ?, ?)");

            /**
             * Run some multi table SELECT
             */
            System.out.println("after address creation/population");
            rs = db.executeSelect("select a.user_id, u.email, u.name, " +
                    "u.data, u.height, a.name, a.update_date from user u, address a " +
                    "where a.user_id = u.id");
            while (rs.hasNext()) {
                print(rs);
                System.out.println(" address: " + rs.getString(5));
                Date date = rs.getDate(6);
                System.out.println(" update_date: " + date);
                /**
                 * Dropping milliseconds from original java.util.Date.
                 * The date is stored without milliseconds in the DB.
                 * We need to drop them in order to pass the test below;
                 */
                dateNow = db.getDateWithoutMillis(dateNow);
                checkTest("date handling test", date.equals(dateNow));
                /*
                This will treat the column as date because it contains '_date' in
                its name.
                 */
                System.out.println(" update_date: " + rs.getObject(6));
            }
            rs.close();

            /**
             * Transaction handling and Exception handling example.
             *
             * We would like to execute 2 updates as one
             * transaction.
             */
            startTest("transaction/exception handling");
            try {
                /**
                 * Starts the transaction
                 */
                db.beginTransaction();

                /**
                 * First update
                 */
                db.addParam("trans@email.com");
                db.addParam("inloop@email.com");
                db.executeChange("update user set email = ? where email = ?");

                printUserTable("inside transaction", db);

                /**
                 * Second update
                 */
                db.addParam("inloop2@email.com");
                db.addParam("trans@email.com");
                /**
                 * intentional SQL syntax error to model an exception during
                 * transaction execution
                 */
                db.executeChange("updte user set email = ? where email = ?");
                /**
                 * Commit, which will not happen as we'll jump over
                 * into the catch clause after the exception at the previous
                 * operator.
                 */
                db.commitTransaction();
            } catch (Throwable e) {
                // Do something....
                System.out.println(e.getMessage());
                /**
                 * Rollback as something went wrong, and we wanted all
                 * or nothing to be saved.
                 */
                db.rollbackTransaction();
                finishTest(true);
            }

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

            /**
             * Verify that all opened statements were closed.
             */
            checkTest("Statement balance", db.getStatementBalance() == 0);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            /**
             * Return error message to display at the screen
             * if anything didn't work in the demo.
             */
            Bootstrap.getInstance().getMobilighter().setText(sqlighterHelloLabel, "SQLighter DemoBase did not pass");
            Bootstrap.getInstance().getMobilighter().setText(sqlighterDetailsLabel, "Exception: " + e.getMessage());
            return;
        }
        if (!testSummaryCheck()) {
            Bootstrap.getInstance().getMobilighter().setText(sqlighterHelloLabel, "SQLighter DemoBase did not pass");
            Bootstrap.getInstance().getMobilighter().setText(sqlighterDetailsLabel, "One or more tests failed");
            return ;
        }
        /**
         * Return greet string to display on the screen
         */
        Bootstrap.getInstance().getMobilighter().setText(sqlighterHelloLabel, greetingStr);
        Bootstrap.getInstance().getMobilighter().setText(sqlighterDetailsLabel, "All tests passed.");
        return;
    }

    /**
     * AnObjectImpl / AnSqlImpl / AnOrmImpl demo
     * @return "Meet AmfiniaN greeting.
     */
    public static void amfibianOperations() {
        try {
            resetTestCounters();
            SQLighterDb sqlighterDb = Bootstrap.getInstance().getSqLighterDb();
            /**
             * We might've received the following JSON string as a result of our mobile
             * app's data exchange with the server.
             *
             * Let's assume our goal is to convert this JSON string into a native business
             * object, do some operations with the object, save it in the database and
             * send transformed business object back to the server as JSON string.
             *
             * Server implementation is out of scope of the demo.
             */
            String jsonAppointment234 =
                    "{id: \"234\", name: \"Meet AmfibiaN!\", isProcessed: \"0\"}";
            /**
             * First, let's tell AmfibiaN about our business entities and their properties we
             * would like to manage. We do not have to manage all of them, just those we care
             * of.
             *
             * The Entity class is a base class for our imaginable project's business objects.
             * It makes sure all our business objects have the id property.
             */
            AnObject<Entity> anEntity = new AnObjectImpl(
                Entity.class,
                /* attribute names/definitions */
                new String[]{"id"});
            /**
             * An Appointment object extends the Entity and has appointment name property as
             * well as isProcessed property that is represented by is_processed database
             * column. Each attribute may contain a comma delimited mapping names.
             * <pre>
             *     <li>native object mapping (required)</li>
             *     <li>db column mapping (optional)</li>
             *     <li>JSON object mapping (optional)</li>
             * </pre>
             */
            AnOrm<Appointment> anOrm = new AnOrmImpl<Appointment>(
                sqlighterDb, // reference to sqlighter database management object
                "appointment", // table name
                Appointment.class, // will
                /* attribute names/definitions */
                new String[]{"name", "isProcessed,is_processed"},
                anEntity); // parent
            /**
             * Get native object from json object, so that we could manipulate
             * it with ease.
             */
            Appointment appointment234 = anOrm.asNativeObject(jsonAppointment234);
            checkTest("JSON 2 native mapping",
                    appointment234.getId().equals(234) &&
                            appointment234.getName().equals("Meet AmfibiaN!") &&
                            appointment234.getIsProcessed().equals(0));
            /**
             * Let's decide to store our appointment234 in the database. Since we do not have the
             * table for this entity in our database yet, we can ask AmfibiaN to give us database
             * create table statement for or object:
             */
            String createAppointmentTableSql = anOrm.startSqlCreate().getQueryString();
            /**
             * The SQL query contained in the variable above is:
             * <pre>
             * create table appointment(
             *  name TEXT,
             *  id INTEGER,
             *  is_processed INTEGER )
             * </pre>
             *
             * Note how column names relate with database column and object attributes.
             * Lets execute the query:
             */
            sqlighterDb.executeChange(createAppointmentTableSql);

            /**
             * Now, since the table for Appointment objects has been created,
             * lets store our object in there.
             */
            anOrm.startSqlInsert(appointment234);
            Long rowsAffected = anOrm.apply();
            checkTest("orm insert", rowsAffected.equals(1l));

            printAppointments(anOrm); // Lets check what we've got in the table
            /**
             * Let's create a new appointment native object:
             */
            Appointment appointment456 = new Appointment();
            appointment456.setName("Appointment #456");
            appointment456.setIsProcessed(0);
            appointment456.setId(456);
            /**
             * The following two lines generate insert into.... statement,
             * bind object attributes and execute the query.
             */
            anOrm.startSqlInsert(appointment456);
            anOrm.apply();

            printAppointments(anOrm); // Lets check what we've got in the table
            /**
             * Next, lets specify that we've processed
             * an appointment.
             */
            appointment234.setIsProcessed(1);
            /**
             * Then, lets update our "Meet Amfibian" object in the database
             */
            anOrm.startSqlUpdate(appointment234);
            anOrm.addWhere("id = ?", appointment234.getId());
            rowsAffected = anOrm.apply();
            checkTest("orm update", rowsAffected.equals(1l));
            /**
             * First two lines above generated SQL update statement,
             * bound our object's attributes, set the where clause
             * to WHERE id = 234.
             * The third line of the code executed the statement against
             * database.
             */

            printAppointments(anOrm); // Lets check what we've got in the table

            /**
             * Next, we are going to retrieve our info from the database and
             * transform back to JSON to be able to send it back to the server
             * over the network.
             */
            anOrm.startSqlSelect();
            anOrm.addWhere("id = ?", 234);
            /**
             * Two lines above set the query to SELECT records from
             * appontment table where id  234.
             *
             * The following line of the code executes the query, iterates
             * through result set and maps business objects with result set
             * columns, giving you a collection of objects ready to use.
             */
            Appointment meetAmfibianAppointment = anOrm.getSingleResult();
            if (meetAmfibianAppointment != null) { // just making sure we've got the result
                System.out.println(
                    "Back to JSON string\nbecause we " +
                    "might want to send it\nback to the " +
                    "server like so: " + anOrm.asJsonString(meetAmfibianAppointment));
                /**
                 * return the value through JSONObject, just because
                 * we can do it this way as well.
                 */
                JSONObject jsonObject = anOrm.asJSONObject(meetAmfibianAppointment);
                String name = (String)jsonObject.get("name");
                checkTest("native to JSON", name.equals("Meet AmfibiaN!"));

                /**
                 * AnUpdate demo/tests are in a separate method.
                 * We'll pass our AnAppointment Amfibian object
                 * in so that we can reuse it there and keep our
                 * demo code smaller.
                 */
                anUpdateOperations(anOrm);

                /**
                 * Run extra tests
                 */
                extraAmfibianTests(anOrm);

                if(!testSummaryCheck()) {
                    Bootstrap.getInstance().getMobilighter().setText(amfibianHelloLabel, "AmfibiaN DemoBase did not pass");
                    Bootstrap.getInstance().getMobilighter().setText(amfibianDetailsLabel, "One or more tests failed");
                    return;
                }
                Bootstrap.getInstance().getMobilighter().setText(amfibianHelloLabel, name);
                Bootstrap.getInstance().getMobilighter().setText(amfibianDetailsLabel, "All tests passed.");
                return;
            }
        } catch (Exception e) {
            Bootstrap.getInstance().getMobilighter().setText(amfibianHelloLabel, "AmfibiaN DemoBase did not pass");
            Bootstrap.getInstance().getMobilighter().setText(amfibianDetailsLabel, e.getMessage());
            return ;
        }
    }

    private static AnObject<Appointment> anAppointmentObject;
    /**
     * Database upgrade strategy demonstration.
     */
    public static void anUpdateOperations(final AnObject<Appointment> anAppointment) {
        try {
            anAppointmentObject = anAppointment;
            SQLighterDb db = Bootstrap.getInstance().getSqLighterDb();
            /**
             * Our custom implementation on AnUpdate strategy.
             */
            AnUpgrade anUpgrade = new AnUpgradeImpl(db) {
                List<String> updateKeys = new LinkedList<>();

                /**
                 * Here we have database DDL statements grouped by
                 * respective key.
                 * @param key
                 * @return
                 */
                @Override
                public List<Object> getTasksByKey(String key) {
                    List<Object> l = new LinkedList<>();
                    if ("2015-12-19".equals(key)) {
                        /**
                         * First update we decided to apply
                         */
                        l.add("create table db_drop_test (name text) ");
                        l.add("create table db_upg_test(name text) ");
                        l.add("insert into db_upg_test(name) values('Joe')");
                    } else if ("2015-12-25".equals(key)) {
                        /**
                         * Second update we decided to apply
                         */
                        l.add("alter table db_upg_test add column email text ");
                        l.add("insert into db_upg_test(name,email) values " +
                                                "('Peter', 'peter@email.com')");
                    } else if ("2015-12-25--01".equals(key)) {
                        /**
                         * Third database update we wanted to make
                         */
                        l.add("drop table db_drop_test");
                    } else if ("2016-01-26".equals(key)) {
                        /**
                         * Intentional failure
                         *
                         * Here we made an error. Things happen.
                         *
                         * Since it is not always possible to realize
                         * what went wrong and fix it right away...
                         *
                         * For cases like that we have the DB_RECOVER_KEY
                         * operation below.
                         */
                        l.add("create tble db_drop_test (name text) ");
                    } else if (AnUpgrade.DB_RECOVER_KEY.equals(key)) {
                        /**
                         * In case any db upgrade fails, this key
                         * is recreating the most up to date
                         * DB structure that's guaranteed to work.
                         *
                         * This demo drops and re-creates the database
                         * file, so we just need to provide create....
                         * statements. This is generally more robust approach
                         * as not always DROP.... statements work.
                         *
                         * It's up to you to define this set of statements.
                         * Alternatively, you might use SQLighter database
                         * copy and just overwrite inconsistent db file
                         * with the new one with pre-created DB structure.
                         */
                        l.add("create table db_drop_test(name text) ");
                        l.add("create table db_upg_test(name text) ");
                        /*
                         * If your business objects are supported by
                         * Amfibian, then recreating DB structure
                         * may be simpler task.
                         */
                        l.add(anAppointmentObject);
                    }
                    return l;
                }

                /**
                 * This method is giving us list of update keys to
                 * apply.
                 * @return
                 */
                @Override
                public List<String> getUpdateKeys() {
                    return updateKeys;
                }

                /**
                 * Through this method we set list of database update keys
                 * available to be applied.
                 * @param updateKeys
                 */
                @Override
                public void setUpdateKeys(List<String> updateKeys) {
                    this.updateKeys = updateKeys;
                }
            };
            List<String> keys = new LinkedList<>();
            int upgradeCount;
            /**
             * Simulate DB upgrade #1
             */
            keys.add("2015-12-19");
            anUpgrade.setUpdateKeys(keys);
            upgradeCount = anUpgrade.applyUpdates();
            SQLighterRs rs = db.executeSelect("select count(*) from db_upg_test");
            if(rs.hasNext()) {
                Long cnt = rs.getLong(0);
                checkTest("database upgrade step 1", cnt == 1 && upgradeCount == 1);
            }
            rs.close();
            /**
             * Simulate DB upgrade #2 with several upgrade keys. With yhe key "2015-12-19" being already previously applied.
             */
            keys.add("2015-12-25");
            keys.add("2015-12-25--01");
            startTest("database upgrade step 2");
            upgradeCount = anUpgrade.applyUpdates();
            rs = db.executeSelect("select email from db_upg_test where email is not null");
            if(rs.hasNext()) {
                String email = rs.getString(0);
                finishTest("peter@email.com".equals(email) && upgradeCount == 2);
            }
            rs.close();
            try {
                rs = db.executeSelect("select email from db_upg_test where email is not null");
                rs.hasNext();
            } catch (Exception t) {
                // supposed to get sql syntax exception
                finishTest(true);
            } finally {
                rs.close();
            }
            /**   Add two more keys with the last one being DB recover key.*/
            keys.add("2016-01-26");
            keys.add(AnUpgrade.DB_RECOVER_KEY);
            upgradeCount = anUpgrade.applyUpdates();
            checkTest("Failure during DB upgrade", upgradeCount == -1);
            startTest("db recovery test");
            if(upgradeCount == -1) {
                /*
                 Our recovery strategy  - close
                 database, delete database file, start
                 from empty file. You have flexibility
                 to design your own strategy such as
                 reuse the file by dropping all tables/indexes
                 etc.
                */
                db.close();
                db.deleteDBFile();
                db.openIfClosed();
                upgradeCount = anUpgrade.attemptToRecover();
                finishTest(upgradeCount == 1);
            }
            /**
             * Verify that all opened statements were closed.
             */
            checkTest("Statement balance", db.getStatementBalance() == 0);

            System.out.println("done with AnUpdate");
        } catch (Throwable t) {
            makeTestsFail();
            t.printStackTrace();
        }
    }

    public static void bindUi(
            Object title,
            Object sqlighterHelloLabel, Object sqlighterDetailsLabel, final Object sqlighterStartButton,
            Object amfibianHelloLabel, Object amfibianDetailsLabel, final Object amfibianStartButton,
            Object mobilighterCredit
    ) {
        Demo.sqlighterHelloLabel = sqlighterHelloLabel;
        Demo.sqlighterDetailsLabel = sqlighterDetailsLabel;
        Demo.amfibianHelloLabel = amfibianHelloLabel;
        Demo.amfibianDetailsLabel = amfibianDetailsLabel;

        final Mobilighter mobilighter = Bootstrap.getInstance().getMobilighter();

        mobilighter.setText(title, "Welcome to SQLighter demo.");
        mobilighter.setText(mobilighterCredit, "UI controled by Mobilighter.");

        mobilighter.setText(sqlighterHelloLabel, "");
        mobilighter.setText(amfibianHelloLabel, "");
        mobilighter.setText(sqlighterDetailsLabel, "");
        mobilighter.setText(amfibianDetailsLabel, "");

        mobilighter.setText(sqlighterStartButton, "Begin SQLighter tests");
        mobilighter.setText(amfibianStartButton, "Begin AmfibiaN tests");

        mobilighter.hide(amfibianStartButton);

        sqlighterStartAction = new MobilAction() {
            @Override
            public void onAction(Object param) {
                sqlighterOperations();
                mobilighter.hide(sqlighterStartButton);
                mobilighter.show(amfibianStartButton);
            }
        };
        mobilighter.addActionListener(sqlighterStartButton, sqlighterStartAction);

        amfibianStartAction = new MobilAction() {
            @Override
            public void onAction(Object param) {
                amfibianOperations();
                mobilighter.hide(amfibianStartButton);
            }
        };
        mobilighter.addActionListener(amfibianStartButton, amfibianStartAction);
    }

}
