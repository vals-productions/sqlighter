package com.prod.vals.andr_demo_prj;

import java.util.Collection;

/**
 * Created by vsayenko on 9/18/16.
 */
public class User extends Entity {
    private String name;
    private Collection<Appointment> appointments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Collection<Appointment> appointments) {
        this.appointments = appointments;
    }
}
