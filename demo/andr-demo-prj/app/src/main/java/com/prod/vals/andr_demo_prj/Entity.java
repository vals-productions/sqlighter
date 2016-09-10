package com.prod.vals.andr_demo_prj;

import java.util.Date;

/**
 * Created by vsayenko on 12/18/15.
 */
public class Entity {
    public Integer id;
    public Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
