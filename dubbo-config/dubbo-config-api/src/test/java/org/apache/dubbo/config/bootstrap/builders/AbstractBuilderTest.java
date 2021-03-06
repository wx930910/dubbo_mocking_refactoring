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

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.AbstractConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AbstractBuilderTest {

	@Test
	void id() {
		AbstractBuilder builder = new MockBuilder().MockedBuilder;
		Mockito.doCallRealMethod().when(builder).id(Mockito.anyString());
		builder.id("id");
		Assertions.assertEquals("id", builder.build().getId());
	}

	@Test
	void prefix() {
		AbstractBuilder builder = new MockBuilder().MockedBuilder;
		Mockito.doCallRealMethod().when(builder).prefix(Mockito.anyString());
		builder.prefix("prefix");
		Assertions.assertEquals("prefix", builder.build().getPrefix());
	}

	@Test
	void appendParameter() {
		Map<String, String> source = null;

		Map<String, String> parameters = new HashMap<>();
		parameters.put("default.num", "one");
		parameters.put("num", "ONE");
		source = AbstractBuilder.appendParameters(source, parameters);

		Assertions.assertTrue(source.containsKey("default.num"));
		Assertions.assertEquals("ONE", source.get("num"));
	}

	@Test
	void appendParameter2() {
		Map<String, String> source = new HashMap<>();
		source.put("default.num", "one1");
		source.put("num", "ONE1");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("default.num", "one");
		parameters.put("num", "ONE");
		source = AbstractBuilder.appendParameters(source, parameters);

		Assertions.assertTrue(source.containsKey("default.num"));
		Assertions.assertEquals("ONE", source.get("num"));
	}

	@Test
	void appendParameters() {
		Map<String, String> source = null;

		source = AbstractBuilder.appendParameter(source, "default.num", "one");
		source = AbstractBuilder.appendParameter(source, "num", "ONE");

		Assertions.assertTrue(source.containsKey("default.num"));
		Assertions.assertEquals("ONE", source.get("num"));
	}

	@Test
	void appendParameters2() {
		Map<String, String> source = new HashMap<>();
		source.put("default.num", "one1");
		source.put("num", "ONE1");

		source = AbstractBuilder.appendParameter(source, "default.num", "one");
		source = AbstractBuilder.appendParameter(source, "num", "ONE");

		Assertions.assertTrue(source.containsKey("default.num"));
		Assertions.assertEquals("ONE", source.get("num"));
	}

	@Test
	void build() {
		AbstractBuilder builder = new MockBuilder().MockedBuilder;
		Mockito.doCallRealMethod().when(builder).id(Mockito.anyString());
		Mockito.doCallRealMethod().when(builder).prefix(Mockito.anyString());
		builder.id("id");
		builder.prefix("prefix");

		AbstractConfig config = builder.build();
		AbstractConfig config2 = builder.build();

		Assertions.assertEquals("id", config.getId());
		Assertions.assertEquals("prefix", config.getPrefix());

		Assertions.assertNotSame(config, config2);
	}

	private static class MockBuilder {
		public AbstractBuilder<AbstractConfig, AbstractBuilder> MockedBuilder;

		public MockBuilder() {
			this.MockedBuilder = Mockito.mock(AbstractBuilder.class,
					Mockito.withSettings().verboseLogging());
			Mockito.doAnswer(invocation -> {
				invocation.callRealMethod();
				return null;
			}).when(this.MockedBuilder)
					.build(Mockito.any(AbstractConfig.class));
			mockBuild();
			mockGetThis();
		}

		private void mockBuild() {
			Mockito.when(this.MockedBuilder.build()).thenAnswer(invocation -> {
				AbstractConfig parameterConfig = Mockito
						.spy(AbstractConfig.class);
				this.MockedBuilder.build(parameterConfig);
				return parameterConfig;
			});
		}

		protected void mockGetThis() {
			Mockito.when(this.MockedBuilder.getThis())
					.thenReturn(this.MockedBuilder);
		}

	}

}