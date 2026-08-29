package com.software.core.data.di;

import android.content.Context;
import com.software.core.data.session.BiliSessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SessionModule_ProvideBiliSessionManagerFactory implements Factory<BiliSessionManager> {
  private final Provider<Context> contextProvider;

  private SessionModule_ProvideBiliSessionManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BiliSessionManager get() {
    return provideBiliSessionManager(contextProvider.get());
  }

  public static SessionModule_ProvideBiliSessionManagerFactory create(
      Provider<Context> contextProvider) {
    return new SessionModule_ProvideBiliSessionManagerFactory(contextProvider);
  }

  public static BiliSessionManager provideBiliSessionManager(Context context) {
    return Preconditions.checkNotNullFromProvides(SessionModule.INSTANCE.provideBiliSessionManager(context));
  }
}
