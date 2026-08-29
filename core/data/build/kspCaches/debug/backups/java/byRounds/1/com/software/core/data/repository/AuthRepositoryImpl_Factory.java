package com.software.core.data.repository;

import com.software.core.data.session.BiliSessionManager;
import com.software.core.network.BiliLoginApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.software.core.network.BiliLoginNetwork")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<BiliLoginApiService> loginApiServiceProvider;

  private final Provider<BiliSessionManager> sessionManagerProvider;

  private AuthRepositoryImpl_Factory(Provider<BiliLoginApiService> loginApiServiceProvider,
      Provider<BiliSessionManager> sessionManagerProvider) {
    this.loginApiServiceProvider = loginApiServiceProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(loginApiServiceProvider.get(), sessionManagerProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(
      Provider<BiliLoginApiService> loginApiServiceProvider,
      Provider<BiliSessionManager> sessionManagerProvider) {
    return new AuthRepositoryImpl_Factory(loginApiServiceProvider, sessionManagerProvider);
  }

  public static AuthRepositoryImpl newInstance(BiliLoginApiService loginApiService,
      BiliSessionManager sessionManager) {
    return new AuthRepositoryImpl(loginApiService, sessionManager);
  }
}
