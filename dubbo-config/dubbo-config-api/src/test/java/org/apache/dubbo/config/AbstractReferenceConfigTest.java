/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.config;

import static org.apache.dubbo.common.constants.CommonConstants.GENERIC_SERIALIZATION_NATIVE_JAVA;
import static org.apache.dubbo.common.constants.CommonConstants.INVOKER_LISTENER_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.REFERENCE_FILTER_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.STUB_EVENT_KEY;
import static org.apache.dubbo.rpc.cluster.Constants.CLUSTER_STICKY_KEY;
import static org.apache.dubbo.rpc.cluster.Constants.ROUTER_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.rpc.cluster.RouterFactory;
import org.apache.dubbo.rpc.cluster.router.condition.ConditionRouterFactory;
import org.apache.dubbo.rpc.cluster.router.condition.config.AppRouterFactory;
import org.apache.dubbo.rpc.cluster.router.tag.TagRouterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractReferenceConfigTest {

	@Test
	public void testCheck() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setCheck(true);
		assertThat(referenceConfig.isCheck(), is(true));
	}

	@Test
	public void testInit() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setInit(true);
		assertThat(referenceConfig.isInit(), is(true));
	}

	@Test
	public void testGeneric() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setGeneric(true);
		assertThat(referenceConfig.isGeneric(), is(true));
		Map<String, String> parameters = new HashMap<String, String>();
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		// FIXME: not sure why AbstractReferenceConfig has both isGeneric and
		// getGeneric
		assertThat(parameters, hasKey("generic"));
	}

	@Test
	public void testInjvm() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setInit(true);
		assertThat(referenceConfig.isInit(), is(true));
	}

	@Test
	public void testFilter() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setFilter("mockfilter");
		assertThat(referenceConfig.getFilter(), equalTo("mockfilter"));
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(REFERENCE_FILTER_KEY, "prefilter");
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(parameters, hasValue("prefilter,mockfilter"));
	}

	@Test
	public void testRouter() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setRouter("condition");
		assertThat(referenceConfig.getRouter(), equalTo("condition"));
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(ROUTER_KEY, "tag");
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(parameters, hasValue("tag,condition"));
		URL url = mock(URL.class);
		when(url.getParameter(ROUTER_KEY)).thenReturn("condition");
		List<RouterFactory> routerFactories = ExtensionLoader
				.getExtensionLoader(RouterFactory.class)
				.getActivateExtension(url, ROUTER_KEY);
		assertThat(
				routerFactories.stream()
						.anyMatch(routerFactory -> routerFactory.getClass()
								.equals(ConditionRouterFactory.class)),
				is(true));
		when(url.getParameter(ROUTER_KEY)).thenReturn("-tag,-app");
		routerFactories = ExtensionLoader
				.getExtensionLoader(RouterFactory.class)
				.getActivateExtension(url, ROUTER_KEY);
		assertThat(
				routerFactories.stream()
						.allMatch(routerFactory -> !routerFactory.getClass()
								.equals(TagRouterFactory.class)
								&& !routerFactory.getClass()
										.equals(AppRouterFactory.class)),
				is(true));
	}

	@Test
	public void testListener() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setListener("mockinvokerlistener");
		assertThat(referenceConfig.getListener(),
				equalTo("mockinvokerlistener"));
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(INVOKER_LISTENER_KEY, "prelistener");
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(parameters, hasValue("prelistener,mockinvokerlistener"));
	}

	@Test
	public void testLazy() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setLazy(true);
		assertThat(referenceConfig.getLazy(), is(true));
	}

	@Test
	public void testOnconnect() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setOnconnect("onConnect");
		assertThat(referenceConfig.getOnconnect(), equalTo("onConnect"));
		assertThat(referenceConfig.getStubevent(), is(true));
	}

	@Test
	public void testOndisconnect() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setOndisconnect("onDisconnect");
		assertThat(referenceConfig.getOndisconnect(), equalTo("onDisconnect"));
		assertThat(referenceConfig.getStubevent(), is(true));
	}

	@Test
	public void testStubevent() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setOnconnect("onConnect");
		Map<String, String> parameters = new HashMap<String, String>();
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(parameters, hasKey(STUB_EVENT_KEY));
	}

	@Test
	public void testReconnect() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setReconnect("reconnect");
		Map<String, String> parameters = new HashMap<String, String>();
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(referenceConfig.getReconnect(), equalTo("reconnect"));
		assertThat(parameters, hasKey(Constants.RECONNECT_KEY));
	}

	@Test
	public void testSticky() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setSticky(true);
		Map<String, String> parameters = new HashMap<String, String>();
		AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
		assertThat(referenceConfig.getSticky(), is(true));
		assertThat(parameters, hasKey(CLUSTER_STICKY_KEY));
	}

	@Test
	public void testVersion() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setVersion("version");
		assertThat(referenceConfig.getVersion(), equalTo("version"));
	}

	@Test
	public void testGroup() throws Exception {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setGroup("group");
		assertThat(referenceConfig.getGroup(), equalTo("group"));
	}

	@Test
	public void testGenericOverride() {
		AbstractReferenceConfig referenceConfig = new MockReferenceConfig().instance;
		referenceConfig.setGeneric("false");
		referenceConfig.refresh();
		Assertions.assertFalse(referenceConfig.isGeneric());
		Assertions.assertEquals("false", referenceConfig.getGeneric());

		AbstractReferenceConfig referenceConfig1 = new MockReferenceConfig().instance;
		referenceConfig1.setGeneric(GENERIC_SERIALIZATION_NATIVE_JAVA);
		referenceConfig1.refresh();
		Assertions.assertEquals(GENERIC_SERIALIZATION_NATIVE_JAVA,
				referenceConfig1.getGeneric());
		Assertions.assertTrue(referenceConfig1.isGeneric());

		AbstractReferenceConfig referenceConfig2 = new MockReferenceConfig().instance;
		referenceConfig2.refresh();
		Assertions.assertNull(referenceConfig2.getGeneric());
	}

	private static class MockReferenceConfig {
		public AbstractReferenceConfig instance = Mockito.mock(
				AbstractReferenceConfig.class, Mockito.CALLS_REAL_METHODS);
	}

}
