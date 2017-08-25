package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import com.ctrip.framework.apollo.biz.wrapper.CacheWrapper;
import com.google.common.cache.Cache;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveCacheWrapper<T> implements CacheWrapper<T> {

  private final Cache<String, T> delegate;

  public CaseInsensitiveCacheWrapper(Cache<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T getIfPresent(String key) {
    return delegate.getIfPresent(key.toLowerCase());
  }

  @Override
  public void put(String key, T value) {
    delegate.put(key.toLowerCase(), value);
  }

  @Override
  public void invalidate(String key) {
    delegate.invalidate(key.toLowerCase());
  }
}
