package org.apache.dubbo.remoting.codec;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.mockito.Mockito;

public class MockAbstractMockChannel {
	public Channel instance;

	public static final String LOCAL_ADDRESS = "local";
	public static final String REMOTE_ADDRESS = "remote";
	public static final String ERROR_WHEN_SEND = "error_when_send";
	InetSocketAddress localAddress;
	InetSocketAddress remoteAddress;
	private URL remoteUrl;
	private ChannelHandler handler;
	private boolean isClosed;
	private volatile boolean closing;
	private Map<String, Object> attributes = new HashMap<String, Object>(1);
	private volatile Object receivedMessage = null;

	public MockAbstractMockChannel() {
		this.instance = Mockito.mock(Channel.class);
		mockGetUrl();
		mockGetChannelHandler();
		mockGetLocalAddress();
		try {
			mockSend();
		} catch (RemotingException e) {
			e.printStackTrace();
		}
		mockClose();
		mockStartClose();
		mockIsClosed();
		mockGetRemoteAddress();
		mockIsConnected();
		mockRest();
	}

	public MockAbstractMockChannel(URL remoteUrl) {
		this.instance = Mockito.mock(Channel.class);
		this.remoteUrl = remoteUrl;
		this.remoteAddress = NetUtils
				.toAddress(remoteUrl.getParameter(REMOTE_ADDRESS));
		this.localAddress = NetUtils
				.toAddress(remoteUrl.getParameter(LOCAL_ADDRESS));
	}

	public MockAbstractMockChannel(ChannelHandler handler) {
		this.instance = Mockito.mock(Channel.class);
		this.handler = handler;
	}

	public Object getReceivedMessage() {
		return receivedMessage;
	}

	private void mockGetUrl() {
		Mockito.doReturn(this.remoteUrl).when(this.instance).getUrl();
	}

	private void mockGetChannelHandler() {
		Mockito.doReturn(this.handler).when(this.instance).getChannelHandler();
	}

	private void mockGetLocalAddress() {
		Mockito.doReturn(this.localAddress).when(this.instance)
				.getLocalAddress();
	}

	private void mockSend() throws RemotingException {
		Mockito.doAnswer(invocation -> {
			Object message = invocation.getArgument(0);
			if (remoteUrl.getParameter(ERROR_WHEN_SEND, Boolean.FALSE)) {
				receivedMessage = null;
				throw new RemotingException(localAddress, remoteAddress,
						"mock error");
			} else {
				receivedMessage = message;
			}
			return null;
		}).when(this.instance).send(Mockito.any(Object.class));
		Mockito.doAnswer(invocation -> {
			Object message = invocation.getArgument(0);
			this.instance.send(message);
			return null;
		}).when(this.instance).send(Mockito.any(Object.class),
				Mockito.anyBoolean());
	}

	private void mockClose() {
		Mockito.doAnswer(invocation -> {
			this.instance.close(0);
			return null;
		}).when(this.instance).close();
		Mockito.doAnswer(invocation -> {
			this.isClosed = true;
			return null;
		}).when(this.instance).close(Mockito.anyInt());
	}

	private void mockStartClose() {
		Mockito.doAnswer(invocation -> {
			this.closing = true;
			return null;
		}).when(this.instance).startClose();
	}

	private void mockIsClosed() {
		Mockito.doReturn(this.isClosed).when(this.instance).isClosed();
	}

	private void mockGetRemoteAddress() {
		Mockito.doReturn(this.remoteAddress).when(this.instance)
				.getRemoteAddress();
	}

	private void mockIsConnected() {
		Mockito.doReturn(this.isClosed).when(this.instance).isConnected();
	}

	private void mockRest() {
		Mockito.doAnswer(invocation -> this.attributes
				.containsKey(invocation.getArgument(0))).when(this.instance)
				.hasAttribute(Mockito.anyString());
		Mockito.doAnswer(
				invocation -> this.attributes.get(invocation.getArgument(0)))
				.when(this.instance).getAttribute(Mockito.anyString());
		Mockito.doAnswer(invocation -> {
			this.attributes.put(invocation.getArgument(0),
					invocation.getArgument(1));
			return null;
		}).when(this.instance).setAttribute(Mockito.anyString(),
				Mockito.any(Object.class));
		Mockito.doAnswer(invocation -> {
			this.attributes.remove(invocation.getArgument(0));
			return null;
		}).when(this.instance).removeAttribute(Mockito.anyString());
	}

}
