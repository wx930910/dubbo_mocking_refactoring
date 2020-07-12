package org.apache.dubbo.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GenericEventListenerTestByMock {
	private EventDispatcher eventDispatcher;

	private MockGenericEventListener listener;

	@BeforeEach
	public void init() {
		this.listener = new MockGenericEventListener();
		this.eventDispatcher = EventDispatcher.getDefaultExtension();
		this.eventDispatcher.addEventListener(listener.instance);
	}

	@AfterEach
	public void destroy() {
		this.eventDispatcher.removeAllEventListeners();
	}

	@Test
	public void testOnEvent() {
		String value = "Hello,World";
		EchoEvent echoEvent = new EchoEvent(value);
		eventDispatcher.dispatch(echoEvent);
		assertEquals(echoEvent, listener.getEchoEvent());
		assertEquals(value, listener.getEchoEvent().getSource());
	}

	class MockGenericEventListener {

		private GenericEventListener instance;
		private EchoEvent echoEvent;

		public MockGenericEventListener() {
			this.instance = Mockito.mock(GenericEventListener.class,
					Mockito.CALLS_REAL_METHODS);
			Mockito.doAnswer(invocation -> {
				this.echoEvent = invocation.getArgument(0);
				return null;
			}).when(this.instance).onEvent(Mockito.any(EchoEvent.class));
		}

		public void onEvent(EchoEvent echoEvent) {
			this.echoEvent = echoEvent;
		}

		public void event(EchoEvent echoEvent) {
			assertEquals("Hello,World", echoEvent.getSource());
		}

		public void event(EchoEvent echoEvent, Object arg) {
			this.echoEvent = echoEvent;
		}

		public EchoEvent getEchoEvent() {
			return echoEvent;
		}
	}
}
