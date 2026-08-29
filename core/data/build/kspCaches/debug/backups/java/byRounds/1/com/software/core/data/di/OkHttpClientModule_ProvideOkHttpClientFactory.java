package com.software.core.data.di;

import com.software.core.data.session.BiliSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class OkHttpClientModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<BiliSessionManager> biliSessionManagerProvider;

  private OkHttpClientModule_ProvideOkHttpClientFactory(
      Provider<BiliSessionManager> biliSessionManagerProvider) {
    this.biliSessionManagerProvider = biliSessionManagerProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(biliSessionManagerProvider.get());
  }

  public static OkHttpClientModule_ProvideOkHttpClientFactory create(
      Provider<BiliSessionManager> biliSessionManagerProvider) {
    return new OkHttpClientModule_ProvideOkHttpClientFactory(biliSessionManagerProvider);
  }

  public static OkHttpClient provideOkHttpClient(BiliSessionManager biliSessionManager) {
    return Preconditions.checkNotNullFromProvides(OkHttpClientModule.INSTANCE.provideOkHttpClient(biliSessionManager));
  }
}
