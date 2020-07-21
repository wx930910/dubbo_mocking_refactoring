package org.apache.dubbo.remoting;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_TIMEOUT;
import static org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.remoting.exchange.support.ExchangeHandlerAdapter;
import org.junit.jupiter.api.Test;

public class ChanelHandlerTestWithMock2 {
	private static final Logger logger = LoggerFactory
			.getLogger(ChanelHandlerTest.class);

	public static ExchangeClient initClient(String url) {
		// Create client and build connection
		ExchangeClient exchangeClient = null;
		PeformanceTestHandler handler = new PeformanceTestHandler(url);
		boolean run = true;
		while (run) {
			try {

				exchangeClient = Exchangers.connect(url, handler);
			} catch (Throwable t) {

				if (t != null && t.getCause() != null
						&& t.getCause().getClass() != null
						&& (t.getCause()
								.getClass() == java.net.ConnectException.class
								|| t.getCause()
										.getClass() == java.net.ConnectException.class)) {

				} else {
					t.printStackTrace();
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (exchangeClient != null) {
				run = false;
			}
		}
		return exchangeClient;
	}

	public static void closeClient(ExchangeClient client) {
		if (client.isConnected()) {
			client.close();
		}
	}

	@Test
	public void testClient() throws Throwable {
		// read server info from property
		if (PerformanceUtils.getProperty("server", null) == null) {
			logger.warn("Please set -Dserver=127.0.0.1:9911");
			return;
		}
		final String server = System.getProperty("server", "127.0.0.1:9911");
		final String transporter = PerformanceUtils.getProperty(
				Constants.TRANSPORTER_KEY, Constants.DEFAULT_TRANSPORTER);
		final String serialization = PerformanceUtils.getProperty(
				Constants.SERIALIZATION_KEY,
				Constants.DEFAULT_REMOTING_SERIALIZATION);
		final int timeout = PerformanceUtils.getIntProperty(TIMEOUT_KEY,
				DEFAULT_TIMEOUT);
		int sleep = PerformanceUtils.getIntProperty("sleep", 60 * 1000 * 60);

		final String url = "exchange://" + server + "?transporter="
				+ transporter + "&serialization=" + serialization + "&timeout="
				+ timeout;

		ExchangeClient exchangeClient = initClient(url);

		Thread.sleep(sleep);
		closeClient(exchangeClient);
	}

	static class PeformanceTestHandler extends ExchangeHandlerAdapter {
		String url = "";

		/**
		 * @param url
		 */
		public PeformanceTestHandler(String url) {
			this.url = url;
		}

		@Override
		public void connected(Channel channel) throws RemotingException {
			System.out.println("connected event,chanel;" + channel);
		}

		@Override
		public void disconnected(Channel channel) throws RemotingException {
			System.out.println("disconnected event,chanel;" + channel);
			initClient(url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.dubbo.remoting.transport.support.ChannelHandlerAdapter#
		 * caught(org.apache.dubbo.remoting.Channel, java.lang.Throwable)
		 */
		@Override
		public void caught(Channel channel, Throwable exception)
				throws RemotingException {
			// System.out.println("caught event:"+exception);
		}

	}
}
