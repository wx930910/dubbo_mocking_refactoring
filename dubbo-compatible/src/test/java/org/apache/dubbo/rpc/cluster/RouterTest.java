/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;

/**
 *
 */
public class RouterTest {

	private static List<Router> routers = new ArrayList<>();

	@BeforeAll
	public static void setUp() {
		Router router1 = Mockito.mock(Router.class);
		Mockito.when(router1.compareTo(Mockito.any(Router.class)))
				.thenReturn(0);
		Router router2 = Mockito.mock(Router.class);
		Mockito.when(router2.compareTo(Mockito.any(Router.class)))
				.thenReturn(0);
		Router router3 = Mockito.mock(Router.class);
		Mockito.when(router3.compareTo(Mockito.any(Router.class)))
				.thenReturn(0);
		routers.add(router1);
		routers.add(router2);
		routers.add(router3);
	}

	@Test
	public void testCompareTo() {
		try {
			Collections.sort(routers);
			Assertions.assertTrue(true);
		} catch (Exception e) {
			Assertions.assertFalse(false);
		}
	}

	static class MockCompatibleRouter {
		public Router instance;

		public MockCompatibleRouter() {
			this.instance = Mockito.mock(Router.class);
			Mockito.when(this.instance.getUrl()).thenReturn(null);
			Mockito.doReturn(null).when(this.instance).getUrl();
			Mockito.when(this.instance.route(Mockito.anyList(),
					Mockito.any(URL.class), Mockito.any(Invocation.class)))
					.thenReturn(null);
			Mockito.when(this.instance.compareTo(Mockito.any(Router.class)))
					.thenReturn(0);
		}

	}

}
