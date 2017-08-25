package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import com.ctrip.framework.apollo.biz.wrapper.CacheWrapper;
import com.google.common.cache.Cache;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseSensitiveCacheWrapper<T> implements CacheWrapper<T> {
  private final Cache<String, T> delegate;

  public CaseSensitiveCacheWrapper(Cache<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T getIfPresent(String key) {
    return delegate.getIfPresent(key);
  }

  @Override
  public void put(String key, T value) {
    delegate.put(key, value);
  }

  @Override
  public void invalidate(String key) {
    delegate.invalidate(key);
  }
}
