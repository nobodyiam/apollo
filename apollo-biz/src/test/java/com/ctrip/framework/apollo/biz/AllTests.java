package com.ctrip.framework.apollo.biz;

import com.ctrip.framework.apollo.biz.config.BizConfigTest;
import com.ctrip.framework.apollo.biz.grayReleaseRule.GrayReleaseRulesHolderTest;
import com.ctrip.framework.apollo.biz.message.DatabaseMessageSenderTest;
import com.ctrip.framework.apollo.biz.message.ReleaseMessageScannerTest;
import com.ctrip.framework.apollo.biz.repository.AppNamespaceRepositoryTest;
import com.ctrip.framework.apollo.biz.repository.AppRepositoryTest;
import com.ctrip.framework.apollo.biz.service.AdminServiceTest;
import com.ctrip.framework.apollo.biz.service.AdminServiceTransactionTest;
import com.ctrip.framework.apollo.biz.service.ClusterServiceTest;
import com.ctrip.framework.apollo.biz.service.InstanceServiceTest;
import com.ctrip.framework.apollo.biz.service.NamespaceBranchServiceTest;
import com.ctrip.framework.apollo.biz.service.NamespacePublishInfoTest;
import com.ctrip.framework.apollo.biz.service.NamespaceServiceIntegrationTest;
import com.ctrip.framework.apollo.biz.service.NamespaceServiceTest;
import com.ctrip.framework.apollo.biz.service.ReleaseCreationTest;
import com.ctrip.framework.apollo.biz.service.ReleaseServiceTest;
import com.ctrip.framework.apollo.biz.service.BizDBPropertySourceTest;
import com.ctrip.framework.apollo.biz.utils.ReleaseKeyGeneratorTest;

import com.ctrip.framework.apollo.biz.wrapper.caseInsensitive.CaseInsensitiveCacheWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseInsensitive.CaseInsensitiveLoadingCacheWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseInsensitive.CaseInsensitiveMapWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseInsensitive.CaseInsensitiveMultimapWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseInsensitive.CaseInsensitiveWrappersTest;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveCacheWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveLoadingCacheWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveMapWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveMultimapWrapperTest;
import com.ctrip.framework.apollo.biz.wrapper.caseSensitive.CaseSensitiveWrappersTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AppRepositoryTest.class, AppNamespaceRepositoryTest.class, AdminServiceTest.class,
    AdminServiceTransactionTest.class, DatabaseMessageSenderTest.class, BizDBPropertySourceTest.class,
    ReleaseServiceTest.class, ReleaseMessageScannerTest.class, ClusterServiceTest.class, ReleaseKeyGeneratorTest.class,
    InstanceServiceTest.class, GrayReleaseRulesHolderTest.class, NamespaceBranchServiceTest.class,
    ReleaseCreationTest.class, NamespacePublishInfoTest.class, NamespaceServiceIntegrationTest.class,
    BizConfigTest.class, NamespaceServiceTest.class, CaseInsensitiveMultimapWrapperTest.class,
    CaseInsensitiveMapWrapperTest.class, CaseInsensitiveLoadingCacheWrapperTest.class,
    CaseSensitiveMapWrapperTest.class, CaseSensitiveMultimapWrapperTest.class,
    CaseSensitiveLoadingCacheWrapperTest.class, CaseSensitiveWrappersTest.class, CaseInsensitiveWrappersTest.class,
    CaseInsensitiveCacheWrapperTest.class, CaseSensitiveCacheWrapperTest.class
})
public class AllTests {

}
