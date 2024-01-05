package com.nicholasfragiskatos.feedme.di

import com.nicholasfragiskatos.feedme.utils.dates.DateConverter
import com.nicholasfragiskatos.feedme.utils.dates.DateConverterImpl
import com.nicholasfragiskatos.feedme.utils.dispatchers.DefaultDispatcherProvider
import com.nicholasfragiskatos.feedme.utils.dispatchers.DispatcherProvider
import com.nicholasfragiskatos.feedme.utils.preferences.PreferenceManager
import com.nicholasfragiskatos.feedme.utils.preferences.PreferenceManagerImpl
import com.nicholasfragiskatos.feedme.utils.reports.ReportGenerator
import com.nicholasfragiskatos.feedme.utils.reports.ReportGeneratorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    abstract fun bindPreferenceManager(preferenceManagerImpl: PreferenceManagerImpl): PreferenceManager

    @Binds
    abstract fun bindReportGenerator(reportGeneratorImpl: ReportGeneratorImpl): ReportGenerator

    @Binds
    abstract fun bindDateConverter(dateConverterImpl: DateConverterImpl): DateConverter
}
