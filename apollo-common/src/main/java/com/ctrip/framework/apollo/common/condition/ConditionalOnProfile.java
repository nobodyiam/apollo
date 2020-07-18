package com.ctrip.framework.apollo.common.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional} that only matches when the specified profiles are active.
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnProfileCondition.class)
public @interface ConditionalOnProfile {

  /**
   * The profiles that should be active
   */
  String[] value() default {};
}
