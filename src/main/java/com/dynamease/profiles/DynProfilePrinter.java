package com.dynamease.profiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 */

/**
 * @author Yves Nicolas
 * 
 */
public class DynProfilePrinter implements ProfilePrinter {

    private static final String SEP = ":";
    private static final String SEP2 = " ,";

    /**
     * 
     */
    public DynProfilePrinter() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.ProfilePrinter#prettyPrintasString(java.lang.Object)
     */
    @Override
    public String prettyPrintasString(Object profile) {
        Class<? extends Object> c = profile.getClass();

        Method[] methods = c.getMethods();
        

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith("get") && !name.equalsIgnoreCase("getclass")) {
   
                try {
                    Object value = methods[i].invoke(profile);
                    if (value != null) {
                       
                        sb.append(name.substring(3));
                        sb.append(SEP);
                        sb.append(value.toString());
                        sb.append(SEP2);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    sb.append("unable to retrieve");
                }
                
            }

        }

        return sb.toString();

    }

}
