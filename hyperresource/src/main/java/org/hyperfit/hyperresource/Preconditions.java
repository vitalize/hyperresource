package org.hyperfit.hyperresource;

/**
 * Couple of utils to avoid having to bring in some additional library
 */
public final class Preconditions {

    private Preconditions(){

    }

    public static String notEmpty(String value, String paramName){
        if (value == null || value.trim().equals("")){
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }

        return value;
    }
}
