/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.spring.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.spring.AbstractSpringIntegrationTest;
import com.ctrip.framework.apollo.spring.spi.SpringConfigChangeListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

public class PropertySourcesProcessorTest extends AbstractSpringIntegrationTest {

  private ConfigurableEnvironment environment;
  private ConfigurableListableBeanFactory beanFactory;
  private PropertySourcesProcessor processor;
  private MutablePropertySources propertySources;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    propertySources = mock(MutablePropertySources.class);
    environment = mock(ConfigurableEnvironment.class);
    when(environment.getPropertySources()).thenReturn(propertySources);
    beanFactory = mock(ConfigurableListableBeanFactory.class);
    processor = new PropertySourcesProcessor();
    processor.setEnvironment(environment);
  }

  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();
    PropertySourcesProcessor.reset();
  }

  @Test
  public void testInitializePropertySources() {
    String namespaceName = "someNamespace";
    String anotherNamespaceName = "anotherNamespace";
    Config config = mock(Config.class);
    Config anotherConfig = mock(Config.class);
    mockConfig(namespaceName, config);
    mockConfig(anotherNamespaceName, anotherConfig);
    PropertySourcesProcessor.addNamespaces(Lists.newArrayList(namespaceName, anotherNamespaceName),
        0);

    processor.postProcessBeanFactory(beanFactory);

    ArgumentCaptor<CompositePropertySource> argumentCaptor = ArgumentCaptor.forClass(
        CompositePropertySource.class);
    verify(propertySources).addFirst(argumentCaptor.capture());

    CompositePropertySource compositePropertySource = argumentCaptor.getValue();
    assertEquals(2, compositePropertySource.getPropertySources().size());

    ConfigPropertySource propertySource = (ConfigPropertySource) Lists.newArrayList(
        compositePropertySource.getPropertySources()).get(0);
    ConfigPropertySource anotherPropertySource = (ConfigPropertySource) Lists.newArrayList(
        compositePropertySource.getPropertySources()).get(1);

    assertEquals(namespaceName, propertySource.getName());
    assertSame(config, propertySource.getSource());
    assertEquals(anotherNamespaceName, anotherPropertySource.getName());
    assertSame(anotherConfig, anotherPropertySource.getSource());
  }

  @Test
  public void testSpringConfigChangeListener() {
    String namespaceName = "someNamespace";
    Config config = mock(Config.class);
    mockConfig(namespaceName, config);
    PropertySourcesProcessor.addNamespaces(Lists.newArrayList(namespaceName), 0);
    ConfigPropertySourceFactory factory = mock(ConfigPropertySourceFactory.class);
    MockInjector.setInstance(ConfigPropertySourceFactory.class, factory);

    int order = 0;
    int anotherOrder = -1;
    int yetAnotherOrder = -2;
    SpringConfigChangeListener someListener = mock(SpringConfigChangeListener.class);
    when(someListener.getOrder()).thenReturn(order);
    when(someListener.isInterested(any(ConfigPropertySource.class))).thenReturn(true);
    SpringConfigChangeListener anotherListener = mock(SpringConfigChangeListener.class);
    when(anotherListener.getOrder()).thenReturn(anotherOrder);
    when(anotherListener.isInterested(any(ConfigPropertySource.class))).thenReturn(true);
    SpringConfigChangeListener yetAnotherListener = mock(SpringConfigChangeListener.class);
    when(yetAnotherListener.getOrder()).thenReturn(yetAnotherOrder);
    when(yetAnotherListener.isInterested(any(ConfigPropertySource.class))).thenReturn(false);

    Map<String, SpringConfigChangeListener> listeners = ImmutableMap.of(someListener.toString(),
        someListener, anotherListener.toString(), anotherListener);
    when(beanFactory.getBeansOfType(SpringConfigChangeListener.class)).thenReturn(listeners);

    processor.postProcessBeanFactory(beanFactory);

    ArgumentCaptor<CompositePropertySource> argumentCaptor = ArgumentCaptor.forClass(
        CompositePropertySource.class);
    verify(propertySources).addFirst(argumentCaptor.capture());

    CompositePropertySource compositePropertySource = argumentCaptor.getValue();
    assertEquals(1, compositePropertySource.getPropertySources().size());

    ConfigPropertySource propertySource = (ConfigPropertySource) Lists.newArrayList(
        compositePropertySource.getPropertySources()).get(0);

    assertEquals(namespaceName, propertySource.getName());
    assertSame(config, propertySource.getSource());

    InOrder inOrder = inOrder(config);
    inOrder.verify(config, times(1)).addChangeListener(anotherListener);
    inOrder.verify(config, times(1)).addChangeListener(someListener);
    inOrder.verify(config, never()).addChangeListener(yetAnotherListener);
  }
}
