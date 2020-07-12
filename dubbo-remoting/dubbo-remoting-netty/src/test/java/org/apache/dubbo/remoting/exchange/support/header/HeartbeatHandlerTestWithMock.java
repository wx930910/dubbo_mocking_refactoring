package org.apache.dubbo.remoting.exchange.support.header;

import java.util.concurrent.CompletableFuture;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.remoting.transport.dispatcher.FakeChannelHandlers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HeartbeatHandlerTestWithMock {
	private static final Logger logger = LoggerFactory
			.getLogger(HeartbeatHandlerTest.class);

	private ExchangeServer server;
	private ExchangeClient client;

	@AfterEach
	public void after() throws Exception {
		if (client != null) {
			client.close();
			client = null;
		}

		if (server != null) {
			server.close();
			server = null;
		}

		// wait for timer to finish
		Thread.sleep(2000);
	}

	@Test
	public void testServerHeartbeat() throws Exception {
		URL serverURL = URL
				.valueOf("header://localhost:55555?transporter=netty3");
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		TestHeartbeatHandler handler = new TestHeartbeatHandler();
		server = Exchangers.bind(serverURL, handler.instance);
		System.out.println("Server bind successfully");

		FakeChannelHandlers.setTestingChannelHandlers();
		serverURL = serverURL.removeParameter(Constants.HEARTBEAT_KEY);

		// Let the client not reply to the heartbeat, and turn off automatic
		// reconnect to simulate the client dropped.
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 600 * 1000);
		serverURL = serverURL.addParameter(Constants.RECONNECT_KEY, false);

		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		Assertions.assertTrue(handler.disconnectCount > 0);
		System.out.println("disconnect count " + handler.disconnectCount);
	}

	@Test
	public void testHeartbeat() throws Exception {
		URL serverURL = URL
				.valueOf("header://localhost:55556?transporter=netty3");
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		TestHeartbeatHandler handler = new TestHeartbeatHandler();
		server = Exchangers.bind(serverURL, handler.instance);
		System.out.println("Server bind successfully");

		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		System.err.println(
				"++++++++++++++ disconnect count " + handler.disconnectCount);
		System.err.println(
				"++++++++++++++ connect count " + handler.connectCount);
		Assertions.assertEquals(0, handler.disconnectCount);
		Assertions.assertEquals(1, handler.connectCount);
	}

	@Test
	public void testClientHeartbeat() throws Exception {
		FakeChannelHandlers.setTestingChannelHandlers();
		URL serverURL = URL
				.valueOf("header://localhost:55557?transporter=netty3");
		TestHeartbeatHandler handler = new TestHeartbeatHandler();
		server = Exchangers.bind(serverURL, handler.instance);
		System.out.println("Server bind successfully");

		FakeChannelHandlers.resetChannelHandlers();
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		Assertions.assertTrue(handler.connectCount > 0);
		System.out.println("connect count " + handler.connectCount);
	}

	class TestHeartbeatHandler {

		public int disconnectCount = 0;
		public int connectCount = 0;

		public ExchangeHandler instance;

		public TestHeartbeatHandler() {
			this.instance = Mockito.mock(ExchangeHandler.class);
			try {
				Mockito.doAnswer(invocation -> {
					Object request = invocation.getArgument(0);
					return CompletableFuture.completedFuture(request);
				}).when(this.instance).reply(Mockito.any(ExchangeChannel.class),
						Mockito.any());
				Mockito.doAnswer(invocation -> {
					++connectCount;
					return null;
				}).when(this.instance).connected(Mockito.any(Channel.class));
				Mockito.doAnswer(invocation -> {
					++disconnectCount;
					return null;
				}).when(this.instance).disconnected(Mockito.any(Channel.class));
				Mockito.doAnswer(invocation -> {
					Object message = invocation.getArgument(1);
					logger.error(this.getClass().getSimpleName()
							+ message.toString());
					return null;
				}).when(this.instance).received(Mockito.any(Channel.class),
						Mockito.any());
				Mockito.doAnswer(invocation -> {
					Throwable exception = invocation.getArgument(1);
					exception.printStackTrace();
					return null;
				}).when(this.instance).caught(Mockito.any(Channel.class),
						Mockito.any(Throwable.class));
				Mockito.doAnswer(invocation -> {
					return invocation.getArgument(1);
				}).when(this.instance).telnet(Mockito.any(Channel.class),
						Mockito.anyString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
