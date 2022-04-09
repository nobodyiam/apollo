/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.spring.spi;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import org.springframework.core.Ordered;

public interface SpringConfigChangeListener extends ConfigChangeListener, Ordered {

  /**
   * whether the listener is interested in the given namespace
   */
  boolean isInterested(ConfigPropertySource configPropertySource);

  default int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
