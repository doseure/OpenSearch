/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.action;

import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.action.NotifyOnceListener;

import java.util.concurrent.atomic.AtomicReference;

public class NotifyOnceListenerTests extends OpenSearchTestCase {

    public void testWhenSuccessCannotNotifyMultipleTimes() {
        AtomicReference<String> response = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();

        NotifyOnceListener<String> listener = new NotifyOnceListener<String>() {
            @Override
            public void innerOnResponse(String s) {
                response.set(s);
            }

            @Override
            public void innerOnFailure(Exception e) {
                exception.set(e);
            }
        };

        listener.onResponse("response");
        listener.onResponse("wrong-response");
        listener.onFailure(new RuntimeException());

        assertNull(exception.get());
        assertEquals("response", response.get());
    }

    public void testWhenErrorCannotNotifyMultipleTimes() {
        AtomicReference<String> response = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();

        NotifyOnceListener<String> listener = new NotifyOnceListener<String>() {
            @Override
            public void innerOnResponse(String s) {
                response.set(s);
            }

            @Override
            public void innerOnFailure(Exception e) {
                exception.set(e);
            }
        };

        RuntimeException expected = new RuntimeException();
        listener.onFailure(expected);
        listener.onFailure(new IllegalArgumentException());
        listener.onResponse("response");

        assertNull(response.get());
        assertSame(expected, exception.get());
    }
}
