package org.apache.dubbo.remoting.exchange.support.header;

import java.util.concurrent.CompletableFuture;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.remoting.transport.dispatcher.FakeChannelHandlers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HeartbeatHandlerTestWithMock2 {
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandlerTest.class);

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
		URL serverURL = URL.valueOf("header://localhost:55555?transporter=netty3");
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		// Mock interface
		ExchangeHandler handler = Mockito.mock(ExchangeHandler.class);

		server = Exchangers.bind(serverURL, handler);
		System.out.println("testServerHeartbeat Server bind successfully");

		FakeChannelHandlers.setTestingChannelHandlers();
		serverURL = serverURL.removeParameter(Constants.HEARTBEAT_KEY);

		// Let the client not reply to the heartbeat, and turn off automatic
		// reconnect to simulate the client dropped.
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 600 * 1000);
		serverURL = serverURL.addParameter(Constants.RECONNECT_KEY, false);

		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		// Verify disconnect been invoked only once.
		Mockito.verify(handler, Mockito.times(1)).disconnected(Mockito.any(Channel.class));
		// Assertions.assertTrue(handler.disconnectCount > 0);
		System.out.println("disconnect count " + 1);
	}

	@Test
	public void testHeartbeat() throws Exception {
		URL serverURL = URL.valueOf("header://localhost:55556?transporter=netty3");
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		// Mock interface
		ExchangeHandler handler = Mockito.mock(ExchangeHandler.class);
		server = Exchangers.bind(serverURL, handler);
		System.out.println("testHeartbeat Server bind successfully");

		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		// System.err.println(
		// "++++++++++++++ disconnect count " + handler.disconnectCount);
		// System.err.println(
		// "++++++++++++++ connect count " + handler.connectCount);
		// Verify haven't invoke disconnected and invoked connected once.
		Mockito.verify(handler, Mockito.never()).disconnected(Mockito.any(Channel.class));
		Mockito.verify(handler, Mockito.times(1)).connected(Mockito.any(Channel.class));
		// Assertions.assertEquals(0, handler.disconnectCount);
		// Assertions.assertEquals(1, handler.connectCount);
	}

	/**
	 * Test target is #Exchangers#connect() It is unit test since it only focus
	 * on testing #Exchangers class interaction. But mean time it has too many
	 * interactions between production files. (FakeChannelHandlers,
	 * ExchangeServer, ExchangeClient)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testClientHeartbeat() throws Exception {
		FakeChannelHandlers.setTestingChannelHandlers();
		URL serverURL = URL.valueOf("header://localhost:55557?transporter=netty3");
		// Mock interface
		ExchangeHandler handler = Mockito.mock(ExchangeHandler.class);
		server = Exchangers.bind(serverURL, handler);
		System.out.println("testClientHeartbeat Server bind successfully");

		FakeChannelHandlers.resetChannelHandlers();
		serverURL = serverURL.addParameter(Constants.HEARTBEAT_KEY, 1000);
		client = Exchangers.connect(serverURL);
		Thread.sleep(10000);
		// Verify disconnect been invoked only once.
		Mockito.verify(handler, Mockito.times(1)).connected(Mockito.any(Channel.class));
		// Assertions.assertTrue(handler.connectCount > 0);
		System.out.println("connect count " + 1);
	}

	class TestHeartbeatHandler implements ExchangeHandler {

		public int disconnectCount = 0;
		public int connectCount = 0;

		public CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException {
			return CompletableFuture.completedFuture(request);
		}

		@Override
		public void connected(Channel channel) throws RemotingException {
			System.out.println("Connecting");
			++connectCount;
		}

		@Override
		public void disconnected(Channel channel) throws RemotingException {
			System.out.println("Disconnecting");
			++disconnectCount;
		}

		@Override
		public void sent(Channel channel, Object message) throws RemotingException {

		}

		@Override
		public void received(Channel channel, Object message) throws RemotingException {
			logger.error(this.getClass().getSimpleName() + message.toString());
		}

		@Override
		public void caught(Channel channel, Throwable exception) throws RemotingException {
			exception.printStackTrace();
		}

		public String telnet(Channel channel, String message) throws RemotingException {
			return message;
		}
	}
}
