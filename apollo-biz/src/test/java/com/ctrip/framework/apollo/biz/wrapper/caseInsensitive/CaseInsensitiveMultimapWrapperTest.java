package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.common.collect.Multimap;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseInsensitiveMultimapWrapperTest {

  private CaseInsensitiveMultimapWrapper<String> caseInsensitiveMultimapWrapper;
  @Mock
  private Multimap<String, String> someMultiMap;

  @Before
  public void setUp() throws Exception {
    caseInsensitiveMultimapWrapper = new CaseInsensitiveMultimapWrapper<>(someMultiMap);
  }

  @Test
  public void testContainsKey() throws Exception {
    String someKey = "someKey";
    boolean someResult = true;

    when(someMultiMap.containsKey(someKey.toLowerCase())).thenReturn(someResult);

    assertEquals(someResult, caseInsensitiveMultimapWrapper.containsKey(someKey));

    verify(someMultiMap, times(1)).containsKey(someKey.toLowerCase());
  }

  @Test
  public void testGet() throws Exception {
    String someKey = "someKey";
    Collection<String> someResult = Lists.newArrayList("someResult");

    when(someMultiMap.get(someKey.toLowerCase())).thenReturn(someResult);

    assertEquals(someResult, caseInsensitiveMultimapWrapper.get(someKey));

    verify(someMultiMap, times(1)).get(someKey.toLowerCase());
  }

  @Test
  public void testPut() throws Exception {
    String someKey = "someKey";
    String someValue = "someValue";
    boolean someResult = true;

    when(someMultiMap.put(someKey.toLowerCase(), someValue)).thenReturn(someResult);

    assertEquals(someResult, caseInsensitiveMultimapWrapper.put(someKey, someValue));

    verify(someMultiMap, times(1)).put(someKey.toLowerCase(), someValue);
  }

  @Test
  public void testRemove() throws Exception {
    String someKey = "someKey";
    String someValue = "someValue";
    boolean someResult = true;

    when(someMultiMap.remove(someKey.toLowerCase(), someValue)).thenReturn(someResult);

    assertEquals(someResult, caseInsensitiveMultimapWrapper.remove(someKey, someValue));

    verify(someMultiMap, times(1)).remove(someKey.toLowerCase(), someValue);
  }

  @Test
  public void testSize() throws Exception {
    int someSize = 1;

    when(someMultiMap.size()).thenReturn(someSize);

    assertEquals(someSize, caseInsensitiveMultimapWrapper.size());

    verify(someMultiMap, times(1)).size();
  }
}
