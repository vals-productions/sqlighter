package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;

/**
 * Created by developer on 9/14/16.
 */
public class DemoAppointmentGetAdapter implements AnAdapter {
    @Override
    public Object convert(AnAttrib attrib, Object value) {
        if(attrib.getAttribName().equals("isProcessed")) {
            int intValue = (value == null) ? 0 : (Integer) value;
            if(intValue != 0) {
                intValue = 1;
            }
            return intValue;
        }
        return value;
    }

    @Override
    public void onWarning(Class cluss, String attribName, Object value) {
    }
}
