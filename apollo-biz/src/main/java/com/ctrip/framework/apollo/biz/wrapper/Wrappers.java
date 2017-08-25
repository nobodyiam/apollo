package com.ctrip.framework.apollo.biz.wrapper;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface Wrappers {
  <T> MultimapWrapper<T> multimapWrapper(Multimap<String, T> multimap);

  <T> MapWrapper<T> mapWrapper(Map<String, T> map);

  <T> LoadingCacheWrapper<T> loadingCacheWrapper(LoadingCache<String, T> loadingCache);
}
