package com.myimbd.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myimbd.app.base.BaseFragment
import com.myimbd.app.databinding.FragmentMoviesListBinding
import com.myimbd.app.ui.details.MovieDetailsActivity
import com.myimbd.app.ui.main.adapter.HeaderAdapter
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.view.ViewGroup

@AndroidEntryPoint
class MoviesListFragment :
    BaseFragment<FragmentMoviesListBinding>(FragmentMoviesListBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var headerAdapter: HeaderAdapter
    private var currentViewType: ViewType = ViewType.LIST

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupRecyclerView()
        observeViewModel()

        binding.toggleBttn.setOnClickListener {
            currentViewType = if (currentViewType == ViewType.LIST) ViewType.GRID else ViewType.LIST
            movieAdapter.setViewType(currentViewType)
            binding.recyclerView.layoutManager = getLayoutManager(currentViewType)
        }

        viewModel.loadMovies()
    }

    private fun setupAdapters() {
        movieAdapter = MovieAdapter(
            currentViewType = currentViewType,
            onMovieClick = { movie ->
                val intent = Intent(requireContext(), MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            },
            onWishlistClick = { movie, imageView ->
                viewModel.toggleWishlist(movie)
            }
        )

        headerAdapter = HeaderAdapter(
            onSearchQueryChanged = { query ->
                viewModel.searchMovies(query)
            },
            onGenreFilterChanged = { genre ->
                viewModel.filterByGenre(genre)
            },
            availableGenres = listOf(
                "Action",
                "Comedy",
                "Drama",
                "Horror",
                "Romance",
                "Sci-Fi",
                "Thriller"
            )
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = getLayoutManager(currentViewType)

            // Create a combined adapter with header and movies
            val combinedAdapter = CombinedAdapter(headerAdapter, movieAdapter)
            adapter = combinedAdapter

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
            movieAdapter.submitList(movies.toList())
        }
    }

    override fun handleLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Combined adapter to show header + movies
    private inner class CombinedAdapter(
        private val headerAdapter: HeaderAdapter,
        private val movieAdapter: MovieAdapter
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) TYPE_HEADER else TYPE_MOVIE
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_HEADER -> headerAdapter.onCreateViewHolder(parent, viewType)
                TYPE_MOVIE -> movieAdapter.onCreateViewHolder(
                    parent,
                    movieAdapter.currentViewType.ordinal
                )

                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                TYPE_HEADER -> headerAdapter.onBindViewHolder(
                    holder as HeaderAdapter.HeaderViewHolder,
                    0
                )

                TYPE_MOVIE -> movieAdapter.onBindViewHolder(holder, position - 1)
            }
        }

        override fun getItemCount(): Int {
            return 1 + movieAdapter.itemCount // Header + movies
        }
    }
}

private const val TYPE_HEADER = 0
private const val TYPE_MOVIE = 1