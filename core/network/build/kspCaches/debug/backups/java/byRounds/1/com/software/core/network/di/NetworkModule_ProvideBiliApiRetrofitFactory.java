package com.software.core.network.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideBiliApiRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Json> jsonProvider;

  private NetworkModule_ProvideBiliApiRetrofitFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Json> jsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public Retrofit get() {
    return provideBiliApiRetrofit(okHttpClientProvider.get(), jsonProvider.get());
  }

  public static NetworkModule_ProvideBiliApiRetrofitFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideBiliApiRetrofitFactory(okHttpClientProvider, jsonProvider);
  }

  public static Retrofit provideBiliApiRetrofit(OkHttpClient okHttpClient, Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideBiliApiRetrofit(okHttpClient, json));
  }
}
