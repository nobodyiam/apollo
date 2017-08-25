package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import com.ctrip.framework.apollo.biz.wrapper.LoadingCacheWrapper;
import com.google.common.cache.LoadingCache;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseSensitiveLoadingCacheWrapper<T> implements LoadingCacheWrapper<T> {

  private final LoadingCache<String, T> delegate;

  public CaseSensitiveLoadingCacheWrapper(LoadingCache<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T getUnchecked(String key) {
    return delegate.getUnchecked(key);
  }

  @Override
  public void invalidate(String key) {
    delegate.invalidate(key);
  }
}
