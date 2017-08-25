package com.ctrip.framework.apollo.biz.wrapper.caseInsensitive;

import java.util.Collection;

import com.ctrip.framework.apollo.biz.wrapper.MultimapWrapper;
import com.google.common.collect.Multimap;

/**
 * A simple case insensitive wrapper which does not take locale into account.
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseInsensitiveMultimapWrapper<T> implements MultimapWrapper<T> {
  private final Multimap<String, T> delegate;

  public CaseInsensitiveMultimapWrapper(Multimap<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public boolean containsKey(String key) {
    return delegate.containsKey(key.toLowerCase());
  }

  @Override
  public boolean containsEntry(String key, T value) {
    return delegate.containsEntry(key.toLowerCase(), value);
  }

  public Collection<T> get(String key) {
    return delegate.get(key.toLowerCase());
  }

  public boolean put(String key, T value) {
    return delegate.put(key.toLowerCase(), value);
  }

  @Override
  public boolean putAll(String key, Iterable<? extends T> values) {
    return delegate.putAll(key.toLowerCase(), values);
  }

  public boolean remove(String key, T value) {
    return delegate.remove(key.toLowerCase(), value);
  }

  @Override
  public Collection<T> removeAll(String key) {
    return delegate.removeAll(key.toLowerCase());
  }

  public int size() {
    return delegate.size();
  }
}
