package com.wald.mainject.config;

/**
 * @author vkosolapov
 * @since
 */
public class ToStringNamingService implements NamingService {
    @Override
    public String nameFor(Object object) {
        return object.toString();
    }
}
