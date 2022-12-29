package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
// This fixes: java.lang.IllegalArgumentException: failed to configure : Package targetSdkVersion=30 > maxSdkVersion=29
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {
    // provide testing to the RemindersListViewModel and its live data objects

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        repository = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), repository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getReminders_success() {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        repository.setShouldFail(false)
        viewModel.loadReminders()

        // Then assert that the progress indicator is shown.
        MatcherAssert.assertThat(viewModel.showLoading.value, Matchers.`is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        MatcherAssert.assertThat(viewModel.showLoading.value, Matchers.`is`(false))

        // Then assert that an error message is not shown.
        MatcherAssert.assertThat(viewModel.showSnackBar.value.isNullOrEmpty(), Matchers.`is`(true))
    }

    @Test
    fun getReminders_failure() {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        repository.setShouldFail(true)
        viewModel.loadReminders()

        // Then assert that the progress indicator is shown.
        MatcherAssert.assertThat(viewModel.showLoading.value, Matchers.`is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        MatcherAssert.assertThat(viewModel.showLoading.value, Matchers.`is`(false))

        // Then assert that the error message is shown.
        MatcherAssert.assertThat(viewModel.showSnackBar.value.isNullOrEmpty(), Matchers.`is`(false))
    }
}