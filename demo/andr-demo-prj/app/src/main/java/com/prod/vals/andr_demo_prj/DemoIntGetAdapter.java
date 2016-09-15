package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;

/**
 * Created by developer on 9/13/16.
 */
public class DemoIntGetAdapter implements AnAdapter {
    @Override
    public Object convert(AnAttrib attrib, Object value) {
        int returnValue = ((value == null) ? 0 : (Integer)value);
        return returnValue;
    }

    @Override
    public void onWarning(Class cluss, String attribName, Object value) {

    }
}
