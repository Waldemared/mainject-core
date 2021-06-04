package com.wald.mainject.config;

/**
 * @author vkosolapov
 * @since
 */
public class SomeConfiguration {
    @ToInject
    public NamingService getNamingService() {
        return new ClassAndHashNamingService();
    }
}
