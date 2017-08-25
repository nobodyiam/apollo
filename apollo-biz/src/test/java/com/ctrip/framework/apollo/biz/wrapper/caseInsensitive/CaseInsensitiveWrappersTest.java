package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import static org.junit.Assert.*;

import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveLoadingCacheWrapper;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveMapWrapper;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveMultimapWrapper;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveWrappers;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
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
public class CaseInsensitiveWrappersTest {

  private CaseInsensitiveWrappers caseInsensitiveWrappers;

  @Mock
  private Multimap<String, Object> someMultimap;
  @Mock
  private Map<String, Object> someMap;
  @Mock
  private LoadingCache<String, Object> someLoadingCache;

  @Before
  public void setUp() throws Exception {
    caseInsensitiveWrappers = new CaseInsensitiveWrappers();
  }

  @Test
  public void checkInstance() throws Exception {
    assertTrue(caseInsensitiveWrappers.multimapWrapper(someMultimap) instanceof CaseInsensitiveMultimapWrapper);
    assertTrue(caseInsensitiveWrappers.mapWrapper(someMap) instanceof CaseInsensitiveMapWrapper);
    assertTrue(
        caseInsensitiveWrappers.loadingCacheWrapper(someLoadingCache) instanceof CaseInsensitiveLoadingCacheWrapper);
  }
}
