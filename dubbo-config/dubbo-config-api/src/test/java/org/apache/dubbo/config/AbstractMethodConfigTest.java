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
package org.apache.dubbo.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.sameInstance;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractMethodConfigTest {
	@Test
	public void testTimeout() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setTimeout(10);
		assertThat(methodConfig.instance.getTimeout(), equalTo(10));
	}

	@Test
	public void testForks() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setForks(10);
		assertThat(methodConfig.instance.getForks(), equalTo(10));
	}

	@Test
	public void testRetries() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setRetries(3);
		assertThat(methodConfig.instance.getRetries(), equalTo(3));
	}

	@Test
	public void testLoadbalance() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setLoadbalance("mockloadbalance");
		assertThat(methodConfig.instance.getLoadbalance(),
				equalTo("mockloadbalance"));
	}

	@Test
	public void testAsync() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setAsync(true);
		assertThat(methodConfig.instance.isAsync(), is(true));
	}

	@Test
	public void testActives() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setActives(10);
		assertThat(methodConfig.instance.getActives(), equalTo(10));
	}

	@Test
	public void testSent() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setSent(true);
		assertThat(methodConfig.instance.getSent(), is(true));
	}

	@Test
	public void testMock() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setMock((Boolean) null);
		assertThat(methodConfig.instance.getMock(), isEmptyOrNullString());
		methodConfig.instance.setMock(true);
		assertThat(methodConfig.instance.getMock(), equalTo("true"));
		methodConfig.instance.setMock("return null");
		assertThat(methodConfig.instance.getMock(), equalTo("return null"));
		methodConfig.instance.setMock("mock");
		assertThat(methodConfig.instance.getMock(), equalTo("mock"));
	}

	@Test
	public void testMerger() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setMerger("merger");
		assertThat(methodConfig.instance.getMerger(), equalTo("merger"));
	}

	@Test
	public void testCache() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setCache("cache");
		assertThat(methodConfig.instance.getCache(), equalTo("cache"));
	}

	@Test
	public void testValidation() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		methodConfig.instance.setValidation("validation");
		assertThat(methodConfig.instance.getValidation(),
				equalTo("validation"));
	}

	@Test
	public void testParameters() throws Exception {
		MockMethodConfig methodConfig = new MockMethodConfig();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("key", "value");
		methodConfig.instance.setParameters(parameters);
		assertThat(methodConfig.instance.getParameters(),
				sameInstance(parameters));
	}

	private static class MockMethodConfig {
		public AbstractMethodConfig instance;

		public MockMethodConfig() {
			this.instance = Mockito.mock(AbstractMethodConfig.class, Mockito
					.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS));
		}

	}
}
