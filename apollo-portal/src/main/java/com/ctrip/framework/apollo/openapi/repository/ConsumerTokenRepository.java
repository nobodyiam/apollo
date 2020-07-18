package com.ctrip.framework.apollo.openapi.repository;

import com.ctrip.framework.apollo.openapi.entity.ConsumerToken;
import java.util.Date;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConsumerTokenRepository extends PagingAndSortingRepository<ConsumerToken, Long> {

  /**
   * find consumer token by token
   *
   * @param token the token
   * @param validDate the date when the token is valid
   */
  ConsumerToken findTopByTokenAndExpiresAfter(String token, Date validDate);

  ConsumerToken findByConsumerId(Long consumerId);
}
