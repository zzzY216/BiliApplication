package com.software.core.data.repository;

import com.software.core.network.BiliApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  private final Provider<BiliApiService> apiServiceProvider;

  private UserRepositoryImpl_Factory(Provider<BiliApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<BiliApiService> apiServiceProvider) {
    return new UserRepositoryImpl_Factory(apiServiceProvider);
  }

  public static UserRepositoryImpl newInstance(BiliApiService apiService) {
    return new UserRepositoryImpl(apiService);
  }
}
