package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.impl.AnIncubatorImpl;
import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.mobilighter.intf.MobilAction;
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
    private int passedTestCount = 0;
    private List<String> testList = new LinkedList<>();

    protected Object sqlighterHelloLabel, sqlighterDetailsLabel;
    protected Object amfibianHelloLabel, amfibianDetailsLabel;
    protected MobilAction sqlighterStartAction, amfibianStartAction;

    protected void resetTestCounters() {
        testList.clear();
        passedTestCount = 0;
    }
    protected void checkTest(String name, boolean isPassed) {
        testList.add(name);
        if(isPassed) {
            passedTestCount++;
        }
    }

    protected void startTest(String name) {
        testList.add(name);
    }

    protected void finishTest(boolean isPassed) {
        if(isPassed) {
            passedTestCount++;
        }
    }

    protected void makeTestsFail() {
        passedTestCount = 0;
    }

    protected boolean testSummaryCheck() {
        return testList.size() == passedTestCount;
    }


    protected void printAppointments(AnOrm<Appointment> anOrm) throws Exception {
        System.out.println("Appointment records");
        anOrm.startSqlSelect();
        print(anOrm.getRecords());
    }

    protected void print(Collection<Appointment> appointments) {
        for (Appointment a: appointments) {
            print(a);
        }
    }

    protected void print(Appointment appointment) {
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
    protected void print(SQLighterRs rs) {
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
    protected void printUserTable(String title, SQLighterDb db) throws Exception {
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
    protected boolean verifyRecord(SQLighterRs rs, String userName, String userEmail,
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

    protected String jsonStringWithObjectDefinitions;

    protected AnIncubator anIncubator = new AnIncubatorImpl();

    public <T> AnOrm<T> getOrm(Class<T> cluss) throws Exception {
        AnOrm<T> anOrm = anIncubator.make(cluss);
        anOrm.setSqlighterDb(Bootstrap.getInstance().getSqLighterDb());
        return anOrm;
    }
}
