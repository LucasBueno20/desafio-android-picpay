package com.picpay.desafio.android.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import app.cash.turbine.Event
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.picpay.desafio.android.data.local.UserDao
import com.picpay.desafio.android.data.repository.UserListRepositoryImpl
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.presentation.home.HomeFragmentViewModel
import com.picpay.desafio.android.presentation.home.UiEvent
import com.picpay.desafio.android.presentation.home.UserListState
import com.picpay.desafio.android.userMock
import com.picpay.desafio.android.utils.Resource
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.Exception


@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class HomeFragmentViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeFragmentViewModel

    @MockK
    private lateinit var useCase: GetUsersUseCase


    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        MockKAnnotations.init(this)

        viewModel = HomeFragmentViewModel(useCase)
    }

    @Test
    fun `verify is loading when view model is called`() = runBlocking {
        coEvery { useCase.invoke() } returns flow {}

        viewModel.getUsers()

        viewModel.stateFlow.test {
            assertThat(awaitItem()).isEqualTo(UserListState.IsLoading(true))
        }
    }

    @Test
    fun `verify get user list when result success`() = runBlocking {
        coEvery { useCase.invoke() } returns flow { emit(Resource.Success(listOf(userMock))) }

        viewModel.getUsers()

        viewModel.stateFlow.test {
            assertThat(awaitItem()).isEqualTo(UserListState.Success(listOf(userMock)))
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `verify get user list when result error`() = runBlocking {
        Dispatchers.setMain(Dispatchers.IO)

        coEvery { useCase.invoke() } returns flow { emit(Resource.Error("error", null)) }

        viewModel.getUsers()

        viewModel.eventFlow.test {
            assertThat(awaitEvent()).isEqualTo(Event.Item(UiEvent.Error("error")))
            cancelAndConsumeRemainingEvents()
        }
    }


}