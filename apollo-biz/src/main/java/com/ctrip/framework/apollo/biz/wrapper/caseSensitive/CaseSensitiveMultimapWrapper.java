package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import com.ctrip.framework.apollo.biz.wrapper.MultimapWrapper;
import com.google.common.collect.Multimap;
import java.util.Collection;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseSensitiveMultimapWrapper<T> implements MultimapWrapper<T> {

  private final Multimap<String, T> delegate;

  public CaseSensitiveMultimapWrapper(Multimap<String, T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean containsKey(String key) {
    return delegate.containsKey(key);
  }

  @Override
  public Collection<T> get(String key) {
    return delegate.get(key);
  }

  @Override
  public boolean put(String key, T value) {
    return delegate.put(key, value);
  }

  @Override
  public boolean remove(String key, T value) {
    return delegate.remove(key, value);
  }

  @Override
  public int size() {
    return delegate.size();
  }
}
