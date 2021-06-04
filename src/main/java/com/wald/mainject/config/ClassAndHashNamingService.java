package com.wald.mainject.config;

/**
 * @author vkosolapov
 * @since
 */
public class ClassAndHashNamingService implements NamingService {
    @Override
    public String nameFor(Object object) {
        return object.getClass().getSimpleName() + Long.toHexString(object.hashCode());
    }
}
