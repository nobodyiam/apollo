package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseSensitiveMultimapWrapperTest {

  private CaseSensitiveMultimapWrapper<Object> multimapWrapper;
  @Mock
  private Multimap<String, Object> multimap;

  @Before
  public void setUp() throws Exception {
    multimapWrapper = new CaseSensitiveMultimapWrapper<>(multimap);
  }

  @Test
  public void testIsEmpty() throws Exception {
    boolean someResult = true;

    when(multimap.isEmpty()).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.isEmpty());

    verify(multimap, times(1)).isEmpty();
  }

  @Test
  public void testContainsKey() throws Exception {
    String someKey = "someKey";
    boolean someResult = true;

    when(multimap.containsKey(someKey)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.containsKey(someKey));

    verify(multimap, times(1)).containsKey(someKey);
  }

  @Test
  public void testContainsEntry() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);
    boolean someResult = true;

    when(multimap.containsEntry(someKey, someValue)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.containsEntry(someKey, someValue));

    verify(multimap, times(1)).containsEntry(someKey, someValue);
  }

  @Test
  public void testGet() throws Exception {
    String someKey = "someKey";
    Collection<Object> someResult = Lists.newArrayList(mock(Object.class));

    when(multimap.get(someKey)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.get(someKey));

    verify(multimap, times(1)).get(someKey);
  }

  @Test
  public void testPut() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);
    boolean someResult = true;

    when(multimap.put(someKey, someValue)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.put(someKey, someValue));

    verify(multimap, times(1)).put(someKey, someValue);
  }

  @Test
  public void testPutAll() throws Exception {
    String someKey = "someKey";
    Iterable<Object> someValue = Lists.newArrayList(mock(Object.class));
    boolean someResult = true;

    when(multimap.putAll(someKey, someValue)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.putAll(someKey, someValue));

    verify(multimap, times(1)).putAll(someKey, someValue);
  }

  @Test
  public void testRemove() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);
    boolean someResult = true;

    when(multimap.remove(someKey, someValue)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.remove(someKey, someValue));

    verify(multimap, times(1)).remove(someKey, someValue);
  }

  @Test
  public void testRemoveAll() throws Exception {
    String someKey = "someKey";
    Collection<Object> someResult = Lists.newArrayList(mock(Object.class));

    when(multimap.removeAll(someKey)).thenReturn(someResult);

    assertEquals(someResult, multimapWrapper.removeAll(someKey));

    verify(multimap, times(1)).removeAll(someKey);
  }

  @Test
  public void testSize() throws Exception {
    int someSize = 1;

    when(multimap.size()).thenReturn(someSize);

    assertEquals(someSize, multimapWrapper.size());

    verify(multimap, times(1)).size();
  }

}
