package org.apache.dubbo.config.bootstrap.builders;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.AbstractMethodConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractMethodBuilderTestWithMock {
	@Test
	void timeout() {
		MethodBuilder builder = new MethodBuilder();
		builder.timeout(10);

		Assertions.assertEquals(10, builder.build().getTimeout());
	}

	@Test
	void retries() {
		MethodBuilder builder = new MethodBuilder();
		builder.retries(3);

		Assertions.assertEquals(3, builder.build().getRetries());
	}

	@Test
	void actives() {
		MethodBuilder builder = new MethodBuilder();
		builder.actives(3);

		Assertions.assertEquals(3, builder.build().getActives());
	}

	@Test
	void loadbalance() {
		MethodBuilder builder = new MethodBuilder();
		builder.loadbalance("mockloadbalance");

		Assertions.assertEquals("mockloadbalance",
				builder.build().getLoadbalance());
	}

	@Test
	void async() {
		MethodBuilder builder = new MethodBuilder();
		builder.async(true);

		Assertions.assertTrue(builder.build().isAsync());
	}

	@Test
	void sent() {
		MethodBuilder builder = new MethodBuilder();
		builder.sent(true);

		Assertions.assertTrue(builder.build().getSent());
	}

	@Test
	void mock() {
		MethodBuilder builder = new MethodBuilder();
		builder.mock("mock");
		Assertions.assertEquals("mock", builder.build().getMock());
		builder.mock("return null");
		Assertions.assertEquals("return null", builder.build().getMock());
	}

	@Test
	void mock1() {
		MethodBuilder builder = new MethodBuilder();
		builder.mock(true);
		Assertions.assertEquals("true", builder.build().getMock());
		builder.mock(false);
		Assertions.assertEquals("false", builder.build().getMock());
	}

	@Test
	void merger() {
		MethodBuilder builder = new MethodBuilder();
		builder.merger("merger");
		Assertions.assertEquals("merger", builder.build().getMerger());
	}

	@Test
	void cache() {
		MethodBuilder builder = new MethodBuilder();
		builder.cache("cache");
		Assertions.assertEquals("cache", builder.build().getCache());
	}

	@Test
	void validation() {
		MethodBuilder builder = new MethodBuilder();
		builder.validation("validation");
		Assertions.assertEquals("validation", builder.build().getValidation());
	}

	@Test
	void appendParameter() {
		MethodBuilder builder = new MethodBuilder();
		builder.appendParameter("default.num", "one").appendParameter("num",
				"ONE");

		Map<String, String> parameters = builder.build().getParameters();

		Assertions.assertTrue(parameters.containsKey("default.num"));
		Assertions.assertEquals("ONE", parameters.get("num"));
	}

	@Test
	void appendParameters() {
		Map<String, String> source = new HashMap<>();
		source.put("default.num", "one");
		source.put("num", "ONE");

		MethodBuilder builder = new MethodBuilder();
		builder.appendParameters(source);

		Map<String, String> parameters = builder.build().getParameters();

		Assertions.assertTrue(parameters.containsKey("default.num"));
		Assertions.assertEquals("ONE", parameters.get("num"));
	}

	@Test
	void forks() {
		MethodBuilder builder = new MethodBuilder();
		builder.forks(5);

		Assertions.assertEquals(5, builder.build().getForks());
	}

	@Test
	void build() {
		MethodBuilder builder = new MethodBuilder();
		builder.id("id").prefix("prefix").timeout(1).retries(2).actives(3)
				.loadbalance("mockloadbalance").async(true).sent(false)
				.mock("mock").merger("merger").cache("cache")
				.validation("validation").appendParameter("default.num", "one");

		AbstractMethodConfig config = builder.build();
		AbstractMethodConfig config2 = builder.build();

		Assertions.assertEquals("id", config.getId());
		Assertions.assertEquals("prefix", config.getPrefix());
		Assertions.assertEquals(1, config.getTimeout());
		Assertions.assertEquals(2, config.getRetries());
		Assertions.assertEquals(3, config.getActives());
		Assertions.assertEquals("mockloadbalance", config.getLoadbalance());
		Assertions.assertTrue(config.isAsync());
		Assertions.assertFalse(config.getSent());
		Assertions.assertEquals("mock", config.getMock());
		Assertions.assertEquals("merger", config.getMerger());
		Assertions.assertEquals("cache", config.getCache());
		Assertions.assertEquals("validation", config.getValidation());
		Assertions
				.assertTrue(config.getParameters().containsKey("default.num"));
		Assertions.assertEquals("one",
				config.getParameters().get("default.num"));

		Assertions.assertNotSame(config, config2);

	}

	private static class MethodBuilder
			extends AbstractMethodBuilder<AbstractMethodConfig, MethodBuilder> {

		public AbstractMethodConfig build() {
			AbstractMethodConfig parameterConfig = new MockMethodConfig().instance;
			super.build(parameterConfig);

			return parameterConfig;
		}

		@Override
		protected MethodBuilder getThis() {
			return this;
		}
	}

	private static class MockMethodBuilder {
		public AbstractMethodBuilder<AbstractMethodConfig, MethodBuilder> instance;

		public MockMethodBuilder() {
			this.instance = Mockito.mock(AbstractMethodBuilder.class,
					Mockito.CALLS_REAL_METHODS);
			mockBuild();
			mockGetThis();
		}

		private void mockBuild() {
			Mockito.doAnswer(invocation -> {
				AbstractMethodConfig parameterConfig = new MockMethodConfig().instance;
				this.instance.build(parameterConfig);
				return parameterConfig;
			}).when(this.instance).build();
		}

		private void mockGetThis() {
			Mockito.doReturn(this.instance).when(this.instance).getThis();
		}

	}

	private static class MockMethodConfig {
		public AbstractMethodConfig instance;

		public MockMethodConfig() {
			this.instance = Mockito.mock(AbstractMethodConfig.class, Mockito
					.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS));
		}

	}

}
