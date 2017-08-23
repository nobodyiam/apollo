package com.ctrip.framework.apollo.biz.utils;

import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * A simple case insensitive wrapper which does not take locale into account.
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveMultimapWrapper<T> {
  private final Multimap<String, T> delegate;

  public CaseInsensitiveMultimapWrapper(Multimap<String, T> delegate) {
    this.delegate = delegate;
  }

  public boolean containsKey(String key) {
    return delegate.containsKey(key.toLowerCase());
  }

  public Collection<T> get(String key) {
    return delegate.get(key.toLowerCase());
  }

  public boolean put(String key, T value) {
    return delegate.put(key.toLowerCase(), value);
  }

  public boolean remove(String key, T value) {
    return delegate.remove(key.toLowerCase(), value);
  }

  public int size() {
    return delegate.size();
  }
}
