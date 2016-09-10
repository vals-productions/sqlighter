package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.impl.AnIncubatorImpl;
import com.vals.a2ios.amfibian.impl.AnObjectImpl;
import com.vals.a2ios.amfibian.impl.AnOrmImpl;
import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.mobilighter.intf.MobilAction;
import com.vals.a2ios.mobilighter.intf.Mobilighter;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is mostly to make Demo.java cleaner.
 *
 * Created by vsayenko on 1/18/16.
 */
public abstract class DemoBase {

    private SQLighterDb sqLighterDb = Bootstrap.getInstance().getSqLighterDb();
    /**
     * Test tracking variables
     */
    private static int passedTestCount = 0;
    private static List<String> testList = new LinkedList<>();

    protected static Object sqlighterHelloLabel, sqlighterDetailsLabel;
    protected static Object amfibianHelloLabel, amfibianDetailsLabel;
    protected static MobilAction sqlighterStartAction, amfibianStartAction;

    protected static void resetTestCounters() {
        testList.clear();
        passedTestCount = 0;
    }
    protected static void checkTest(String name, boolean isPassed) {
        testList.add(name);
        if(isPassed) {
            passedTestCount++;
        }
    }

    protected static void startTest(String name) {
        testList.add(name);
    }

    protected static void finishTest(boolean isPassed) {
        if(isPassed) {
            passedTestCount++;
        }
    }

    protected static void makeTestsFail() {
        passedTestCount = 0;
    }

    protected static boolean testSummaryCheck() {
        return testList.size() == passedTestCount;
    }


    protected static void printAppointments(AnOrm<Appointment> anOrm) throws Exception {
        System.out.println("Appointment records");
        anOrm.startSqlSelect();
        print(anOrm.getRecords());
    }

    protected static void print(Collection<Appointment> appointments) {
        for (Appointment a: appointments) {
            print(a);
        }
    }

    protected static void print(Appointment appointment) {
        System.out.println(
                "Appointment object. id: " + appointment.getId() +
                        ", name: " + appointment.getName() +
                        ", isProcessed:" + appointment.getIsProcessed() +
                        ", createDate:" + appointment.getCreateDate()
        );
    }

    /**
     * Prints single SQL result record
     *
     * @param rs - SQLighterRs reference
     */
    protected static void print(SQLighterRs rs) {
        Long pk = rs.getLong(0);
        String e = rs.getString(1);
        String n = rs.getString(2);
        byte[] dataBytes = rs.getBlob(3);
        String dataString = null;
        if (dataBytes != null) {
            dataString = new String(dataBytes);
        }
        Number h = rs.getDouble(4);
        System.out.println("pk: " + pk + ", email: " + e + ", name: " + n + ", blob data: " + dataString + ", height: " + h);
    }

    /**
     * Iterate through all records in User table
     *
     * @param title - report title
     * @param db - SQLighterDb reference
     * @throws Exception
     */
    protected static void printUserTable(String title, SQLighterDb db) throws Exception {
        System.out.println(title);
        SQLighterRs rs = db.executeSelect("select id, email, name, data, height from user");
        while (rs.hasNext()) {
            print(rs);
        }
        rs.close();
    }

    /**
     * This method prints the record and verifies its values
     *
     * @param rs
     * @param userName
     * @param userEmail
     * @param userHeight
     * @param blobString
     * @param id
     * @return
     */
    protected static boolean verifyRecord(SQLighterRs rs, String userName, String userEmail,
                                          Double userHeight, String blobString, Long id) {
        Long pk = rs.getLong(0);
        String e = rs.getString(1);
        String n = rs.getString(2);
        byte[] dataBytes = rs.getBlob(3);
        String dataString = null;
        if (dataBytes != null) {
            dataString = new String(dataBytes);
        }
        Number h = rs.getDouble(4);
        System.out.println("pk: " + pk + ", email: " + e + ", name: " + n +
                ", blob data: " + dataString + ", height: " + h);
        return (pk.equals(id) &&
                e.equals(userEmail) &&
                n.equals(userName) &&
                dataString.equals(blobString) &&
                h.doubleValue() == userHeight.doubleValue());
    }

    protected static void extraAmfibianTests(AnOrm<Appointment> anOrm) throws Exception {
        anOrm.addInclAttribs(new String[]{"id"});

        anOrm.startSqlSelect();
        String sql = anOrm.getQueryString();

        checkTest("restricted select clause test 1",
                sql.startsWith("select appointment0.id "));

        anOrm.resetSkipInclAttrNameList();
        anOrm.addSkipAttribs("id", "name", "createDate");
        anOrm.startSqlSelect();
        sql = anOrm.getQueryString();

        checkTest("restricted select clause test 2",
                sql.startsWith("select appointment0.is_processed "));

        String jsonArrayStr = "[";
        /* to json array */
        int nElem = 2;
        for (int i = 0; i < nElem; i++) {
            Appointment a = new Appointment();
            a.setId(i);
            a.setName("Appointemnt " + i);
            a.setIsProcessed(i);
            anOrm.setNativeObject(a);
            String jsonObjectString = anOrm.asJsonString(a);
            jsonArrayStr += jsonObjectString;
            if(i < nElem - 1) {
                jsonArrayStr += ",";
            }
        }
        jsonArrayStr += "]";

        Collection<Appointment> appointments = anOrm.asList(jsonArrayStr);
        checkTest("2 and back from JSON", appointments.size() == nElem);
        int i = 0;
        Iterator<Appointment> it = appointments.iterator();
        while (it.hasNext()) {
            Appointment a = it.next();
            checkTest("json array check #1",
                    a.getId().equals(i) &&
                            a.getName().equals("Appointemnt " + i) &&
                            a.getIsProcessed().equals(i));
            i++;
        }
    }

    protected static String jsonStringWithObjectDefinitions;

    protected static boolean isUseJsonFile = true;

    protected static AnIncubator anIncubator = new AnIncubatorImpl() {
        @Override
        public Class<?> getClassByName(String name) {
            if (name.equals(Entity.class.getName())) return Entity.class;
            else if (name.equals(Appointment.class.getName())) return Appointment.class;
            return null;
        }
    };

    /**
     * The Entity class is a base class for our imaginable project's business objects.
     * It makes sure all our business objects have the id property.
     */
    public static AnOrm<Entity> getOrmEntity() throws Exception {
        if(!isUseJsonFile) {
            return new AnOrmImpl(
                    null,
                    "",
                    Entity.class,
                    /* attribute names/definitions */
                    new String[]{"id"},
                    null);
        } else {
            return anIncubator.make(Entity.class);
        }
    }

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
    public static AnOrm<Appointment> getOrmAppointent(SQLighterDb sqLighterDb) throws Exception {
        if(!isUseJsonFile) {
            AnOrm<Appointment> anOrm = new AnOrmImpl<Appointment>(
                    sqLighterDb, // reference to sqlighter database management object
                    "appointment", // table name
                    Appointment.class, // will
                    /* attribute names/definitions */
                    new String[]{"name", "isProcessed,is_processed,processed"},
                    getOrmEntity());
             /*
             * Lets customize the name with NOT NULL constraint
             */
            anOrm.getAttrib("name").setDbColumnDefinition("TEXT NOT NULL");
            return anOrm;
        } else {
            AnOrm<Appointment> anOrm = anIncubator.make(Appointment.class);
            anOrm.setSqlighterDb(sqLighterDb);
            return anOrm;
        }
    }
}
