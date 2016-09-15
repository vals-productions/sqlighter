package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.util.Date;

/**
 * Demo converter
 *
 * Created by vsayenko on 9/12/16.
 */
public class DemoDefaultGetAdapter implements AnAdapter {

    @Override
    public Object convert(AnAttrib attrib, Object value) {
        if (value != null && value instanceof Date) {
            Date d = (Date)value;
            return new Long(d.getTime());
        }
        return value;
    }

    @Override
    public void onWarning(Class cluss, String attribName, Object value) {

    }
}
