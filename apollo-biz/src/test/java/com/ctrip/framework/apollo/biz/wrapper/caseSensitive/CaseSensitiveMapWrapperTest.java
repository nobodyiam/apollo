package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseSensitiveMapWrapperTest {

  private CaseSensitiveMapWrapper<Object> mapWrapper;
  @Mock
  private Map<String, Object> map;

  @Before
  public void setUp() throws Exception {
    mapWrapper = new CaseSensitiveMapWrapper<>(map);
  }

  @Test
  public void testGet() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    when(map.get(someKey)).thenReturn(someValue);

    assertEquals(someValue, mapWrapper.get(someKey));

    verify(map, times(1)).get(someKey);
  }

  @Test
  public void testPut() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);
    Object anotherValue = mock(Object.class);

    when(map.put(someKey, someValue)).thenReturn(anotherValue);

    assertEquals(anotherValue, mapWrapper.put(someKey, someValue));

    verify(map, times(1)).put(someKey, someValue);
  }

  @Test
  public void testRemove() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    when(map.remove(someKey)).thenReturn(someValue);

    assertEquals(someValue, mapWrapper.remove(someKey));

    verify(map, times(1)).remove(someKey);
  }
}
