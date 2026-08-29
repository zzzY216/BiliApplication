package com.software.core.network.di;

import com.software.core.network.BiliAppApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideBiliAppApiServiceFactory implements Factory<BiliAppApiService> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_ProvideBiliAppApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public BiliAppApiService get() {
    return provideBiliAppApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideBiliAppApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideBiliAppApiServiceFactory(retrofitProvider);
  }

  public static BiliAppApiService provideBiliAppApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideBiliAppApiService(retrofit));
  }
}
