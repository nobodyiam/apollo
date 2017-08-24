package com.ctrip.framework.apollo.biz.wrapper.caseSensitive;

import com.ctrip.framework.apollo.biz.wrapper.MultimapWrapper;
import com.ctrip.framework.apollo.biz.wrapper.Wrappers;
import com.google.common.collect.Multimap;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class CaseSensitiveWrappers implements Wrappers {

  @Override
  public <T> MultimapWrapper<T> multimapWrapper(Multimap<String, T> multimap) {
    return null;
  }
}
