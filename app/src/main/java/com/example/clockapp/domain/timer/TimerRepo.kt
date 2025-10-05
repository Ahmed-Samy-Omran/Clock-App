package com.example.clockapp.domain.timer

import kotlinx.coroutines.flow.Flow

interface TimerRepo {
  //Since writing to DataStore takes time (I/O), it must be done in a Coroutine.
  suspend fun saveLastTotalTime(totalTime: Long)
  fun getLastTotalTime(): Flow<Long> // i think it should be Flow<Long> because we are observing the changes in the total time in the TimerViewModel  not just getting the value once.

}