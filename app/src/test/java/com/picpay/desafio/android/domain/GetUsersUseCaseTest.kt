package com.picpay.desafio.android.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.picpay.desafio.android.data.repository.UserListRepositoryImpl
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.userMock
import com.picpay.desafio.android.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class GetUsersUseCaseTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var useCase: GetUsersUseCase

    @MockK
    private lateinit var repository: UserListRepositoryImpl


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        useCase = GetUsersUseCase(repository)
    }

    @Test
    fun `verify get users when result success`() = runBlocking {
        val expected = Resource.Success(listOf(userMock))

        coEvery { repository.fetchUserList() } returns flow { emit(expected) }

        val response = useCase.invoke()

        response.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verify get users when result error`() = runBlocking {
        val expected: Resource<List<User>> = Resource.Error("error", null)

        coEvery { repository.fetchUserList() } returns flow { emit(expected) }

        val response = useCase.invoke()

        response.test {
            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }

    }
}
