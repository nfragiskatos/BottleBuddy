// Generated by Dagger (https://dagger.dev).
package com.nicholasfragiskatos.feedme.utils;

import com.nicholasfragiskatos.feedme.utils.dates.DateConverterImpl;

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
public final class DateConverterImpl_Factory implements Factory<DateConverterImpl> {
  @Override
  public DateConverterImpl get() {
    return newInstance();
  }

  public static DateConverterImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DateConverterImpl newInstance() {
    return new DateConverterImpl();
  }

  private static final class InstanceHolder {
    private static final DateConverterImpl_Factory INSTANCE = new DateConverterImpl_Factory();
  }
}