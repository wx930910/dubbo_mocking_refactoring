package org.apache.dubbo.config;

import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.api.Greeting;
import org.apache.dubbo.config.support.Parameter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AbstractConfigTestWithMock {

	@Test
	@Config(interfaceClass = Greeting.class, filter = { "f1, f2" }, listener = {
			"l1, l2" }, parameters = { "k1", "v1", "k2", "v2" })
	public void appendAnnotation() throws Exception {
		Config config = getClass().getMethod("appendAnnotation")
				.getAnnotation(Config.class);
		MockAnnotationConfig annotationConfig = new MockAnnotationConfig();
		annotationConfig.instance.appendAnnotation(Config.class, config);
		Assertions.assertSame(Greeting.class, annotationConfig.getInterface());
		Assertions.assertEquals("f1, f2", annotationConfig.getFilter());
		Assertions.assertEquals("l1, l2", annotationConfig.getListener());
		Assertions.assertEquals(2, annotationConfig.getParameters().size());
		Assertions.assertEquals("v1",
				annotationConfig.getParameters().get("k1"));
		Assertions.assertEquals("v2",
				annotationConfig.getParameters().get("k2"));
		assertThat(annotationConfig.toString(),
				Matchers.containsString("filter=\"f1, f2\" "));
		assertThat(annotationConfig.toString(),
				Matchers.containsString("listener=\"l1, l2\" "));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.ANNOTATION_TYPE })
	public @interface ConfigField {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.METHOD,
			ElementType.ANNOTATION_TYPE })
	public @interface Config {
		Class<?> interfaceClass() default void.class;

		String interfaceName() default "";

		String[] filter() default {};

		String[] listener() default {};

		String[] parameters() default {};

		ConfigField[] configFields() default {};

		ConfigField configField() default @ConfigField;
	}

	private static class OverrideConfig extends AbstractInterfaceConfig {
		public String address;
		public String protocol;
		public String exclude;
		public String key;
		public String useKeyAsProperty;
		public String escape;
		public String notConflictKey;
		public String notConflictKey2;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		@Parameter(excluded = true)
		public String getExclude() {
			return exclude;
		}

		public void setExclude(String exclude) {
			this.exclude = exclude;
		}

		@Parameter(key = "key1", useKeyAsProperty = false)
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@Parameter(key = "key2", useKeyAsProperty = true)
		public String getUseKeyAsProperty() {
			return useKeyAsProperty;
		}

		public void setUseKeyAsProperty(String useKeyAsProperty) {
			this.useKeyAsProperty = useKeyAsProperty;
		}

		@Parameter(escaped = true)
		public String getEscape() {
			return escape;
		}

		public void setEscape(String escape) {
			this.escape = escape;
		}

		public String getNotConflictKey() {
			return notConflictKey;
		}

		public void setNotConflictKey(String notConflictKey) {
			this.notConflictKey = notConflictKey;
		}

		public String getNotConflictKey2() {
			return notConflictKey2;
		}

		public void setNotConflictKey2(String notConflictKey2) {
			this.notConflictKey2 = notConflictKey2;
		}
	}

	private static class PropertiesConfig extends AbstractConfig {
		private char c;
		private boolean bool;
		private byte b;
		private int i;
		private long l;
		private float f;
		private double d;
		private short s;
		private String str;

		PropertiesConfig() {
		}

		PropertiesConfig(String id) {
			this.id = id;
		}

		public char getC() {
			return c;
		}

		public void setC(char c) {
			this.c = c;
		}

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

		public byte getB() {
			return b;
		}

		public void setB(byte b) {
			this.b = b;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public long getL() {
			return l;
		}

		public void setL(long l) {
			this.l = l;
		}

		public float getF() {
			return f;
		}

		public void setF(float f) {
			this.f = f;
		}

		public double getD() {
			return d;
		}

		public void setD(double d) {
			this.d = d;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public short getS() {
			return s;
		}

		public void setS(short s) {
			this.s = s;
		}
	}

	private static class ParameterConfig {
		private int number;
		private String name;
		private int age;
		private String secret;

		ParameterConfig() {
		}

		ParameterConfig(int number, String name, int age, String secret) {
			this.number = number;
			this.name = name;
			this.age = age;
			this.secret = secret;
		}

		@Parameter(key = "num", append = true)
		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		@Parameter(key = "naming", append = true, escaped = true, required = true)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		@Parameter(excluded = true)
		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public Map getParameters() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("key.1", "one");
			map.put("key-2", "two");
			return map;
		}
	}

	private static class AttributeConfig {
		private char letter;
		private boolean activate;
		private byte flag;

		public AttributeConfig(char letter, boolean activate, byte flag) {
			this.letter = letter;
			this.activate = activate;
			this.flag = flag;
		}

		@Parameter(attribute = true, key = "let")
		public char getLetter() {
			return letter;
		}

		public void setLetter(char letter) {
			this.letter = letter;
		}

		@Parameter(attribute = true)
		public boolean isActivate() {
			return activate;
		}

		public void setActivate(boolean activate) {
			this.activate = activate;
		}

		public byte getFlag() {
			return flag;
		}

		public void setFlag(byte flag) {
			this.flag = flag;
		}
	}

	private static class MockAnnotationConfig {
		AbstractConfig instance;

		private Class interfaceClass;
		private String filter;
		private String listener;
		private Map<String, String> parameters;
		private String[] configFields;

		public MockAnnotationConfig() {
			this.instance = Mockito.mock(AbstractConfig.class,
					Mockito.CALLS_REAL_METHODS);
		}

		public Class getInterface() {
			return interfaceClass;
		}

		public void setInterface(Class interfaceName) {
			this.interfaceClass = interfaceName;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getListener() {
			return listener;
		}

		public void setListener(String listener) {
			this.listener = listener;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public void setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
		}

		public String[] getConfigFields() {
			return configFields;
		}

		public void setConfigFields(String[] configFields) {
			this.configFields = configFields;
		}
	}

	private static class AnnotationConfig extends AbstractConfig {
		private Class interfaceClass;
		private String filter;
		private String listener;
		private Map<String, String> parameters;
		private String[] configFields;

		public Class getInterface() {
			return interfaceClass;
		}

		public void setInterface(Class interfaceName) {
			this.interfaceClass = interfaceName;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getListener() {
			return listener;
		}

		public void setListener(String listener) {
			this.listener = listener;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public void setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
		}

		public String[] getConfigFields() {
			return configFields;
		}

		public void setConfigFields(String[] configFields) {
			this.configFields = configFields;
		}
	}

	protected static void setOsEnv(Map<String, String> newenv)
			throws Exception {
		try {
			Class<?> processEnvironmentClass = Class
					.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass
					.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField
					.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField
					.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String, String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap"
						.equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}
	}
}
