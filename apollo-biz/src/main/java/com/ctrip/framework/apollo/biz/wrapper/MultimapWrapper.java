package com.ctrip.framework.apollo.biz.wrapper;

import java.util.Collection;

/**
 * Wrapper for multimap
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public interface MultimapWrapper<T> {

  boolean containsKey(String key);

  Collection<T> get(String key);

  boolean put(String key, T value);

  boolean remove(String key, T value);

  int size();
}
