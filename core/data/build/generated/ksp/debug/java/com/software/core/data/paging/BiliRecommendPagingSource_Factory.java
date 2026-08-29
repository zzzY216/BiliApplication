package com.software.core.data.paging;

import com.software.core.network.BiliAppApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("com.software.core.network.BiliAppNetwork")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class BiliRecommendPagingSource_Factory implements Factory<BiliRecommendPagingSource> {
  private final Provider<BiliAppApiService> apiServiceProvider;

  private BiliRecommendPagingSource_Factory(Provider<BiliAppApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public BiliRecommendPagingSource get() {
    return newInstance(apiServiceProvider.get());
  }

  public static BiliRecommendPagingSource_Factory create(
      Provider<BiliAppApiService> apiServiceProvider) {
    return new BiliRecommendPagingSource_Factory(apiServiceProvider);
  }

  public static BiliRecommendPagingSource newInstance(BiliAppApiService apiService) {
    return new BiliRecommendPagingSource(apiService);
  }
}
