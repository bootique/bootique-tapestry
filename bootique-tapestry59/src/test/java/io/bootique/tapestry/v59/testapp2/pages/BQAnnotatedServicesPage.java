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

package io.bootique.tapestry.v59.testapp2.pages;

import io.bootique.annotation.Args;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BQAnnotatedServicesPage {

    @Args
    @Inject
    private String[] args;

    public String getProperty() {
        return Arrays.asList(args).stream().sorted().collect(Collectors.joining("_"));
    }

}
