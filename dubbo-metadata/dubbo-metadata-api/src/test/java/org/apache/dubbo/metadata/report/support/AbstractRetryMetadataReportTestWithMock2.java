package org.apache.dubbo.metadata.report.support;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

import java.util.List;
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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractRetryMetadataReportTestWithMock2 {

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
		// Spy abstract class
		AbstractMetadataReport retryReport = Mockito
				.mock(AbstractMetadataReport.class,
						Mockito.withSettings()
								.defaultAnswer(Mockito.CALLS_REAL_METHODS)
								.useConstructor(storeUrl));
		// Consecutive do something
		Mockito.doThrow(new RuntimeException("must retry: 2"),
				new RuntimeException("must retry: 2")).doNothing()
				.when(retryReport)
				.doStoreProviderMetadata(Mockito.any(MetadataIdentifier.class),
						Mockito.anyString());
		retryReport.metadataReportRetry.retryPeriod = 400L;
		URL url = URL.valueOf("dubbo://"
				+ NetUtils.getLocalAddress().getHostName()
				+ ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic");
		Assertions.assertNull(
				retryReport.metadataReportRetry.retryScheduledFuture);
		Assertions.assertEquals(0,
				retryReport.metadataReportRetry.retryCounter.get());
		// Verify methods never been invoked.
		Mockito.verify(retryReport, Mockito.never()).doStoreProviderMetadata(
				Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		// Assertions.assertTrue(retryReport.store.isEmpty());
		Assertions.assertTrue(retryReport.failedReports.isEmpty());
		storePrivider(retryReport, interfaceName, version, group, application);
		Thread.sleep(150);
		// Verify methods been invoked once.
		Mockito.verify(retryReport, Mockito.times(1)).doStoreProviderMetadata(
				Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		// Assertions.assertTrue(retryReport.store.isEmpty());
		Assertions.assertFalse(retryReport.failedReports.isEmpty());
		Assertions.assertNotNull(
				retryReport.metadataReportRetry.retryScheduledFuture);
		Thread.sleep(2000L);
		Assertions.assertTrue(
				retryReport.metadataReportRetry.retryCounter.get() != 0);
		Assertions.assertTrue(
				retryReport.metadataReportRetry.retryCounter.get() >= 3);
		Mockito.verify(retryReport, Mockito.times(3)).doStoreProviderMetadata(
				Mockito.any(MetadataIdentifier.class), Mockito.anyString());
		Assertions.assertTrue(retryReport.failedReports.isEmpty());
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
		// Spy abstract class
		AbstractMetadataReport retryReport = Mockito
				.mock(AbstractMetadataReport.class,
						Mockito.withSettings()
								.defaultAnswer(Mockito.CALLS_REAL_METHODS)
								.useConstructor(storeUrl));
		// Consecutive do something
		Mockito.doThrow(new RuntimeException("must retry: 2"),
				new RuntimeException("must retry: 2")).doNothing()
				.when(retryReport)
				.doStoreProviderMetadata(Mockito.any(MetadataIdentifier.class),
						Mockito.anyString());
		retryReport.metadataReportRetry.retryPeriod = 150L;
		retryReport.metadataReportRetry.retryTimesIfNonFail = 2;

		storePrivider(retryReport, interfaceName, version, group, application);
		Thread.sleep(80);

		Assertions.assertFalse(
				retryReport.metadataReportRetry.retryScheduledFuture
						.isCancelled());
		Assertions.assertFalse(
				retryReport.metadataReportRetry.retryExecutor.isShutdown());
		Thread.sleep(1000L);
		Assertions
				.assertTrue(retryReport.metadataReportRetry.retryScheduledFuture
						.isCancelled());
		Assertions.assertTrue(
				retryReport.metadataReportRetry.retryExecutor.isShutdown());

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

	private static class RetryMetadataReport extends AbstractMetadataReport {

		Map<String, String> store = new ConcurrentHashMap<>();
		int needRetryTimes;
		int executeTimes = 0;

		public RetryMetadataReport(URL metadataReportURL, int needRetryTimes) {
			super(metadataReportURL);
			this.needRetryTimes = needRetryTimes;
		}

		@Override
		protected void doStoreProviderMetadata(
				MetadataIdentifier providerMetadataIdentifier,
				String serviceDefinitions) {
			++executeTimes;
			System.out.println("***Store Provider Metadata " + executeTimes
					+ ";" + System.currentTimeMillis());
			if (executeTimes <= needRetryTimes) {
				throw new RuntimeException("must retry:" + executeTimes);
			}
			store.put(providerMetadataIdentifier
					.getUniqueKey(KeyTypeEnum.UNIQUE_KEY), serviceDefinitions);
		}

		@Override
		protected void doStoreConsumerMetadata(
				MetadataIdentifier consumerMetadataIdentifier,
				String serviceParameterString) {
			++executeTimes;
			System.out.println("***Store Consumer Metadata " + executeTimes
					+ ";" + System.currentTimeMillis());
			if (executeTimes <= needRetryTimes) {
				throw new RuntimeException("must retry:" + executeTimes);
			}
			store.put(consumerMetadataIdentifier.getUniqueKey(
					KeyTypeEnum.UNIQUE_KEY), serviceParameterString);
		}

		@Override
		protected void doSaveMetadata(
				ServiceMetadataIdentifier metadataIdentifier, URL url) {
			throw new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center.");
		}

		@Override
		protected void doRemoveMetadata(
				ServiceMetadataIdentifier metadataIdentifier) {
			throw new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center.");
		}

		@Override
		protected List<String> doGetExportedURLs(
				ServiceMetadataIdentifier metadataIdentifier) {
			throw new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center.");
		}

		@Override
		protected void doSaveSubscriberData(
				SubscriberMetadataIdentifier subscriberMetadataIdentifier,
				String urls) {

		}

		@Override
		protected String doGetSubscribedURLs(
				SubscriberMetadataIdentifier metadataIdentifier) {
			throw new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center.");
		}

		@Override
		public String getServiceDefinition(
				MetadataIdentifier consumerMetadataIdentifier) {
			throw new UnsupportedOperationException(
					"This extension does not support working as a remote metadata center.");
		}

	}
}
