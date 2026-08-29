package com.software.core.data.paging;

import com.software.core.network.BiliApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("com.software.core.network.BiliApiNetwork")
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
public final class GetPopularPagingSource_Factory implements Factory<GetPopularPagingSource> {
  private final Provider<BiliApiService> apiServiceProvider;

  private GetPopularPagingSource_Factory(Provider<BiliApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public GetPopularPagingSource get() {
    return newInstance(apiServiceProvider.get());
  }

  public static GetPopularPagingSource_Factory create(Provider<BiliApiService> apiServiceProvider) {
    return new GetPopularPagingSource_Factory(apiServiceProvider);
  }

  public static GetPopularPagingSource newInstance(BiliApiService apiService) {
    return new GetPopularPagingSource(apiService);
  }
}
