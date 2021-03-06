package org.apereo.cas.util;

import org.springframework.validation.beanvalidation.BeanValidationPostProcessor;

import javax.validation.*;
import javax.validation.Path.Node;
import java.lang.annotation.ElementType;

/**
 * Provides a custom {@link TraversableResolver} that should work in JPA2 environments without the JPA2
 * restrictions (i.e. getters for all properties).
 *
 * @author Scott Battaglia
 * @since 3.4
 *
 */
public class CustomBeanValidationPostProcessor extends BeanValidationPostProcessor {

    /**
     * Instantiates a new custom bean validation post processor.
     */
    public CustomBeanValidationPostProcessor() {
        setAfterInitialization(true);
        final Configuration<?> configuration = Validation.byDefaultProvider().configure();
        configuration.traversableResolver(new TraversableResolver() {

            @Override
            public boolean isReachable(final Object traversableObject, final Node traversableProperty,
                    final Class<?> rootBeanType,
                    final Path pathToTraversableObject, final ElementType elementType) {
                return true;
            }

            @Override
            public boolean isCascadable(final Object traversableObject, final Node traversableProperty,
                    final Class<?> rootBeanType,
                    final Path pathToTraversableObject, final ElementType elementType) {
                return true;
            }
        });

        final Validator validator = configuration.buildValidatorFactory().getValidator();
        setValidator(validator);
        
    }
}
