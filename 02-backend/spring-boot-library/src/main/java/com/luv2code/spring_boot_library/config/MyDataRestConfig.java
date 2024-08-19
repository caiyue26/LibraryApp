// added notes on 2024.07.14

package com.luv2code.spring_boot_library.config;

import com.luv2code.spring_boot_library.entity.Book;
import com.luv2code.spring_boot_library.entity.Message;
import com.luv2code.spring_boot_library.entity.Review;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/*
Summary
This code configures Spring Data REST by implementing the RepositoryRestConfigurer interface to:
1. Expose the ID field of the Book entity in JSON.
2. Disable the POST, PATCH, DELETE, and PUT methods for the Book entity.
3. Configure CORS for the Library Application to allow requests from http://localhost:3000.
 */

// @Configuration: This annotation indicates that the class has @Bean definition methods and can be processed by the
// Spring container to generate bean definitions and service requests at run time.
@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    // Defines a string variable specifying the allowed CORS origin.
    private String theAllowedOrigins = "https://localhost:3000";

    // Overrides the method from the interface to configure Spring Data REST.
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Define Unsupported HTTP Methods
        HttpMethod[] theUnsupportedActions = {
                HttpMethod.POST,
                HttpMethod.PATCH,
                HttpMethod.DELETE,
                HttpMethod.PUT};

        // Configures Spring Data REST to expose the ID field of the entities in the JSON.
        // By default, Spring Data REST does not include the ID field in the JSON output. Having ID fields in JSON is
        // useful for operations like updating / deleting.
        config.exposeIdsFor(Book.class);
        config.exposeIdsFor(Review.class);
        config.exposeIdsFor(Message.class);

        // Disable the specified HTTP methods for the Book entity.
        disableHttpMethods(Book.class, config, theUnsupportedActions);
        disableHttpMethods(Review.class, config, theUnsupportedActions);
        disableHttpMethods(Message.class, config, theUnsupportedActions);

        /* Configure CORS Mapping */
        // Allows requests from the origin defined by 'theAllowedOrigins'
        cors.addMapping(config.getBasePath() + "/**")
                .allowedOrigins(theAllowedOrigins);
    }

    // Private method to disable HTTP methods
    private void disableHttpMethods(Class theClass,
                                    RepositoryRestConfiguration config,
                                    HttpMethod[] theUnsupportedActions) {
        // Retrieves the exposure configuration for the specified domain type (entity class)
        // Disables the specified HTTP methods for single items -> collections
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metdata, httpMethods) ->
                        httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metdata, httpMethods) ->
                        httpMethods.disable(theUnsupportedActions));
    }
}

/*
Class theClass: The entity class for which the methods should be disabled.

RepositoryRestConfiguration config: The REST configuration object.

config.getExposureConfiguration().forDomainType(theClass):
gets the exposure configuration for the specified domain type (entity class).

.withItemExposure((metdata, httpMethods) -> httpMethods.disable()):
disables the specified HTTP methods for single item requests (e.g., `/books/1`).

.withCollectionExposure...
disables the specified HTTP methods for collection requests (e.g., `/books`)

 */


