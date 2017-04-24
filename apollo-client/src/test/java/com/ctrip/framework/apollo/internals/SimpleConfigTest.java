package com.ctrip.framework.apollo.internals;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigTest {
  private String someNamespace;
  @Mock
  private ConfigRepository configRepository;

  @Before
  public void setUp() throws Exception {
    someNamespace = "someName";
  }

  @Test
  public void testGetProperty() throws Exception {
    Properties someProperties = new Properties();
    String someKey = "someKey";
    String someValue = "someValue";
    someProperties.setProperty(someKey, someValue);

    when(configRepository.getConfig()).thenReturn(someProperties);

    SimpleConfig config = new SimpleConfig(someNamespace, configRepository);

    assertEquals(someValue, config.getProperty(someKey, null));
  }

  @Test
  public void testLoadConfigFromConfigRepositoryError() throws Exception {
    when(configRepository.getConfig()).thenThrow(Throwable.class);

    Config config = new SimpleConfig(someNamespace, configRepository);

    String someKey = "someKey";
    String anyValue = "anyValue" + Math.random();
    assertEquals(anyValue, config.getProperty(someKey, anyValue));
  }

  @Test
  public void testOnRepositoryChange() throws Exception {
    Properties someProperties = new Properties();
    String someKey = "someKey";
    String someValue = "someValue";
    String anotherKey = "anotherKey";
    String anotherValue = "anotherValue";
    someProperties.putAll(ImmutableMap.of(someKey, someValue, anotherKey, anotherValue));

    Properties anotherProperties = new Properties();
    String newKey = "newKey";
    String newValue = "newValue";
    String someValueNew = "someValueNew";
    anotherProperties.putAll(ImmutableMap.of(someKey, someValueNew, newKey, newValue));

    when(configRepository.getConfig()).thenReturn(someProperties);

    final SettableFuture<ConfigChangeEvent> configChangeFuture = SettableFuture.create();
    ConfigChangeListener someListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        configChangeFuture.set(changeEvent);
      }
    };

    SimpleConfig config = new SimpleConfig(someNamespace, configRepository);
    config.addChangeListener(someListener);

    config.onRepositoryChange(someNamespace, anotherProperties);

    ConfigChangeEvent changeEvent = configChangeFuture.get(500, TimeUnit.MILLISECONDS);

    assertEquals(someNamespace, changeEvent.getNamespace());
    assertEquals(3, changeEvent.changedKeys().size());

    ConfigChange someKeyChange = changeEvent.getChange(someKey);
    assertEquals(someValue, someKeyChange.getOldValue());
    assertEquals(someValueNew, someKeyChange.getNewValue());
    assertEquals(PropertyChangeType.MODIFIED, someKeyChange.getChangeType());

    ConfigChange anotherKeyChange = changeEvent.getChange(anotherKey);
    assertEquals(anotherValue, anotherKeyChange.getOldValue());
    assertEquals(null, anotherKeyChange.getNewValue());
    assertEquals(PropertyChangeType.DELETED, anotherKeyChange.getChangeType());

    ConfigChange newKeyChange = changeEvent.getChange(newKey);
    assertEquals(null, newKeyChange.getOldValue());
    assertEquals(newValue, newKeyChange.getNewValue());
    assertEquals(PropertyChangeType.ADDED, newKeyChange.getChangeType());
  }
}
