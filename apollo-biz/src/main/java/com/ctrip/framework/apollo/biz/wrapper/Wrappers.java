package com.ctrip.framework.apollo.biz.wrapper;

import com.google.common.collect.Multimap;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface Wrappers {
  <T> MultimapWrapper<T> multimapWrapper(Multimap<String, T> multimap);
}
