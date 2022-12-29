package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var shouldFail = false

    // Create a fake data source to act as a double to the real data source
    var reminderList = mutableListOf<ReminderDTO>()

    fun setShouldFail(shouldFail: Boolean) {
        this.shouldFail = shouldFail
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (shouldFail) throw Exception("Error getting reminders")
            Result.Success(reminderList)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        if (shouldFail) throw Exception("Error saving reminder")
        reminderList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldFail) throw Exception("Error getting reminder")
        return Result.Success(reminderList.first { it.id == id })
    }

    override suspend fun deleteAllReminders() {
        if (shouldFail) throw Exception("Error deleting reminders")
        reminderList.clear()
    }
}