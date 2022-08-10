package com.picpay.desafio.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeFragmentViewModel(
    private val useCase: GetUsersUseCase,
): ViewModel() {

    private val _stateFlow = MutableStateFlow<UserListState>(UserListState.Empty)
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var fetchUserJob: Job? = null


    fun getUsers() {
        fetchUserJob?.cancel()
        fetchUserJob = viewModelScope.launch {
            useCase.invoke()
                .onStart {
                    setLoading(true)
                }
                .catch { e ->
                    setLoading(false)
                    _eventFlow.emit(UiEvent.Error(e.message.toString()))
                }
                .collect { result ->
                    setLoading(false)
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let {
                                displayList(data = it)
                            }
                        }
                        is Resource.Error -> {
                            val msg = result.message
                            if (msg != null) {
                                setLoading(false)
                                _eventFlow.emit(UiEvent.Error(msg))
                            }
                        }
                        is Resource.Loading -> {
                            setLoading(true)
                        }
                    }
                }
        }
    }

    private fun setLoading(boolean: Boolean) {
        _stateFlow.value = UserListState.IsLoading(boolean)
    }

    private fun displayList(data: List<User>) {
        _stateFlow.value = UserListState.Success(data)
    }

}

sealed class UserListState {
    object Empty : UserListState()
    data class Success(val data: List<User>) : UserListState()
    data class IsLoading(val isLoading: Boolean) : UserListState()
}

sealed class UiEvent {
    data class Error(val message: String) : UiEvent()
}