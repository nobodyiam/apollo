package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.common.cache.Cache;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseSensitiveCacheWrapperTest {
  private CaseSensitiveCacheWrapper<Object> caseSensitiveCacheWrapper;
  @Mock
  private Cache<String, Object> cache;

  @Before
  public void setUp() throws Exception {
    caseSensitiveCacheWrapper = new CaseSensitiveCacheWrapper<>(cache);
  }

  @Test
  public void getIfPresent() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    when(cache.getIfPresent(someKey)).thenReturn(someValue);

    assertEquals(someValue, caseSensitiveCacheWrapper.getIfPresent(someKey));

    verify(cache, times(1)).getIfPresent(someKey);
  }

  @Test
  public void put() throws Exception {
    String someKey = "someKey";
    Object someValue = mock(Object.class);

    caseSensitiveCacheWrapper.put(someKey, someValue);

    verify(cache, times(1)).put(someKey, someValue);
  }

  @Test
  public void invalidate() throws Exception {
    String someKey = "someKey";

    caseSensitiveCacheWrapper.invalidate(someKey);

    verify(cache, times(1)).invalidate(someKey);
  }

}
