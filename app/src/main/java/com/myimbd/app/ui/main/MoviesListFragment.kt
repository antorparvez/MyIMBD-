package com.myimbd.app.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myimbd.app.base.BaseFragment
import com.myimbd.app.databinding.FragmentMoviesListBinding
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesListFragment :
    BaseFragment<FragmentMoviesListBinding>(FragmentMoviesListBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeBaseViewModel(viewModel)

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies)
        }

        viewModel.loadMovies()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            onMovieClick = { movie ->
                // TODO: Navigate to details fragment/activity
            },
            onWishlistClick = { movie, wishlistButton ->
                // TODO: Handle wishlist click
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (!rv.canScrollVertically(1)) {
                    viewModel.loadMovies()
                }
            }
        })
    }

    override fun handleLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
