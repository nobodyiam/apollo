package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.cache.LoadingCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseInsensitiveLoadingCacheWrapperTest {

  private CaseInsensitiveLoadingCacheWrapper loadingCacheWrapper;
  @Mock
  private LoadingCache<String, Object> loadingCache;

  @Before
  public void setUp() throws Exception {
    loadingCacheWrapper = new CaseInsensitiveLoadingCacheWrapper<>(loadingCache);
  }

  @Test
  public void testGetUnchecked() throws Exception {
    String someKey = "someKey";

    Object someValue = mock(Object.class);

    when(loadingCache.getUnchecked(someKey.toLowerCase())).thenReturn(someValue);

    assertEquals(someValue, loadingCacheWrapper.getUnchecked(someKey));

    verify(loadingCache, times(1)).getUnchecked(someKey.toLowerCase());
  }

  @Test(expected = RuntimeException.class)
  public void testGetUncheckedWithException() throws Exception {
    String someKey = "someKey";

    when(loadingCache.getUnchecked(someKey.toLowerCase())).thenThrow(new RuntimeException("Some Error"));

    loadingCacheWrapper.getUnchecked(someKey);
  }

  @Test
  public void testInvalidate() throws Exception {
    String someKey = "someKey";

    loadingCacheWrapper.invalidate(someKey);

    verify(loadingCache, times(1)).invalidate(someKey.toLowerCase());
  }
}
