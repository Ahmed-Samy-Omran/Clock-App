package com.example.clockapp.di

import com.example.clockapp.data.clock.ClockRepositoryImpl
import com.example.clockapp.data.timer.TimerRepoImpl
import com.example.clockapp.domain.clock.ClockRepo
import com.example.clockapp.domain.timer.TimerRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

// bind interface to implementation
    @Binds
    @Singleton
    abstract fun bindClockRepo(
        impl:ClockRepositoryImpl
    ):ClockRepo


    @Binds
    @Singleton
    abstract fun bindTimerRepo(
        impl: TimerRepoImpl
    ):TimerRepo


}