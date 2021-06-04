package com.wald.mainject;

import ch.qos.logback.core.Appender;
import com.wald.mainject.config.property.source.PropertiesFile;
import com.wald.mainject.module.AbstractModule;
import com.wald.mainject.module.Module;
import com.wald.mainject.module.ModulesSource;
import com.wald.mainject.module.source.script.ScriptConfigurationSource;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;


/**
 * @author vkosolapov
 * @since
 */
public class Main {
    public static void main(String[] args) {
        /*Injector container = Guice.createInjector(binder -> {
        });*/
        Appender<?> appender = null;

        try {
            final URI scriptPath = PropertiesFile.class.getResource("/config/scripts/kotlinRunTimeConfiguration.kts").toURI();
            final ModulesSource scriptSource = new ScriptConfigurationSource(new File(scriptPath));
            final Module configuration = scriptSource.read().stream().findFirst().get();
            final AbstractModule withEmbedded = null;
            if (withEmbedded != null) {
                final Class<? extends Module> clazz = withEmbedded.getClass();
                System.out.println(clazz);
                for (final Annotation annotation : clazz.getAnnotations()) {
                    System.out.println(annotation);
                }
            }
            System.out.println(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        System.out.println("Some text to output");
        /*System.out.println(container.getInstance(NamingService.class).nameFor(245));*/
    }
}
