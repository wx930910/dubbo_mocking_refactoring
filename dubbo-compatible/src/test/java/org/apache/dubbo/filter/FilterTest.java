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

package org.apache.dubbo.filter;

import static org.apache.dubbo.common.constants.CommonConstants.DUBBO_VERSION_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;
import static org.apache.dubbo.rpc.Constants.TOKEN_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.rpc.RpcException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;

public class FilterTest {

	static Filter myFilter;
	static Integer count = 0;

	static {
		myFilter = Mockito.mock(Filter.class);
		Mockito.when(myFilter.invoke(Mockito.any(Invoker.class), Mockito.any(Invocation.class))).thenAnswer(invo -> {
			Invoker<?> invoker = invo.getArgument(0);
			Invocation invocation = invo.getArgument(1);
			count++;

			if (invocation.getArguments()[0].equals("aa")) {
				throw new RpcException(new IllegalArgumentException("arg0 illegal"));
			}

			Result tmp = invoker.invoke(invocation);
			return tmp;
		});
	}

	@Test
	public void testInvokeException() {
		Invocation invocation = Mockito.mock(Invocation.class);
		String arg0 = "aa";
		Mockito.when(invocation.getTargetServiceUniqueName()).thenReturn(null);
		Mockito.when(invocation.getMethodName()).thenReturn("echo");
		Mockito.when(invocation.getParameterTypes()).thenReturn(new Class[] { String.class });
		Mockito.when(invocation.getArguments()).thenReturn(new Object[] { arg0 });
		Mockito.when(invocation.getAttachments()).thenAnswer(invo -> {
			Map<String, String> attachments = new HashMap<String, String>();
			attachments.put(PATH_KEY, "dubbo");
			attachments.put(GROUP_KEY, "dubbo");
			attachments.put(VERSION_KEY, "1.0.0");
			attachments.put(DUBBO_VERSION_KEY, "1.0.0");
			attachments.put(TOKEN_KEY, "sfag");
			attachments.put(TIMEOUT_KEY, "1000");
			return attachments;
		});
		Mockito.when(invocation.getInvoker()).thenReturn(null);
		Mockito.when(invocation.put(Mockito.any(), Mockito.any())).thenReturn(null);
		Mockito.when(invocation.get(Mockito.any())).thenReturn(null);
		Mockito.when(invocation.getAttributes()).thenReturn(null);
		Mockito.when(invocation.getAttachment(Mockito.anyString())).thenAnswer(invo -> {
			return invocation.getAttachments().get(invo.getArgument(0));
		});
		Mockito.when(invocation.getAttachment(Mockito.anyString(), Mockito.anyString())).thenAnswer(invo -> {
			String key = invo.getArgument(0);
			return invocation.getAttachments().get(key);
		});
		try {
			Invoker<FilterTest> invoker = new LegacyInvoker<FilterTest>(null);
			// Invocation invocation = new LegacyInvocation("aa");
			myFilter.invoke(invoker, invocation);
			fail();
		} catch (RpcException e) {
			Assertions.assertTrue(e.getMessage().contains("arg0 illegal"));
		}
	}

	@Test
	public void testDefault() {
		Invoker<FilterTest> invoker = new LegacyInvoker<FilterTest>(null);
		Invocation invocation = new LegacyInvocation("bbb");
		Result res = myFilter.invoke(invoker, invocation);
		System.out.println(res);
	}

	@AfterAll
	public static void tear() {
		Assertions.assertEquals(2, count);
	}
}
