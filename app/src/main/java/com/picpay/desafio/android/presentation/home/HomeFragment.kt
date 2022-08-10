package com.picpay.desafio.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.picpay.desafio.android.R
import com.picpay.desafio.android.databinding.FragmentHomeBinding
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.presentation.home.adapter.UserListAdapter
import com.picpay.desafio.android.utils.EspressoIdlingResource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModel<HomeFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater).apply {
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            fetchUserList()
        }
        initUi()
        initObservers()
    }

    private fun initUi() {
        setUpRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.getUsers()
        }
    }

    private fun initObservers() {
        homeViewModel.stateFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleState(it) }
            .launchIn(lifecycleScope)

        homeViewModel.eventFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleEvent(it) }
            .launchIn(lifecycleScope)

    }

    private fun fetchUserList() {
        EspressoIdlingResource.increment()
        homeViewModel.getUsers()
    }

    private fun handleState(state: UserListState) {
        when (state) {
            is UserListState.Success ->
                handleUserList(state.data)
            is UserListState.IsLoading ->
                handleLoading(state.isLoading)
            is UserListState.Empty ->
                Unit
        }
    }

    private fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Error -> {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUserList(userList: List<User>) {
        EspressoIdlingResource.decrement()
        binding.recyclerView.adapter?.let { adapter ->
            if(adapter is UserListAdapter){
                adapter.updateList(userList)
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.userListProgressBar.visibility = View.VISIBLE
            binding.swipeRefreshLayout.visibility = View.GONE
        } else {
            binding.userListProgressBar.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.VISIBLE

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setUpRecyclerView() {
        val a = UserListAdapter(listOf())
        binding.recyclerView.apply {
            adapter = a
            layoutManager = LinearLayoutManager(context)
        }
    }

}