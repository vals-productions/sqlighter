package com.vals.a2ios.amfibian.intf;

/**
 * Adapters value may be used to implement customized
 * attribute value conversions for specific cases.
 */
public interface AnAdapter {

    public Object convert(AnAttrib attrib, Object value);

    void onWarning(Class cluss, String attribName, Object value);
}
