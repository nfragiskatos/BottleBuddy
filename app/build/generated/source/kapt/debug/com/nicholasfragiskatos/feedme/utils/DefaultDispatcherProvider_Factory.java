// Generated by Dagger (https://dagger.dev).
package com.nicholasfragiskatos.feedme.utils;

import com.nicholasfragiskatos.feedme.utils.dispatchers.DefaultDispatcherProvider;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DefaultDispatcherProvider_Factory implements Factory<DefaultDispatcherProvider> {
  @Override
  public DefaultDispatcherProvider get() {
    return newInstance();
  }

  public static DefaultDispatcherProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DefaultDispatcherProvider newInstance() {
    return new DefaultDispatcherProvider();
  }

  private static final class InstanceHolder {
    private static final DefaultDispatcherProvider_Factory INSTANCE = new DefaultDispatcherProvider_Factory();
  }
}
