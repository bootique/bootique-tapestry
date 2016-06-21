package com.nhl.bootique.tapestry.di;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;

import java.lang.annotation.Annotation;
import java.util.List;

public class GuiceObjectProvider implements ObjectProvider {

    private Injector injector;

    public GuiceObjectProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T provide(Class<T> objectType, AnnotationProvider annotationProvider, ObjectLocator locator) {

        TypeLiteral<T> type = TypeLiteral.get(objectType);
        List<Binding<T>> bindings = injector.findBindingsByType(type);

        for (Binding<T> binding : bindings) {

            Class<? extends Annotation> annotationType = binding.getKey().getAnnotationType();
            Annotation annotation = annotationType != null ? annotationProvider.getAnnotation(annotationType) : null;

            Key<T> key = annotation != null ? Key.get(type, annotation) : Key.get(type);
            if (key.equals(binding.getKey())) {
                return injector.getInstance(key);
            }
        }

        return null;
    }
}

