// Generated by Dagger (https://dagger.dev).
package com.nicholasfragiskatos.feedme.ui.screens.home;

import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository;
import com.nicholasfragiskatos.feedme.utils.dates.DateConverter;
import com.nicholasfragiskatos.feedme.utils.dispatchers.DispatcherProvider;
import com.nicholasfragiskatos.feedme.utils.preferences.PreferenceManager;
import com.nicholasfragiskatos.feedme.utils.reports.ReportGenerator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class FeedingListScreenViewModel_Factory implements Factory<FeedingListScreenViewModel> {
  private final Provider<FeedingRepository> repositoryProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  private final Provider<ReportGenerator> reportGeneratorProvider;

  private final Provider<DateConverter> dateConverterProvider;

  private final Provider<PreferenceManager> preferenceManagerProvider;

  public FeedingListScreenViewModel_Factory(Provider<FeedingRepository> repositoryProvider,
      Provider<DispatcherProvider> dispatcherProvider,
      Provider<ReportGenerator> reportGeneratorProvider,
      Provider<DateConverter> dateConverterProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.dispatcherProvider = dispatcherProvider;
    this.reportGeneratorProvider = reportGeneratorProvider;
    this.dateConverterProvider = dateConverterProvider;
    this.preferenceManagerProvider = preferenceManagerProvider;
  }

  @Override
  public FeedingListScreenViewModel get() {
    return newInstance(repositoryProvider.get(), dispatcherProvider.get(), reportGeneratorProvider.get(), dateConverterProvider.get(), preferenceManagerProvider.get());
  }

  public static FeedingListScreenViewModel_Factory create(
      Provider<FeedingRepository> repositoryProvider,
      Provider<DispatcherProvider> dispatcherProvider,
      Provider<ReportGenerator> reportGeneratorProvider,
      Provider<DateConverter> dateConverterProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    return new FeedingListScreenViewModel_Factory(repositoryProvider, dispatcherProvider, reportGeneratorProvider, dateConverterProvider, preferenceManagerProvider);
  }

  public static FeedingListScreenViewModel newInstance(FeedingRepository repository,
      DispatcherProvider dispatcherProvider, ReportGenerator reportGenerator,
      DateConverter dateConverter, PreferenceManager preferenceManager) {
    return new FeedingListScreenViewModel(repository, dispatcherProvider, reportGenerator, dateConverter, preferenceManager);
  }
}