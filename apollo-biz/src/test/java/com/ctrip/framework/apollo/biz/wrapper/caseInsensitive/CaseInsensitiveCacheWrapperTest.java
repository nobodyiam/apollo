package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.cache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseInsensitiveCacheWrapperTest {
  private CaseInsensitiveCacheWrapper<Object> caseInsensitiveCacheWrapper;
  @Mock
  private Cache<String, Object> cache;

  @Before
  public void setUp() throws Exception {
    caseInsensitiveCacheWrapper = new CaseInsensitiveCacheWrapper<>(cache);
  }

  @Test
  public void getIfPresent() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    when(cache.getIfPresent(someKey.toLowerCase())).thenReturn(someValue);

    assertEquals(someValue, caseInsensitiveCacheWrapper.getIfPresent(someKey));

    verify(cache, times(1)).getIfPresent(someKey.toLowerCase());
  }

  @Test
  public void put() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    caseInsensitiveCacheWrapper.put(someKey, someValue);

    verify(cache, times(1)).put(someKey.toLowerCase(), someValue);
  }

  @Test
  public void invalidate() throws Exception {
    String someKey = "someKey";

    caseInsensitiveCacheWrapper.invalidate(someKey);

    verify(cache, times(1)).invalidate(someKey.toLowerCase());
  }
}
