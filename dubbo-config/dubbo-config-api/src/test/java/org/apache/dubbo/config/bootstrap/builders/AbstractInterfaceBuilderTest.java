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
package org.apache.dubbo.config.bootstrap.builders;

import java.util.Collections;

import org.apache.dubbo.config.AbstractConfig;
import org.apache.dubbo.config.AbstractInterfaceConfig;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AbstractInterfaceBuilderTest {

	@Test
	void local() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.local("GreetingMock");
		Assertions.assertEquals("GreetingMock", builder.build().getLocal());
	}

	@Test
	void local1() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.local((Boolean) null);
		Assertions.assertNull(builder.build().getLocal());
		builder.local(false);
		Assertions.assertEquals("false", builder.build().getLocal());
		builder.local(true);
		Assertions.assertEquals("true", builder.build().getLocal());
	}

	@Test
	void stub() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.stub("GreetingMock");
		Assertions.assertEquals("GreetingMock", builder.build().getStub());
	}

	@Test
	void stub1() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.stub((Boolean) null);
		Assertions.assertNull(builder.build().getLocal());
		builder.stub(false);
		Assertions.assertEquals("false", builder.build().getStub());
		builder.stub(true);
		Assertions.assertEquals("true", builder.build().getStub());
	}

	@Test
	void monitor() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.monitor("123");

		MonitorConfig monitorConfig = new MonitorConfig("123");
		Assertions.assertEquals(monitorConfig, builder.build().getMonitor());
	}

	@Test
	void monitor1() {
		MonitorConfig monitorConfig = new MonitorConfig("123");
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.monitor(monitorConfig);

		Assertions.assertEquals(monitorConfig, builder.build().getMonitor());
	}

	@Test
	void proxy() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.proxy("mockproxyfactory");

		Assertions.assertEquals("mockproxyfactory", builder.build().getProxy());
	}

	@Test
	void cluster() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.cluster("mockcluster");

		Assertions.assertEquals("mockcluster", builder.build().getCluster());
	}

	@Test
	void filter() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.filter("mockfilter");

		Assertions.assertEquals("mockfilter", builder.build().getFilter());
	}

	@Test
	void listener() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.listener("mockinvokerlistener");

		Assertions.assertEquals("mockinvokerlistener", builder.build().getListener());
	}

	@Test
	void owner() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.owner("owner");

		Assertions.assertEquals("owner", builder.build().getOwner());
	}

	@Test
	void connections() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.connections(1);

		Assertions.assertEquals(1, builder.build().getConnections().intValue());
	}

	@Test
	void layer() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.layer("layer");

		Assertions.assertEquals("layer", builder.build().getLayer());
	}

	@Test
	void application() {
		ApplicationConfig applicationConfig = new ApplicationConfig();

		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.application(applicationConfig);

		Assertions.assertEquals(applicationConfig, builder.build().getApplication());
	}

	@Test
	void module() {
		ModuleConfig moduleConfig = new ModuleConfig();
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.module(moduleConfig);

		Assertions.assertEquals(moduleConfig, builder.build().getModule());
	}

	@Test
	void addRegistries() {
		RegistryConfig registryConfig = new RegistryConfig();

		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.addRegistries(Collections.singletonList(registryConfig));

		Assertions.assertEquals(1, builder.build().getRegistries().size());
		Assertions.assertSame(registryConfig, builder.build().getRegistries().get(0));
		Assertions.assertSame(registryConfig, builder.build().getRegistry());
	}

	@Test
	void addRegistry() {
		RegistryConfig registryConfig = new RegistryConfig();

		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.addRegistry(registryConfig);

		Assertions.assertEquals(1, builder.build().getRegistries().size());
		Assertions.assertSame(registryConfig, builder.build().getRegistries().get(0));
		Assertions.assertSame(registryConfig, builder.build().getRegistry());
	}

	@Test
	void registryIds() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.registryIds("registryIds");

		Assertions.assertEquals("registryIds", builder.build().getRegistryIds());
	}

	@Test
	void onconnect() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.onconnect("onconnect");

		Assertions.assertEquals("onconnect", builder.build().getOnconnect());
	}

	@Test
	void ondisconnect() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.ondisconnect("ondisconnect");

		Assertions.assertEquals("ondisconnect", builder.build().getOndisconnect());
	}

	@Test
	void metadataReportConfig() {
		MetadataReportConfig metadataReportConfig = new MetadataReportConfig();

		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.metadataReportConfig(metadataReportConfig);

		Assertions.assertEquals(metadataReportConfig, builder.build().getMetadataReportConfig());
	}

	@Test
	void configCenter() {
		ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();

		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.configCenter(configCenterConfig);

		Assertions.assertEquals(configCenterConfig, builder.build().getConfigCenter());
	}

	@Test
	void callbacks() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.callbacks(2);
		Assertions.assertEquals(2, builder.build().getCallbacks().intValue());
	}

	@Test
	void scope() {
		AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> builder = new MockInterfaceBuilder().mockingInstance;
		builder.scope("scope");

		Assertions.assertEquals("scope", builder.build().getScope());
	}

	@Test
	void build() {
		MonitorConfig monitorConfig = new MonitorConfig("123");
		ApplicationConfig applicationConfig = new ApplicationConfig();
		ModuleConfig moduleConfig = new ModuleConfig();
		RegistryConfig registryConfig = new RegistryConfig();
		MetadataReportConfig metadataReportConfig = new MetadataReportConfig();
		ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();

		InterfaceBuilder builder = new InterfaceBuilder();
		builder.id("id").prefix("prefix").local(true).stub(false).monitor("123").proxy("mockproxyfactory").cluster("mockcluster").filter("mockfilter")
				.listener("mockinvokerlistener").owner("owner").connections(1).layer("layer").application(applicationConfig).module(moduleConfig)
				.addRegistry(registryConfig).registryIds("registryIds").onconnect("onconnet").ondisconnect("ondisconnect")
				.metadataReportConfig(metadataReportConfig).configCenter(configCenterConfig).callbacks(2).scope("scope");

		AbstractInterfaceConfig config = builder.build();
		AbstractInterfaceConfig config2 = builder.build();

		Assertions.assertEquals("id", config.getId());
		Assertions.assertEquals("prefix", config.getPrefix());
		Assertions.assertEquals("true", config.getLocal());
		Assertions.assertEquals("false", config.getStub());
		Assertions.assertEquals(monitorConfig, config.getMonitor());
		Assertions.assertEquals("mockproxyfactory", config.getProxy());
		Assertions.assertEquals("mockcluster", config.getCluster());
		Assertions.assertEquals("mockfilter", config.getFilter());
		Assertions.assertEquals("mockinvokerlistener", config.getListener());
		Assertions.assertEquals("owner", config.getOwner());
		Assertions.assertEquals(1, config.getConnections().intValue());
		Assertions.assertEquals("layer", config.getLayer());
		Assertions.assertEquals(applicationConfig, config.getApplication());
		Assertions.assertEquals(moduleConfig, config.getModule());
		Assertions.assertEquals(registryConfig, config.getRegistry());
		Assertions.assertEquals("registryIds", config.getRegistryIds());
		Assertions.assertEquals("onconnet", config.getOnconnect());
		Assertions.assertEquals("ondisconnect", config.getOndisconnect());
		Assertions.assertEquals(metadataReportConfig, config.getMetadataReportConfig());
		Assertions.assertEquals(configCenterConfig, config.getConfigCenter());
		Assertions.assertEquals(2, config.getCallbacks().intValue());
		Assertions.assertEquals("scope", config.getScope());

		Assertions.assertNotSame(config, config2);
	}

	private static class InterfaceBuilder extends AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> {

		public AbstractInterfaceConfig build() {
			AbstractInterfaceConfig config = new MockInterfaceConfig().mockingInstance;
			super.build(config);

			return config;
		}

		@Override
		protected InterfaceBuilder getThis() {
			return this;
		}
	}

	private static class MockInterfaceBuilder {

		public AbstractInterfaceBuilder<AbstractInterfaceConfig, InterfaceBuilder> mockingInstance;
		public AbstractBuilder<AbstractConfig, AbstractBuilder> MockedBuilder;

		public MockInterfaceBuilder() {
			this.mockingInstance = Mockito.mock(AbstractInterfaceBuilder.class, Mockito.CALLS_REAL_METHODS);
			mockBuild();
			Mockito.doReturn(this.mockingInstance).when(this.mockingInstance).getThis();
		}

		private void mockBuild() {
			Mockito.doAnswer(invocation -> {
				AbstractInterfaceConfig config = new MockInterfaceConfig().mockingInstance;
				this.mockingInstance.build(config);
				return config;
			}).when(this.mockingInstance).build();
		}
	}

	private static class MockInterfaceConfig {
		public AbstractInterfaceConfig mockingInstance;

		public MockInterfaceConfig() {
			this.mockingInstance = Mockito.mock(AbstractInterfaceConfig.class, Mockito.CALLS_REAL_METHODS);
		}

	}

	private static class InterfaceConfig extends AbstractInterfaceConfig {
	}
}