package com.software.core.data.repository;

import com.software.core.data.paging.BiliRecommendPagingSource;
import com.software.core.data.paging.GetPopularPagingSource;
import com.software.core.data.session.BiliSessionManager;
import com.software.core.network.BiliApiService;
import com.software.core.network.BiliAppApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.software.core.network.BiliAppNetwork",
    "com.software.core.network.BiliApiNetwork"
})
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
public final class VideoRepositoryImpl_Factory implements Factory<VideoRepositoryImpl> {
  private final Provider<BiliAppApiService> appApiServiceProvider;

  private final Provider<BiliApiService> apiServiceProvider;

  private final Provider<BiliRecommendPagingSource> recommendPagingSourceProvider;

  private final Provider<GetPopularPagingSource> popularPagingSourceProvider;

  private final Provider<BiliSessionManager> sessionManagerProvider;

  private VideoRepositoryImpl_Factory(Provider<BiliAppApiService> appApiServiceProvider,
      Provider<BiliApiService> apiServiceProvider,
      Provider<BiliRecommendPagingSource> recommendPagingSourceProvider,
      Provider<GetPopularPagingSource> popularPagingSourceProvider,
      Provider<BiliSessionManager> sessionManagerProvider) {
    this.appApiServiceProvider = appApiServiceProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.recommendPagingSourceProvider = recommendPagingSourceProvider;
    this.popularPagingSourceProvider = popularPagingSourceProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public VideoRepositoryImpl get() {
    return newInstance(appApiServiceProvider.get(), apiServiceProvider.get(), recommendPagingSourceProvider.get(), popularPagingSourceProvider.get(), sessionManagerProvider.get());
  }

  public static VideoRepositoryImpl_Factory create(
      Provider<BiliAppApiService> appApiServiceProvider,
      Provider<BiliApiService> apiServiceProvider,
      Provider<BiliRecommendPagingSource> recommendPagingSourceProvider,
      Provider<GetPopularPagingSource> popularPagingSourceProvider,
      Provider<BiliSessionManager> sessionManagerProvider) {
    return new VideoRepositoryImpl_Factory(appApiServiceProvider, apiServiceProvider, recommendPagingSourceProvider, popularPagingSourceProvider, sessionManagerProvider);
  }

  public static VideoRepositoryImpl newInstance(BiliAppApiService appApiService,
      BiliApiService apiService, BiliRecommendPagingSource recommendPagingSource,
      GetPopularPagingSource popularPagingSource, BiliSessionManager sessionManager) {
    return new VideoRepositoryImpl(appApiService, apiService, recommendPagingSource, popularPagingSource, sessionManager);
  }
}
