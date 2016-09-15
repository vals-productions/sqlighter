package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.util.InputMismatchException;

/**
 * Created by developer on 9/13/16.
 */
public class DemoIntSetAdapter implements AnAdapter {

    @Override
    public Object convert(AnAttrib attrib, Object value) {
        Integer i = (Integer) value;
        return i + 1;
    }

    @Override
    public void onWarning(Class cluss, String attribName, Object value) {

    }
}
