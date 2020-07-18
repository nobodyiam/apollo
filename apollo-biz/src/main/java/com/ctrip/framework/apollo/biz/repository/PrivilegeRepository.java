package com.ctrip.framework.apollo.biz.repository;

import com.ctrip.framework.apollo.biz.entity.Privilege;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PrivilegeRepository extends PagingAndSortingRepository<Privilege, Long> {

  List<Privilege> findByNamespaceId(long namespaceId);

  List<Privilege> findByNamespaceIdAndPrivilType(long namespaceId, String privilType);

  Privilege findByNamespaceIdAndNameAndPrivilType(long namespaceId, String name, String privilType);
}
