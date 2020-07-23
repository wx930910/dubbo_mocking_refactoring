package org.apache.dubbo.configcenter.support.etcd;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.dubbo.remoting.etcd.Constants.SESSION_TIMEOUT_KEY;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.configcenter.ConfigChangedEvent;
import org.apache.dubbo.common.config.configcenter.ConfigurationListener;
import org.apache.dubbo.common.config.configcenter.DynamicConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.launcher.EtcdCluster;
import io.etcd.jetcd.launcher.EtcdClusterFactory;

public class EtcdDynamicConfigurationTestWithMock {
	private static EtcdDynamicConfiguration config;

	public EtcdCluster etcdCluster = EtcdClusterFactory.buildCluster(getClass().getSimpleName(), 3, false, false);

	private static Client client;

	@Before
	public void setUp() {

		etcdCluster.start();

		client = Client.builder().endpoints(etcdCluster.getClientEndpoints()).build();

		List<URI> clientEndPoints = etcdCluster.getClientEndpoints();

		String ipAddress = clientEndPoints.get(0).getHost() + ":" + clientEndPoints.get(0).getPort();
		String urlForDubbo = "etcd3://" + ipAddress + "/org.apache.dubbo.etcd.testService";

		// timeout in 15 seconds.
		URL url = URL.valueOf(urlForDubbo).addParameter(SESSION_TIMEOUT_KEY, 15000);
		config = new EtcdDynamicConfiguration(url);
	}

	@Test
	public void testGetConfig() {

		put("/dubbo/config/org.apache.dubbo.etcd.testService/configurators", "hello");
		put("/dubbo/config/test/dubbo.properties", "aaa=bbb");
		Assert.assertEquals("hello", config.getConfig("org.apache.dubbo.etcd.testService.configurators",
				DynamicConfiguration.DEFAULT_GROUP));
		Assert.assertEquals("aaa=bbb", config.getConfig("dubbo.properties", "test"));
	}

	private void put(String key, String value) {
		try {
			client.getKVClient().put(ByteSequence.from(key, UTF_8), ByteSequence.from(value, UTF_8)).get();
		} catch (Exception e) {
			System.out.println("Error put value to etcd.");
		}
	}

	/**
	 * Test Logic defined in {@link #EtcdDynamicConfiguration#onNext()}. It will
	 * invoke overridden process(). Not sure when onNext() was invoked, but I
	 * think it happened in put() methods.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddListener() throws Exception {
		CountDownLatch latch = new CountDownLatch(4);
		// Mock interface
		ConfigurationListener listener1 = Mockito.mock(ConfigurationListener.class);
		Mockito.doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(listener1).process(Mockito.any(ConfigChangedEvent.class));
		// Capture argument for further assertion
		ArgumentCaptor<ConfigChangedEvent> event1 = ArgumentCaptor.forClass(ConfigChangedEvent.class);
		// Mock interface
		ConfigurationListener listener2 = Mockito.mock(ConfigurationListener.class);
		Mockito.doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(listener2).process(Mockito.any(ConfigChangedEvent.class));
		// Capture argument for further assertion
		ArgumentCaptor<ConfigChangedEvent> event2 = ArgumentCaptor.forClass(ConfigChangedEvent.class);
		// Mock interface
		ConfigurationListener listener3 = Mockito.mock(ConfigurationListener.class);
		Mockito.doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(listener3).process(Mockito.any(ConfigChangedEvent.class));
		// Capture argument for further assertion
		ArgumentCaptor<ConfigChangedEvent> event3 = ArgumentCaptor.forClass(ConfigChangedEvent.class);
		// Mock interface
		ConfigurationListener listener4 = Mockito.mock(ConfigurationListener.class);
		Mockito.doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(listener4).process(Mockito.any(ConfigChangedEvent.class));
		// Capture argument for further assertion
		ArgumentCaptor<ConfigChangedEvent> event4 = ArgumentCaptor.forClass(ConfigChangedEvent.class);
		// TestListener listener1 = new TestListener(latch);
		// TestListener listener2 = new TestListener(latch);
		// TestListener listener3 = new TestListener(latch);
		// TestListener listener4 = new TestListener(latch);
		config.addListener("AService.configurators", listener1);
		config.addListener("AService.configurators", listener2);
		config.addListener("testapp.tagrouters", listener3);
		config.addListener("testapp.tagrouters", listener4);

		put("/dubbo/config/AService/configurators", "new value1");
		Thread.sleep(200);
		put("/dubbo/config/testapp/tagrouters", "new value2");
		Thread.sleep(200);
		put("/dubbo/config/testapp", "new value3");

		Thread.sleep(1000);

		Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
		// Verify that process was execute once
		Mockito.verify(listener1).process(event1.capture());
		Mockito.verify(listener2).process(event2.capture());
		Mockito.verify(listener3).process(event3.capture());
		Mockito.verify(listener4).process(event4.capture());

		// Key Assertions
		Assert.assertEquals("/dubbo/config/AService/configurators", event1.capture().getKey());
		Assert.assertEquals("/dubbo/config/AService/configurators", event2.capture().getKey());
		Assert.assertEquals("/dubbo/config/testapp/tagrouters", event3.capture().getKey());
		Assert.assertEquals("/dubbo/config/testapp/tagrouters", event4.capture().getKey());
		// Content Assertions
		Assert.assertEquals("new value1", event1.capture().getContent());
		Assert.assertEquals("new value1", event2.capture().getContent());
		Assert.assertEquals("new value2", event3.capture().getContent());
		Assert.assertEquals("new value2", event4.capture().getContent());
		// Assert.assertEquals(1,
		// listener1.getCount("/dubbo/config/AService/configurators"));
		// Assert.assertEquals(1,
		// listener2.getCount("/dubbo/config/AService/configurators"));
		// Assert.assertEquals(1,
		// listener3.getCount("/dubbo/config/testapp/tagrouters"));
		// Assert.assertEquals(1,
		// listener4.getCount("/dubbo/config/testapp/tagrouters"));

		// Assert.assertEquals("new value1", listener1.getValue());
		// Assert.assertEquals("new value1", listener2.getValue());
		// Assert.assertEquals("new value2", listener3.getValue());
		// Assert.assertEquals("new value2", listener4.getValue());
	}

	private class TestListener implements ConfigurationListener {
		private CountDownLatch latch;
		private String value;
		private Map<String, Integer> countMap = new HashMap<>();

		public TestListener(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void process(ConfigChangedEvent event) {
			Integer count = countMap.computeIfAbsent(event.getKey(), k -> 0);
			countMap.put(event.getKey(), ++count);
			value = event.getContent();
			latch.countDown();
		}

		public int getCount(String key) {
			return countMap.get(key);
		}

		public String getValue() {
			return value;
		}
	}

	@After
	public void tearDown() {
		etcdCluster.close();
	}
}
