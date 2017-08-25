package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import com.ctrip.framework.apollo.biz.wrapper.LoadingCacheWrapper;
import com.google.common.cache.LoadingCache;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveLoadingCacheWrapper<T> implements LoadingCacheWrapper<T> {

  private final LoadingCache<String, T> delegate;

  public CaseInsensitiveLoadingCacheWrapper(LoadingCache<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T getUnchecked(String key) {
    return delegate.getUnchecked(key.toLowerCase());
  }

  @Override
  public void invalidate(String key) {
    delegate.invalidate(key.toLowerCase());
  }
}
