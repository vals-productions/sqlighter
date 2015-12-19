package com.prod.vals.andr_demo_prj;

import java.util.Date;

/**
 * Created by vsayenko on 12/18/15.
 */
public class Appointment extends Entity {
    private String name;
    private Integer isProcessed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Integer isProcessed) {
        this.isProcessed = isProcessed;
    }
}
