package com.myimbd.app.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myimbd.app.base.BaseFragment
import com.myimbd.app.databinding.FragmentMoviesListBinding
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesListFragment :
    BaseFragment<FragmentMoviesListBinding>(FragmentMoviesListBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private var currentViewType: ViewType = ViewType.LIST

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        observeViewModel()

        binding.toggleBttn.setOnClickListener {
            currentViewType = if (currentViewType == ViewType.LIST) ViewType.GRID else ViewType.LIST
            movieAdapter.setViewType(currentViewType)
            binding.recyclerView.layoutManager = getLayoutManager(currentViewType)
        }

        viewModel.loadMovies()
    }

    private fun setupAdapter() {
        movieAdapter = MovieAdapter(
            currentViewType = currentViewType,
            onMovieClick = { movie ->
                // Navigate to movie detail
            },
            onWishlistClick = { movie, imageView ->
                viewModel.toggleWishlist(movie)
                // Optionally update the UI
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = getLayoutManager(currentViewType)
            adapter = movieAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    val layoutManager = rv.layoutManager as? LinearLayoutManager ?: return
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    // Load next page when 5 items away from the end
                    if (!viewModel.isLoading() && lastVisibleItemPosition >= totalItemCount - 5) {
                        viewModel.loadMovies()
                    }
                }
            })
        }
    }

    private fun getLayoutManager(viewType: ViewType) = when (viewType) {
        ViewType.LIST -> LinearLayoutManager(requireContext())
        ViewType.GRID -> GridLayoutManager(requireContext(), 2)
    }

    private fun observeViewModel() {
        observeBaseViewModel(viewModel)

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies.toList()) // Ensure copy for diff util
        }
    }

    override fun handleLoadingState(isLoading: Boolean) {
        // Show/hide progress loader if needed
       // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
