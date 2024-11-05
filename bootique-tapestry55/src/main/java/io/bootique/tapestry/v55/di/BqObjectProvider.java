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

import io.bootique.di.Injector;
import io.bootique.di.Key;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;

import java.lang.annotation.Annotation;

/**
 * @deprecated in favor of 5.9 Jakarta (or later) modules
 */
@Deprecated(since = "3.0", forRemoval = true)
public class BqObjectProvider implements ObjectProvider {

    private Injector injector;

    public BqObjectProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T provide(Class<T> objectType, AnnotationProvider annotationProvider, ObjectLocator locator) {
        for(Key<? extends T> key : injector.getKeysByType(objectType)) {
            Class<? extends Annotation> annotationType = key.getBindingAnnotation();
            Annotation annotation = annotationType != null ? annotationProvider.getAnnotation(annotationType) : null;
            Key<? extends T> qualifiedKey = annotation != null ?  Key.get(objectType, annotation) : key;
            return injector.getInstance(qualifiedKey);
        }

        return null;
    }
}

