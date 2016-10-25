package com.sap.sailing.cache.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ // Black Box Tests
				AbstractCacheKeyTest.class,
				AbstractCacheValueContainerTest.class,
				ArgsWeakReferenceTest.class,
				CachedMethodSignatureTest.class,
				CacheFetchTest.class,
				CacheImplTest.class,
				DependencyThreadLocalTest.class,
				IdentityCacheKeyTest.class,
				ImmutableCacheKeyTest.class,
				InstanceFieldIDTest.class,
				IteratorWrapperTest.class,
				ListIteratorWrapperTest.class,
				MutableCacheKeyTest.class,
				RecalculationTaskTest.class,
				RecalculatorTest.class,
				RecalculatorThreadFactoryTest.class,
				StaticFieldIDTest.class
				
				// Functional Tests
				})
public class AllTests {

}
