package com.wald.mainject;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;


/**
 * @author vkosolapov
 * @since
 */
public class Example {

    public void someAction() {
        PropertySource source = new HttpPropertySource();
        String propertyValue = source.getProperty("name");
        String propertyValue2 = source.getProperty("name");

        source = new CachingPropertySource(new HttpPropertySource());
        String propertyValue3 = source.getProperty("name");
        String propertyValue6 = source.getProperty("name");
    }

    interface PropertySource {
        String getProperty(String propertyName);
    }

    class DatabasePropertySource implements PropertySource {
        private DataSource dataSource;

        @Override
        public String getProperty(String propertyName) {
            try {
                String value = dataSource.getConnection()
                        .prepareStatement("select value from property where name = ?")
                        .executeQuery()
                        .getString(0);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //dataSource.getConnection()...
            return "some value";
        }
    }

    class HttpPropertySource implements PropertySource {
        private HttpClient httpClient;

        @Override
        public String getProperty(String propertyName) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://gege.pfgew"))
                    .build();

            String value = httpClient.sendAsync(request, body -> HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8).apply(body))
                    .thenApply(HttpResponse::body)
                    .join();
            // get data
            return "property from HTTP";
        }
    }

    class CachingPropertySource implements PropertySource {
        private PropertySource source;
        private Map<String, String> cache;

        CachingPropertySource(PropertySource source) {
            this.source = source;
        }

        @Override
        public String getProperty(String propertyName) {
            if (cache.containsKey(propertyName)) {
                return cache.get(propertyName);
            }

            String propertyValue =  source.getProperty(propertyName);
            cache.put(propertyName, propertyValue);

            return propertyValue;
        }
    }

}
