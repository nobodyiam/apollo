package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import com.ctrip.framework.apollo.biz.wrapper.CacheWrapper;
import com.ctrip.framework.apollo.biz.wrapper.LoadingCacheWrapper;
import com.ctrip.framework.apollo.biz.wrapper.MapWrapper;
import com.ctrip.framework.apollo.biz.wrapper.MultimapWrapper;
import com.ctrip.framework.apollo.biz.wrapper.Wrappers;
import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveWrappers implements Wrappers {

  @Override
  public <T> MultimapWrapper<T> multimapWrapper(Multimap<String, T> multimap) {
    return new CaseInsensitiveMultimapWrapper<>(multimap);
  }

  @Override
  public <T> MapWrapper<T> mapWrapper(Map<String, T> map) {
    return new CaseInsensitiveMapWrapper<>(map);
  }

  @Override
  public <T> LoadingCacheWrapper<T> loadingCacheWrapper(LoadingCache<String, T> loadingCache) {
    return new CaseInsensitiveLoadingCacheWrapper<>(loadingCache);
  }

  @Override
  public <T> CacheWrapper<T> cacheWrapper(Cache<String, T> cache) {
    return new CaseInsensitiveCacheWrapper<>(cache);
  }
}
