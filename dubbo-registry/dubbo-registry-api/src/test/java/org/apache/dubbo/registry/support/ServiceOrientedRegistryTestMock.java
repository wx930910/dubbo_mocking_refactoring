package org.apache.dubbo.registry.support;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSortedSet;
import static org.apache.dubbo.common.URL.valueOf;
import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_PROTOCOL;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;
import static org.apache.dubbo.common.constants.RegistryConstants.REGISTRY_TYPE_KEY;
import static org.apache.dubbo.common.constants.RegistryConstants.SERVICE_REGISTRY_TYPE;
import static org.apache.dubbo.common.constants.RegistryConstants.SUBSCRIBED_SERVICE_NAMES_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.metadata.WritableMetadataService;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.client.ServiceDiscoveryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServiceOrientedRegistryTestMock {
	private static final URL registryURL = valueOf("in-memory://localhost:12345")
			.addParameter(REGISTRY_TYPE_KEY, SERVICE_REGISTRY_TYPE)
			.addParameter(SUBSCRIBED_SERVICE_NAMES_KEY, "a, b , c,d,e ,");

	private static final String SERVICE_INTERFACE = "org.apache.dubbo.metadata.MetadataService";

	private static final String GROUP = "dubbo-provider";

	private static final String VERSION = "1.0.0";

	private static URL url = valueOf("dubbo://192.168.0.102:20880/" + SERVICE_INTERFACE + "?&application=" + GROUP
			+ "&interface=" + SERVICE_INTERFACE + "&group=" + GROUP + "&version=" + VERSION
			+ "&methods=getAllServiceKeys,getServiceRestMetadata,getExportedURLs,getAllExportedURLs" + "&side="
			+ PROVIDER_SIDE);

	private static URL url2 = url.setProtocol("rest");

	private WritableMetadataService metadataService;

	private ServiceDiscoveryRegistry registry;

	private NotifyListener notifyListener;
	private List<URL> cache = new LinkedList<>();

	@BeforeEach
	public void init() {
		registry = ServiceDiscoveryRegistry.create(registryURL);
		metadataService = WritableMetadataService.getDefaultExtension();
		notifyListener = Mockito.mock(NotifyListener.class);
		Mockito.doAnswer(invo -> {
			List<URL> urls = invo.getArgument(0);
			this.cache.addAll(urls);
			return null;
		}).when(this.notifyListener).notify(Mockito.anyList());
	}

	@Test
	public void testSupports() {
		assertTrue(ServiceDiscoveryRegistry.supports(registryURL));
	}

	@Test
	public void testCreate() {
		assertNotNull(registry);
	}

	@Test
	public void testRegister() {

		registry.register(url);

		SortedSet<String> urls = metadataService.getExportedURLs();

		assertTrue(urls.isEmpty());
		assertEquals(toSortedSet(), metadataService.getExportedURLs(SERVICE_INTERFACE));
		assertEquals(toSortedSet(), metadataService.getExportedURLs(SERVICE_INTERFACE, GROUP));

		String serviceInterface = "com.acme.UserService";

		URL newURL = url.setServiceInterface(serviceInterface).setPath(serviceInterface);

		registry.register(newURL);

		urls = metadataService.getExportedURLs();

		assertEquals(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION), toSortedSet(urls.first()));
		assertEquals(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION, DEFAULT_PROTOCOL),
				toSortedSet(urls.first()));

	}

	@Test
	public void testUnregister() {

		String serviceInterface = "com.acme.UserService";

		URL newURL = url.setServiceInterface(serviceInterface).setPath(serviceInterface);

		// register
		registry.register(newURL);

		SortedSet<String> urls = metadataService.getExportedURLs();

		assertEquals(1, urls.size());
		assertTrue(urls.iterator().next().contains(serviceInterface));
		assertEquals(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION), urls);
		assertEquals(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION, DEFAULT_PROTOCOL), urls);

		// unregister
		registry.unregister(newURL);

		urls = metadataService.getExportedURLs();

		assertEquals(toSortedSet(), urls);
		assertTrue(CollectionUtils.isEmpty(metadataService.getExportedURLs(serviceInterface)));
		assertTrue(CollectionUtils.isEmpty(metadataService.getExportedURLs(serviceInterface, GROUP)));
		assertTrue(CollectionUtils.isEmpty(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION)));
		assertTrue(CollectionUtils
				.isEmpty(metadataService.getExportedURLs(serviceInterface, GROUP, VERSION, DEFAULT_PROTOCOL)));
	}

	@Test
	public void testSubscribe() {
		NotifyListener testSubscribeNotifyListener = Mockito.mock(NotifyListener.class);
		List<URL> testSubscribeCache = new LinkedList<>();
		Mockito.doAnswer(invo -> {
			List<URL> urls = invo.getArgument(0);
			testSubscribeCache.addAll(urls);
			return null;
		}).when(testSubscribeNotifyListener).notify(Mockito.anyList());
		registry.subscribe(url, testSubscribeNotifyListener);

		SortedSet<String> urls = metadataService.getSubscribedURLs();

		assertTrue(urls.isEmpty());

	}

	private static <T extends Comparable<T>> SortedSet<T> toSortedSet(T... values) {
		return unmodifiableSortedSet(new TreeSet<>(asList(values)));
	}
}
