package com.wald.mainject.config;

import com.google.inject.Provides;


/**
 * @author vkosolapov
 * @since
 */
public class SomeGuiceCon {
    @Provides
    NamingService getService() {
        return new ClassAndHashNamingService();
    }
}
