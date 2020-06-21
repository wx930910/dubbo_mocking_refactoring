package org.apache.dubbo.metadata.report.support;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER_SIDE;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.metadata.definition.ServiceDefinitionBuilder;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.report.identifier.KeyTypeEnum;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.metadata.report.identifier.ServiceMetadataIdentifier;
import org.apache.dubbo.metadata.report.identifier.SubscriberMetadataIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

public class AbstractMetadataReportTestWithMock {
	private MockNewMetadataReport abstractMetadataReport;

	@BeforeEach
	public void before() {
		URL url = URL.valueOf("zookeeper://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic");
		abstractMetadataReport = new MockNewMetadataReport(url);
	}

	@Test
	public void testGetProtocol() {
		URL url = URL.valueOf("dubbo://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic&side=provider");
		String protocol = abstractMetadataReport.mockingInstance
				.getProtocol(url);
		Assertions.assertEquals(protocol, "provider");

		URL url2 = URL.valueOf("consumer://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic");
		String protocol2 = abstractMetadataReport.mockingInstance
				.getProtocol(url2);
		Assertions.assertEquals(protocol2, "consumer");
	}

	@Test
	public void testStoreProviderUsual()
			throws ClassNotFoundException, InterruptedException {
		String interfaceName = "org.apache.dubbo.metadata.store.InterfaceNameTestService";
		String version = "1.0.0";
		String group = null;
		String application = "vic";
		MetadataIdentifier providerMetadataIdentifier = storePrivider(
				abstractMetadataReport.mockingInstance, interfaceName, version,
				group, application);
		Thread.sleep(1500);
		Assertions.assertNotNull(
				abstractMetadataReport.store.get(providerMetadataIdentifier
						.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));
	}

	@Test
	public void testStoreProviderSync()
			throws ClassNotFoundException, InterruptedException {
		String interfaceName = "org.apache.dubbo.metadata.store.InterfaceNameTestService";
		String version = "1.0.0";
		String group = null;
		String application = "vic";
		abstractMetadataReport.mockingInstance.syncReport = true;
		MetadataIdentifier providerMetadataIdentifier = storePrivider(
				abstractMetadataReport.mockingInstance, interfaceName, version,
				group, application);
		Assertions.assertNotNull(
				abstractMetadataReport.store.get(providerMetadataIdentifier
						.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));
	}

	@Test
	public void testFileExistAfterPut()
			throws InterruptedException, ClassNotFoundException {
		// just for one method
		URL singleUrl = URL.valueOf("redis://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.metadata.store.InterfaceNameTestService?version=1.0.0&application=singleTest");
		MockNewMetadataReport singleMetadataReport = new MockNewMetadataReport(
				singleUrl);

		Assertions.assertFalse(
				singleMetadataReport.mockingInstance.file.exists());

		String interfaceName = "org.apache.dubbo.metadata.store.InterfaceNameTestService";
		String version = "1.0.0";
		String group = null;
		String application = "vic";
		MetadataIdentifier providerMetadataIdentifier = storePrivider(
				singleMetadataReport.mockingInstance, interfaceName, version,
				group, application);

		Thread.sleep(2000);
		Assertions
				.assertTrue(singleMetadataReport.mockingInstance.file.exists());
		Assertions.assertTrue(singleMetadataReport.mockingInstance.properties
				.containsKey(providerMetadataIdentifier
						.getUniqueKey(KeyTypeEnum.UNIQUE_KEY)));
	}

	@Test
	public void testRetry()
			throws InterruptedException, ClassNotFoundException {
		String interfaceName = "org.apache.dubbo.metadata.store.RetryTestService";
		String version = "1.0.0.retry";
		String group = null;
		String application = "vic.retry";
		URL storeUrl = URL.valueOf("retryReport://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestServiceForRetry?version=1.0.0.retry&application=vic.retry");
		MockRetryMetadataReport retryReport = new MockRetryMetadataReport(
				storeUrl, 2);
		retryReport.instance.metadataReportRetry.retryPeriod = 400L;
		URL url = URL.valueOf("dubbo://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic");
		Assertions.assertNull(
				retryReport.instance.metadataReportRetry.retryScheduledFuture);
		Assertions.assertEquals(0,
				retryReport.instance.metadataReportRetry.retryCounter.get());
		Assertions.assertTrue(retryReport.store.isEmpty());
		Assertions.assertTrue(retryReport.instance.failedReports.isEmpty());

		storePrivider(retryReport.instance, interfaceName, version, group,
				application);
		Thread.sleep(150);

		Assertions.assertTrue(retryReport.store.isEmpty());
		Assertions.assertFalse(retryReport.instance.failedReports.isEmpty());
		Assertions.assertNotNull(
				retryReport.instance.metadataReportRetry.retryScheduledFuture);
		Thread.sleep(2000L);
		Assertions.assertTrue(
				retryReport.instance.metadataReportRetry.retryCounter
						.get() != 0);
		Assertions.assertTrue(
				retryReport.instance.metadataReportRetry.retryCounter
						.get() >= 3);
		Assertions.assertFalse(retryReport.store.isEmpty());
		Assertions.assertTrue(retryReport.instance.failedReports.isEmpty());
	}

	@Test
	public void testRetryCancel()
			throws InterruptedException, ClassNotFoundException {
		String interfaceName = "org.apache.dubbo.metadata.store.RetryTestService";
		String version = "1.0.0.retrycancel";
		String group = null;
		String application = "vic.retry";
		URL storeUrl = URL.valueOf("retryReport://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestServiceForRetryCancel?version=1.0.0.retrycancel&application=vic.retry");
		MockRetryMetadataReport retryReport = new MockRetryMetadataReport(
				storeUrl, 2);
		retryReport.instance.metadataReportRetry.retryPeriod = 150L;
		retryReport.instance.metadataReportRetry.retryTimesIfNonFail = 2;

		storePrivider(retryReport.instance, interfaceName, version, group,
				application);
		Thread.sleep(80);

		Assertions.assertFalse(
				retryReport.instance.metadataReportRetry.retryScheduledFuture
						.isCancelled());
		Assertions.assertFalse(
				retryReport.instance.metadataReportRetry.retryExecutor
						.isShutdown());
		Thread.sleep(1000L);
		Assertions.assertTrue(
				retryReport.instance.metadataReportRetry.retryScheduledFuture
						.isCancelled());
		Assertions.assertTrue(
				retryReport.instance.metadataReportRetry.retryExecutor
						.isShutdown());

	}

	private MetadataIdentifier storePrivider(
			AbstractMetadataReport abstractMetadataReport, String interfaceName,
			String version, String group, String application)
			throws ClassNotFoundException {
		URL url = URL.valueOf("xxx://"
				+ NetUtils.getLocalAddress().getHostName() + ":4444/"
				+ interfaceName + "?version=" + version + "&application="
				+ application + (group == null ? "" : "&group=" + group)
				+ "&testPKey=8989");

		MetadataIdentifier providerMetadataIdentifier = new MetadataIdentifier(
				interfaceName, version, group, PROVIDER_SIDE, application);
		Class interfaceClass = Class.forName(interfaceName);
		FullServiceDefinition fullServiceDefinition = ServiceDefinitionBuilder
				.buildFullDefinition(interfaceClass, url.getParameters());

		abstractMetadataReport.storeProviderMetadata(providerMetadataIdentifier,
				fullServiceDefinition);

		return providerMetadataIdentifier;
	}

	private MetadataIdentifier storeConsumer(
			AbstractMetadataReport abstractMetadataReport, String interfaceName,
			String version, String group, String application,
			Map<String, String> tmp) throws ClassNotFoundException {
		URL url = URL.valueOf("xxx://"
				+ NetUtils.getLocalAddress().getHostName() + ":4444/"
				+ interfaceName + "?version=" + version + "&application="
				+ application + (group == null ? "" : "&group=" + group)
				+ "&testPKey=9090");

		tmp.putAll(url.getParameters());
		MetadataIdentifier consumerMetadataIdentifier = new MetadataIdentifier(
				interfaceName, version, group, CONSUMER_SIDE, application);

		abstractMetadataReport.storeConsumerMetadata(consumerMetadataIdentifier,
				tmp);

		return consumerMetadataIdentifier;
	}

	@Test
	public void testPublishAll()
			throws ClassNotFoundException, InterruptedException {

		Assertions.assertTrue(abstractMetadataReport.store.isEmpty());
		Assertions.assertTrue(
				abstractMetadataReport.mockingInstance.allMetadataReports
						.isEmpty());
		String interfaceName = "org.apache.dubbo.metadata.store.InterfaceNameTestService";
		String version = "1.0.0";
		String group = null;
		String application = "vic";
		MetadataIdentifier providerMetadataIdentifier1 = storePrivider(
				abstractMetadataReport.mockingInstance, interfaceName, version,
				group, application);
		Thread.sleep(1000);
		Assertions.assertEquals(
				abstractMetadataReport.mockingInstance.allMetadataReports
						.size(),
				1);
		Assertions.assertTrue(
				((FullServiceDefinition) abstractMetadataReport.mockingInstance.allMetadataReports
						.get(providerMetadataIdentifier1)).getParameters()
								.containsKey("testPKey"));

		MetadataIdentifier providerMetadataIdentifier2 = storePrivider(
				abstractMetadataReport.mockingInstance, interfaceName,
				version + "_2", group + "_2", application);
		Thread.sleep(1000);
		Assertions.assertEquals(
				abstractMetadataReport.mockingInstance.allMetadataReports
						.size(),
				2);
		Assertions.assertTrue(
				((FullServiceDefinition) abstractMetadataReport.mockingInstance.allMetadataReports
						.get(providerMetadataIdentifier2)).getParameters()
								.containsKey("testPKey"));
		Assertions.assertEquals(
				((FullServiceDefinition) abstractMetadataReport.mockingInstance.allMetadataReports
						.get(providerMetadataIdentifier2)).getParameters()
								.get("version"),
				version + "_2");

		Map<String, String> tmpMap = new HashMap<>();
		tmpMap.put("testKey", "value");
		MetadataIdentifier consumerMetadataIdentifier = storeConsumer(
				abstractMetadataReport.mockingInstance, interfaceName,
				version + "_3", group + "_3", application, tmpMap);
		Thread.sleep(1000);
		Assertions.assertEquals(
				abstractMetadataReport.mockingInstance.allMetadataReports
						.size(),
				3);

		Map tmpMapResult = (Map) abstractMetadataReport.mockingInstance.allMetadataReports
				.get(consumerMetadataIdentifier);
		Assertions.assertEquals(tmpMapResult.get("testPKey"), "9090");
		Assertions.assertEquals(tmpMapResult.get("testKey"), "value");
		Assertions.assertEquals(3, abstractMetadataReport.store.size());

		abstractMetadataReport.store.clear();

		Assertions.assertEquals(0, abstractMetadataReport.store.size());

		abstractMetadataReport.mockingInstance.publishAll();
		Thread.sleep(200);

		Assertions.assertEquals(3, abstractMetadataReport.store.size());

		String v = abstractMetadataReport.store.get(providerMetadataIdentifier1
				.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
		Gson gson = new Gson();
		FullServiceDefinition data = gson.fromJson(v,
				FullServiceDefinition.class);
		checkParam(data.getParameters(), application, version);

		String v2 = abstractMetadataReport.store.get(providerMetadataIdentifier2
				.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
		gson = new Gson();
		data = gson.fromJson(v2, FullServiceDefinition.class);
		checkParam(data.getParameters(), application, version + "_2");

		String v3 = abstractMetadataReport.store.get(consumerMetadataIdentifier
				.getUniqueKey(KeyTypeEnum.UNIQUE_KEY));
		gson = new Gson();
		Map v3Map = gson.fromJson(v3, Map.class);
		checkParam(v3Map, application, version + "_3");
	}

	@Test
	public void testCalculateStartTime() {
		for (int i = 0; i < 300; i++) {
			long t = abstractMetadataReport.mockingInstance.calculateStartTime()
					+ System.currentTimeMillis();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			Assertions.assertTrue(c.get(Calendar.HOUR_OF_DAY) >= 2);
			Assertions.assertTrue(c.get(Calendar.HOUR_OF_DAY) <= 6);
		}
	}

	private FullServiceDefinition toServiceDefinition(String v) {
		Gson gson = new Gson();
		FullServiceDefinition data = gson.fromJson(v,
				FullServiceDefinition.class);
		return data;
	}

	private void checkParam(Map<String, String> map, String application,
			String version) {
		Assertions.assertEquals(map.get("application"), application);
		Assertions.assertEquals(map.get("version"), version);
	}

	private Map<String, String> queryUrlToMap(String urlQuery) {
		if (urlQuery == null) {
			return Collections.emptyMap();
		}
		String[] pairs = urlQuery.split("&");
		Map<String, String> map = new HashMap<>();
		for (String pairStr : pairs) {
			String[] pair = pairStr.split("=");
			map.put(pair[0], pair[1]);
		}
		return map;
	}

	private static class MockNewMetadataReport {
		Map<String, String> store = new ConcurrentHashMap<>();

		public AbstractMetadataReport mockingInstance;

		public MockNewMetadataReport(URL metadataReportURL) {
			initMockingInstance(metadataReportURL);
			mockDoStoreProviderMetadata();
			mockDoStoreConsumerMetadata();
			mockDoSaveMetadata();
			mockDoRemoveMetadata();
			mockGetExportedURLs();
			mockDoSaveSubscriberData();
			mockDoGetSubscribedURLs();
			mockGetServiceDefinition();
		}

		private void initMockingInstance(URL metadataReportURL) {
			this.mockingInstance = Mockito.mock(AbstractMetadataReport.class,
					Mockito.withSettings()
							.defaultAnswer(Mockito.CALLS_REAL_METHODS)
							.useConstructor(metadataReportURL));
		}

		private void mockDoStoreProviderMetadata() {
			Mockito.doAnswer(invocation -> {
				MetadataIdentifier providerMetadataIdentifier = invocation
						.getArgument(0);
				String serviceDefinitions = invocation.getArgument(1);
				store.put(providerMetadataIdentifier.getUniqueKey(
						KeyTypeEnum.UNIQUE_KEY), serviceDefinitions);
				return null;
			}).when(this.mockingInstance).doStoreProviderMetadata(
					Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		}

		private void mockDoStoreConsumerMetadata() {
			Mockito.doAnswer(invocation -> {
				MetadataIdentifier consumerMetadataIdentifier = invocation
						.getArgument(0);
				String serviceParameterString = invocation.getArgument(1);
				store.put(
						consumerMetadataIdentifier
								.getUniqueKey(KeyTypeEnum.UNIQUE_KEY),
						serviceParameterString);
				return null;
			}).when(this.mockingInstance).doStoreConsumerMetadata(
					Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		}

		private void mockDoSaveMetadata() {
			Mockito.doThrow(new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center."))
					.when(this.mockingInstance).doSaveMetadata(
							Mockito.any(ServiceMetadataIdentifier.class),
							Mockito.any(URL.class));
		}

		private void mockDoRemoveMetadata() {
			Mockito.doThrow(new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center."))
					.when(this.mockingInstance).doRemoveMetadata(
							Mockito.any(ServiceMetadataIdentifier.class));
		}

		private void mockGetExportedURLs() {
			Mockito.doThrow(new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center."))
					.when(this.mockingInstance).doGetExportedURLs(
							Mockito.any(ServiceMetadataIdentifier.class));
		}

		private void mockDoSaveSubscriberData() {
			Mockito.doNothing().when(this.mockingInstance).doSaveSubscriberData(
					Mockito.any(SubscriberMetadataIdentifier.class),
					Mockito.anyString());
		}

		private void mockDoGetSubscribedURLs() {
			Mockito.doThrow(new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center."))
					.when(this.mockingInstance).doGetSubscribedURLs(
							Mockito.any(SubscriberMetadataIdentifier.class));
		}

		private void mockGetServiceDefinition() {
			Mockito.doThrow(new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center."))
					.when(this.mockingInstance).getServiceDefinition(
							Mockito.any(MetadataIdentifier.class));
		}

	}

	private static class MockRetryMetadataReport {
		AbstractMetadataReport instance;
		Map<String, String> store = new ConcurrentHashMap<>();
		int needRetryTimes;
		int executeTimes = 0;

		public MockRetryMetadataReport(URL metadataReportURL,
				int needRetryTimes) {
			this.instance = Mockito.mock(AbstractMetadataReport.class,
					Mockito.withSettings()
							.defaultAnswer(Mockito.CALLS_REAL_METHODS)
							.useConstructor(metadataReportURL));
			this.needRetryTimes = needRetryTimes;
			mockDoStoreProviderMetadata();
			mockDoSaveMetadata();
			mockDoRemoveMetadata();
			mockDoGetExportedURLs();
			mockDoSaveSubscriberData();
			mockGetSubscribedURLs();
			mockDoServiceDefinition();
		}

		private void mockDoStoreProviderMetadata() {
			Mockito.doAnswer(invocation -> {
				MetadataIdentifier providerMetadataIdentifier = invocation
						.getArgument(0);
				String serviceDefinitions = invocation.getArgument(1);
				++executeTimes;
				System.out.println("***" + executeTimes + ";"
						+ System.currentTimeMillis());
				if (executeTimes <= needRetryTimes) {
					throw new RuntimeException("must retry:" + executeTimes);
				}
				store.put(providerMetadataIdentifier.getUniqueKey(
						KeyTypeEnum.UNIQUE_KEY), serviceDefinitions);
				return null;
			}).when(this.instance).doStoreProviderMetadata(
					Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		}

		private void mockDoSaveMetadata() {
			Mockito.doAnswer(invocation -> {
				throw new UnsupportedOperationException(
						"This extension does not support working as a remote metadata center.");
			}).when(this.instance).doSaveMetadata(
					Mockito.any(ServiceMetadataIdentifier.class),
					Mockito.any(URL.class));
		}

		private void mockDoRemoveMetadata() {
			Mockito.doAnswer(invocation -> {
				throw new UnsupportedOperationException(
						"This extension does not support working as a remote metadata center.");
			}).when(this.instance).doRemoveMetadata(
					Mockito.any(ServiceMetadataIdentifier.class));
		}

		private void mockDoGetExportedURLs() {
			Mockito.doAnswer(invocation -> {
				throw new UnsupportedOperationException(
						"This extension does not support working as a remote metadata center.");
			}).when(this.instance).doGetExportedURLs(
					Mockito.any(ServiceMetadataIdentifier.class));
		}

		private void mockDoSaveSubscriberData() {
			Mockito.doAnswer(invocation -> {
				return null;
			}).when(this.instance).doSaveSubscriberData(
					Mockito.any(SubscriberMetadataIdentifier.class),
					Mockito.anyString());
		}

		private void mockGetSubscribedURLs() {
			Mockito.doAnswer(invocation -> {
				throw new UnsupportedOperationException(
						"This extension does not support working as a remote metadata center.");
			}).when(this.instance).doGetSubscribedURLs(
					Mockito.any(SubscriberMetadataIdentifier.class));
		}

		private void mockDoServiceDefinition() {
			Mockito.doAnswer(invocation -> {
				throw new UnsupportedOperationException(
						"This extension does not support working as a remote metadata center.");
			}).when(this.instance).getServiceDefinition(
					Mockito.any(MetadataIdentifier.class));
		}

	}

}
