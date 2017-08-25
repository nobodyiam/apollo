package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseSensitiveWrappersTest {

  private CaseSensitiveWrappers caseSensitiveWrappers;
  @Mock
  private Multimap<String, Object> someMultimap;
  @Mock
  private Map<String, Object> someMap;
  @Mock
  private LoadingCache<String, Object> someLoadingCache;
  @Mock
  private Cache<String, Object> someCache;

  @Before
  public void setUp() throws Exception {
    caseSensitiveWrappers = new CaseSensitiveWrappers();
  }

  @Test
  public void checkInstance() throws Exception {
    assertTrue(caseSensitiveWrappers.multimapWrapper(someMultimap) instanceof CaseSensitiveMultimapWrapper);
    assertTrue(caseSensitiveWrappers.mapWrapper(someMap) instanceof CaseSensitiveMapWrapper);
    assertTrue(caseSensitiveWrappers.loadingCacheWrapper(someLoadingCache) instanceof CaseSensitiveLoadingCacheWrapper);
    assertTrue(caseSensitiveWrappers.cacheWrapper(someCache) instanceof CaseSensitiveCacheWrapper);
  }
}
