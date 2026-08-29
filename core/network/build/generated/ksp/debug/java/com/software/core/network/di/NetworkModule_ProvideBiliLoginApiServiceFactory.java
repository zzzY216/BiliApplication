package com.software.core.network.di;

import com.software.core.network.BiliLoginApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideBiliLoginApiServiceFactory implements Factory<BiliLoginApiService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_ProvideBiliLoginApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public BiliLoginApiService get() {
    return provideBiliLoginApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideBiliLoginApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideBiliLoginApiServiceFactory(retrofitProvider);
  }

  public static BiliLoginApiService provideBiliLoginApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideBiliLoginApiService(retrofit));
  }
}
