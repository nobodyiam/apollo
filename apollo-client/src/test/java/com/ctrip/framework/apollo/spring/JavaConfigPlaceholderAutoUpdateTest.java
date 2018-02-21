package com.ctrip.framework.apollo.spring;

import static org.junit.Assert.assertEquals;

import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.internals.SimpleConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.util.ConfigUtil;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

public class JavaConfigPlaceholderAutoUpdateTest extends AbstractSpringIntegrationTest {

  private static final String TIMEOUT_PROPERTY = "timeout";
  private static final int DEFAULT_TIMEOUT = 100;
  private static final String BATCH_PROPERTY = "batch";
  private static final int DEFAULT_BATCH = 200;
  private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

  @Test
  public void testAutoUpdateWithOneNamespace() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(newBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateDisabled() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    MockConfigUtil mockConfigUtil = new MockConfigUtil();
    mockConfigUtil.setAutoUpdateInjectedSpringProperties(false);

    MockInjector.setInstance(ConfigUtil.class, mockConfigUtil);

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithMultipleNamespaces() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties applicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(initialTimeout));
    Properties fxApolloProperties = assembleProperties(BATCH_PROPERTY,
        String.valueOf(initialBatch));

    SimpleConfig applicationConfig = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION,
        applicationProperties);
    SimpleConfig fxApolloConfig = prepareConfig(FX_APOLLO_NAMESPACE, fxApolloProperties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig2.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newApplicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(newTimeout));

    applicationConfig
        .onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newApplicationProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newFxApolloProperties = assembleProperties(BATCH_PROPERTY, String.valueOf(newBatch));

    fxApolloConfig.onRepositoryChange(FX_APOLLO_NAMESPACE, newFxApolloProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(newBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithMultipleNamespacesWithSameProperties() throws Exception {
    int someTimeout = 1000;
    int someBatch = 2000;
    int anotherBatch = 3000;
    int someNewTimeout = 1001;
    int someNewBatch = 2001;

    Properties applicationProperties = assembleProperties(BATCH_PROPERTY,
        String.valueOf(someBatch));
    Properties fxApolloProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(someTimeout), BATCH_PROPERTY, String.valueOf(anotherBatch));

    SimpleConfig applicationConfig = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION,
        applicationProperties);
    SimpleConfig fxApolloConfig = prepareConfig(FX_APOLLO_NAMESPACE, fxApolloProperties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig2.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(someTimeout, bean.getTimeout());
    assertEquals(someBatch, bean.getBatch());

    Properties newFxApolloProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(someNewTimeout), BATCH_PROPERTY, String.valueOf(someNewBatch));

    fxApolloConfig.onRepositoryChange(FX_APOLLO_NAMESPACE, newFxApolloProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(someNewTimeout, bean.getTimeout());
    assertEquals(someBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithNewProperties() throws Exception {
    int initialTimeout = 1000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties applicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(initialTimeout));

    SimpleConfig applicationConfig = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION,
        applicationProperties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(DEFAULT_BATCH, bean.getBatch());

    Properties newApplicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(newTimeout), BATCH_PROPERTY, String.valueOf(newBatch));

    applicationConfig
        .onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newApplicationProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(newBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithIrrelevantProperties() throws Exception {
    int initialTimeout = 1000;

    String someIrrelevantKey = "someIrrelevantKey";
    String someIrrelevantValue = "someIrrelevantValue";

    String anotherIrrelevantKey = "anotherIrrelevantKey";
    String anotherIrrelevantValue = "anotherIrrelevantValue";

    Properties applicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(initialTimeout), someIrrelevantKey, someIrrelevantValue);

    SimpleConfig applicationConfig = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION,
        applicationProperties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(DEFAULT_BATCH, bean.getBatch());

    Properties newApplicationProperties = assembleProperties(TIMEOUT_PROPERTY,
        String.valueOf(initialTimeout), anotherIrrelevantKey, String.valueOf(anotherIrrelevantValue));

    applicationConfig
        .onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newApplicationProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(DEFAULT_BATCH, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithDeletedProperties() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = new Properties();

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(DEFAULT_TIMEOUT, bean.getTimeout());
    assertEquals(DEFAULT_BATCH, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithDeletedPropertiesWithNoDefaultValue() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig6.class);

    TestJavaConfigBean5 bean = context.getBean(TestJavaConfigBean5.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithTypeMismatch() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    String newBatch = "newBatch";

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig1.class);

    TestJavaConfigBean bean = context.getBean(TestJavaConfigBean.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, newBatch);

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    assertEquals(newTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithValueInjectedAsParameter() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig3.class);

    TestJavaConfigBean2 bean = context.getBean(TestJavaConfigBean2.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    // Does not support this scenario
    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testApplicationPropertySourceWithValueInjectedInConfiguration() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig7.class);

    TestJavaConfigBean2 bean = context.getBean(TestJavaConfigBean2.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    // Does not support this scenario
    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithValueInjectedAsConstructorArgs() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig4.class);

    TestJavaConfigBean3 bean = context.getBean(TestJavaConfigBean3.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    // Does not support this scenario
    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Test
  public void testAutoUpdateWithInvalidSetter() throws Exception {
    int initialTimeout = 1000;
    int initialBatch = 2000;
    int newTimeout = 1001;
    int newBatch = 2001;

    Properties properties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(initialTimeout),
        BATCH_PROPERTY, String.valueOf(initialBatch));

    SimpleConfig config = prepareConfig(ConfigConsts.NAMESPACE_APPLICATION, properties);

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        AppConfig5.class);

    TestJavaConfigBean4 bean = context.getBean(TestJavaConfigBean4.class);

    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());

    Properties newProperties = assembleProperties(TIMEOUT_PROPERTY, String.valueOf(newTimeout),
        BATCH_PROPERTY, String.valueOf(newBatch));

    config.onRepositoryChange(ConfigConsts.NAMESPACE_APPLICATION, newProperties);

    TimeUnit.MILLISECONDS.sleep(50);

    // Does not support this scenario
    assertEquals(initialTimeout, bean.getTimeout());
    assertEquals(initialBatch, bean.getBatch());
  }

  @Configuration
  @EnableApolloConfig
  static class AppConfig1 {
    @Bean
    TestJavaConfigBean testJavaConfigBean() {
      return new TestJavaConfigBean();
    }
  }

  @Configuration
  @EnableApolloConfig({"application", "FX.apollo"})
  static class AppConfig2 {
    @Bean
    TestJavaConfigBean testJavaConfigBean() {
      return new TestJavaConfigBean();
    }
  }

  @Configuration
  @EnableApolloConfig
  static class AppConfig3 {
    /**
     * This case won't get auto updated
     */
    @Bean
    TestJavaConfigBean2 testJavaConfigBean2(@Value("${timeout:100}") int timeout, @Value("${batch:200}") int batch) {
      TestJavaConfigBean2 bean = new TestJavaConfigBean2();

      bean.setTimeout(timeout);
      bean.setBatch(batch);

      return bean;
    }
  }

  @Configuration
  @ComponentScan(
      includeFilters = {@Filter(type = FilterType.ANNOTATION, value = {Component.class})},
      excludeFilters = {@Filter(type = FilterType.ANNOTATION, value = {Configuration.class})})
  @EnableApolloConfig
  static class AppConfig4 {
  }

  @Configuration
  @EnableApolloConfig
  static class AppConfig5 {
    @Bean
    TestJavaConfigBean4 testJavaConfigBean() {
      return new TestJavaConfigBean4();
    }
  }

  @Configuration
  @EnableApolloConfig
  static class AppConfig6 {
    @Bean
    TestJavaConfigBean5 testJavaConfigBean() {
      return new TestJavaConfigBean5();
    }
  }

  @Configuration
  @EnableApolloConfig
  static class AppConfig7 {

    @Value("${batch}")
    private int batch;

    @Bean
    @Value("${timeout}")
    TestJavaConfigBean2 testJavaConfigBean2(int timeout) {
      TestJavaConfigBean2 bean = new TestJavaConfigBean2();

      bean.setTimeout(timeout);
      bean.setBatch(batch);

      return bean;
    }
  }

  static class TestJavaConfigBean {

    @Value("${timeout:100}")
    private int timeout;
    private int batch;

    @Value("${batch:200}")
    public void setBatch(int batch) {
      this.batch = batch;
    }

    public int getTimeout() {
      return timeout;
    }

    public int getBatch() {
      return batch;
    }
  }

  static class TestJavaConfigBean2 {
    private int timeout;
    private int batch;

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    public int getBatch() {
      return batch;
    }

    public void setBatch(int batch) {
      this.batch = batch;
    }
  }

  /**
   * This case won't get auto updated
   */
  @Component
  static class TestJavaConfigBean3 {
    private final int timeout;
    private final int batch;

    @Autowired
    public TestJavaConfigBean3(@Value("${timeout:100}") int timeout,
        @Value("${batch:200}") int batch) {
      this.timeout = timeout;
      this.batch = batch;
    }

    public int getTimeout() {
      return timeout;
    }

    public int getBatch() {
      return batch;
    }
  }

  /**
   * This case won't get auto updated
   */
  static class TestJavaConfigBean4 {

    private int timeout;
    private int batch;

    @Value("${batch:200}")
    public void setValues(int batch, @Value("${timeout:100}") int timeout) {
      this.batch = batch;
      this.timeout = timeout;
    }

    public int getTimeout() {
      return timeout;
    }

    public int getBatch() {
      return batch;
    }
  }

  static class TestJavaConfigBean5 {

    @Value("${timeout}")
    private int timeout;
    private int batch;

    @Value("${batch}")
    public void setBatch(int batch) {
      this.batch = batch;
    }

    public int getTimeout() {
      return timeout;
    }

    public int getBatch() {
      return batch;
    }
  }
}
