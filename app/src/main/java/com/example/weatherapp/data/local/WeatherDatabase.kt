package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.favorite.*


@Database(
    entities = [FavoriteLocation::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun favoriteDao(): FavoriteDao
}

