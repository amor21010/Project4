package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    // Add testing implementation to the RemindersLocalRepository.kt

    // Class under test
    private lateinit var remindersRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersRepository = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun closeDb() = database.close()

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    // Replace with runBlockingTest once issue is resolved
    @Test
    fun insertReminderAndGetById() = runBlocking  {
        // GIVEN - A new reminder saved in the database.
        val reminder = ReminderDTO("test 1", "description 1", "Test Location", 12.13282, -86.2504)
        remindersRepository.saveReminder(reminder)

        // WHEN  - Reminder retrieved by ID.
        val result = remindersRepository.getReminder(reminder.id)

        // THEN - Same reminder is returned.
        assertThat(result, CoreMatchers.notNullValue())
        assertThat(result, instanceOf(Result.Success::class.java))

        val data = (result as Result.Success).data
        assertThat(data.id, `is`(reminder.id))
        assertThat(data.title, `is`(reminder.title))
        assertThat(data.description, `is`(reminder.description))
        assertThat(data.location, `is`(reminder.location))
        assertThat(data.latitude, `is`(reminder.latitude))
        assertThat(data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun insertReminderFailed() = runBlocking {
        // GIVEN - Get a non existing reminder.
        val result = remindersRepository.getReminder("A")

        val error =  (result is Result.Error)
        val message = (result as Result.Error).message
        assertThat(error, `is`(true))
        assertThat(message, `is`("Reminder not found!"))
    }
}