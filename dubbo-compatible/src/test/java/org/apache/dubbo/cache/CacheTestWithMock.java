package org.apache.dubbo.cache;

import org.apache.dubbo.rpc.RpcInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alibaba.dubbo.cache.Cache;
import com.alibaba.dubbo.cache.CacheFactory;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;

public class CacheTestWithMock {
	@Test
	public void testCacheFactory() {
		URL url = URL.valueOf("test://test:11/test?cache=jacache&.cache.write.expire=1");
		CacheFactory cacheFactory = new MyCacheFactory();
		Invocation invocation = Mockito.mock(Invocation.class);
		// Mockito.when(invocation.getParameterTypes()).thenReturn(new
		// Class[0]);
		// Mockito.when(invocation.getArguments()).thenReturn(new Object[0]);

		Cache cache = cacheFactory.getCache(url, invocation);
		cache.put("testKey", "testValue");

		org.apache.dubbo.cache.CacheFactory factory = cacheFactory;
		org.apache.dubbo.common.URL u = org.apache.dubbo.common.URL
				.valueOf("test://test:11/test?cache=jacache&.cache.write.expire=1");
		org.apache.dubbo.rpc.Invocation inv = new RpcInvocation();
		org.apache.dubbo.cache.Cache c = factory.getCache(u, inv);
		String v = (String) c.get("testKey");
		Assertions.assertEquals("testValue", v);
	}
}
