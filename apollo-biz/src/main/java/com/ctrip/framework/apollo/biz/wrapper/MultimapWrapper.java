package com.ctrip.framework.apollo.biz.wrapper;

import java.util.Collection;

/**
 * Wrapper for multimap
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public interface MultimapWrapper<T> {

  boolean isEmpty();

  boolean containsKey(String key);

  boolean containsEntry(String key, T value);

  Collection<T> get(String key);

  boolean put(String key, T value);

  boolean putAll(String key, Iterable<? extends T> values);

  boolean remove(String key, T value);

  Collection<T> removeAll(String key);

  int size();
}
