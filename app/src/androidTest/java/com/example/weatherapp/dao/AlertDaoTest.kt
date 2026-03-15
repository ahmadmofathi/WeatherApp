package com.example.weatherapp.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.alert.AlertDao
import com.example.weatherapp.data.local.alert.WeatherAlert
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: AlertDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.alertDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAlert_alertIsStoredInDatabase() = runTest {
        val alert = WeatherAlert(
            latitude = 30.0,
            longitude = 31.0,
            condition = "Rain",
            startTime = 1000L,
            endTime = 2000L
        )

        val id = dao.insertAlert(alert)

        val result = dao.getAlertById(id.toInt())
        assertNotNull(result)
        assertEquals("Rain", result!!.condition)
        assertEquals(30.0, result.latitude, 0.01)
        assertEquals(31.0, result.longitude, 0.01)
    }

    @Test
    fun deleteAlert_alertIsRemovedFromDatabase() = runTest {
        val alert = WeatherAlert(
            latitude = 30.0,
            longitude = 31.0,
            condition = "Snow",
            startTime = 1000L,
            endTime = 2000L
        )

        val id = dao.insertAlert(alert)
        val stored = dao.getAlertById(id.toInt())!!

        dao.deleteAlert(stored)

        val result = dao.getAlertById(id.toInt())
        assertNull(result)
    }

    @Test
    fun getAlerts_returnsAllInsertedAlerts() = runTest {
        dao.insertAlert(
            WeatherAlert(
                latitude = 30.0, longitude = 31.0,
                condition = "Rain", startTime = 1000L, endTime = 2000L
            )
        )
        dao.insertAlert(
            WeatherAlert(
                latitude = 40.0, longitude = 29.0,
                condition = "Snow", startTime = 3000L, endTime = 4000L
            )
        )

        val alerts = dao.getAlerts().first()

        assertEquals(2, alerts.size)
        assertTrue(alerts.any { it.condition == "Rain" })
        assertTrue(alerts.any { it.condition == "Snow" })
    }

    @Test
    fun updateAlert_isActiveStateIsUpdated() = runTest {
        val alert = WeatherAlert(
            latitude = 30.0,
            longitude = 31.0,
            condition = "Clear",
            startTime = 1000L,
            endTime = 2000L,
            isActive = true
        )

        val id = dao.insertAlert(alert)
        val stored = dao.getAlertById(id.toInt())!!

        // Deactivate
        dao.updateAlert(stored.copy(isActive = false))

        val updated = dao.getAlertById(id.toInt())
        assertNotNull(updated)
        assertFalse(updated!!.isActive)
    }

    @Test
    fun getAlertById_returnsNullForNonExistentId() = runTest {
        val result = dao.getAlertById(999)
        assertNull(result)
    }

    @Test
    fun getAlerts_emptyDatabaseReturnsEmptyList() = runTest {
        val alerts = dao.getAlerts().first()
        assertTrue(alerts.isEmpty())
    }
}
