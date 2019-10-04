/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.tapestry.v55.di;

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

