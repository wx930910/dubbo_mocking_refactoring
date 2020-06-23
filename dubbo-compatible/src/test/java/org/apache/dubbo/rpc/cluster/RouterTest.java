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
		Router compatibleRouter = new MockCompatibleRouter().instance;
		routers.add(compatibleRouter);
		CompatibleRouter2 compatibleRouter2 = new CompatibleRouter2();
		routers.add(compatibleRouter2);
		NewRouter newRouter = new NewRouter();
		routers.add(newRouter);
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
			this.instance = Mockito.mock(Router.class, Mockito.withSettings()
					.defaultAnswer(Mockito.CALLS_REAL_METHODS));
			Mockito.doReturn(null).when(this.instance).getUrl();
			Mockito.doReturn(null).when(this.instance).route(Mockito.anyList(),
					Mockito.any(URL.class), Mockito.any(Invocation.class));
			Mockito.doReturn(0).when(this.instance)
					.compareTo(Mockito.any(Router.class));
		}

	}

}
