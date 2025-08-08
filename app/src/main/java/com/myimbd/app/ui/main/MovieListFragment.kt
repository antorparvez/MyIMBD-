package com.myimbd.app.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.myimbd.app.R
import com.myimbd.app.databinding.FragmentMovieListBinding
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupChips()
        setupClickListeners()
        setupObservers()
        setupPagination()
        
        // Load initial data
        viewModel.loadMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                // Navigate to details
                (activity as? MainActivity)?.navigateToMovieDetails(movie.id.toString())
            },
            onWishlistClick = { movie ->
                viewModel.toggleWishlist(movie)
            }
        )

        binding.moviesRecyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        outRect.top = 8
                        outRect.bottom = 8
                        outRect.left = 8
                        outRect.right = 8
                    }
                }
            })
        }
    }

    private fun setupChips() {
        binding.genreChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChip = checkedIds.firstOrNull()?.let { group.findViewById<Chip>(it) }
            when (selectedChip?.id) {
                R.id.chipAll -> viewModel.filterByGenre(null)
                R.id.chipAction -> viewModel.filterByGenre("Action")
                R.id.chipDrama -> viewModel.filterByGenre("Drama")
                R.id.chipComedy -> viewModel.filterByGenre("Comedy")
                R.id.chipHorror -> viewModel.filterByGenre("Horror")
                R.id.chipThriller -> viewModel.filterByGenre("Thriller")
                R.id.chipAdventure -> viewModel.filterByGenre("Adventure")
                R.id.chipAnimation -> viewModel.filterByGenre("Animation")
            }
        }
    }

    private fun setupClickListeners() {
        binding.viewToggleButton.setOnClickListener {
            viewModel.toggleViewType()
        }
    }

    private fun setupPagination() {
        binding.moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    
                    // Check if we should load more based on position
                    if (viewModel.shouldLoadMore(lastVisibleItemPosition)) {
                        viewModel.loadMoreMovies()
                    }
                } else if (layoutManager is GridLayoutManager) {
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    
                    // Check if we should load more based on position
                    if (viewModel.shouldLoadMore(lastVisibleItemPosition)) {
                        viewModel.loadMoreMovies()
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies.toMutableList())
            updateEmptyState(movies.isEmpty())
            updateResultsText(movies.size)
        }

        viewModel.currentViewType.observe(viewLifecycleOwner) { viewType ->
            updateViewType(viewType)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingLayout.isVisible = isLoading && viewModel.movies.value?.isEmpty() == true
            binding.paginationLoadingLayout.isVisible = isLoading && viewModel.movies.value?.isNotEmpty() == true
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message (you can implement a snackbar or toast)
            }
        }

        viewModel.hasMoreData.observe(viewLifecycleOwner) { hasMore ->
            // You can show/hide a "Load More" button or handle pagination UI here
        }
    }

    private fun updateViewType(viewType: ViewType) {
        val layoutManager = when (viewType) {
            ViewType.LIST -> LinearLayoutManager(requireContext())
            ViewType.GRID -> GridLayoutManager(requireContext(), 2)
        }
        
        binding.moviesRecyclerView.layoutManager = layoutManager
        movieAdapter.setViewType(viewType)
        
        // Update icon
        val iconRes = if (viewType == ViewType.LIST) {
            R.drawable.ic_view_grid
        } else {
            R.drawable.ic_view_list
        }
        binding.viewToggleButton.setIconResource(iconRes)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.isVisible = isEmpty && !viewModel.isLoading.value!!
    }

    private fun updateResultsText(count: Int) {
        binding.resultsText.text = "Movies ($count)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
