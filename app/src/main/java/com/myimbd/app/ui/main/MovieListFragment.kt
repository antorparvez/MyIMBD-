package com.myimbd.app.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.myimbd.app.R
import com.myimbd.app.base.BaseFragment
import com.myimbd.app.databinding.FragmentMovieListBinding
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.app.ui.main.animation.WishlistAnimation
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class MovieListFragment : BaseFragment<FragmentMovieListBinding>(
    FragmentMovieListBinding::inflate
) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter
    private var isPaginationInProgress = false
    private var lastPaginationTime = 0L
    private val paginationDelay = 1000L // 1 second delay between pagination calls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load movies only the first time the fragment is created
        if (savedInstanceState == null) {
            viewModel.loadMovies()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChips()
        setupClickListeners()
        setupObservers()
        setupRecyclerViewPagination()
        
        // Observe base ViewModel states
        observeBaseViewModel(viewModel)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                (activity as? MainActivity)?.navigateToMovieDetails(movie.id.toString())
            },
            onWishlistClick = { movie ->
                viewModel.toggleWishlist(movie)
            },
            onWishlistAnimation = { startView, parentView ->
                Log.d("MovieListFragment", "Animation callback received")
                Log.d("MovieListFragment", "Start view: ${startView.id}, Parent view: ${parentView.id}")
                
                // Find the wishlist button in the toolbar
                val wishlistButton = activity?.findViewById<View>(R.id.wishlistButton)
                if (wishlistButton != null) {
                    Log.d("MovieListFragment", "Wishlist button found in toolbar: ${wishlistButton.id}")
                    // Start the wishlist animation using the convenience method
                    WishlistAnimation.animate(
                        context = requireContext(),
                        startView = startView,
                        endView = wishlistButton,
                        parentView = parentView
                    )
                } else {
                    Log.e("MovieListFragment", "Wishlist button not found in toolbar!")
                }
            }
        )

        // Configure RecyclerView for optimal performance
        binding.moviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.moviesRecyclerView.adapter = movieAdapter
        binding.moviesRecyclerView.setHasFixedSize(false)
        binding.moviesRecyclerView.itemAnimator = null // Disable animations for smoother scrolling

        // Add item decoration for spacing
        binding.moviesRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(8, 8, 8, 8)
            }
        })

        movieAdapter.setViewType(ViewType.LIST)
        binding.viewToggleButton.setIconResource(R.drawable.ic_view_grid)
    }

    private fun setupChips() {
       /* binding.genreChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
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
        }*/
    }

    private fun setupClickListeners() {
        binding.viewToggleButton.setOnClickListener {
            viewModel.toggleViewType()
        }
    }

    private fun setupRecyclerViewPagination() {
        binding.moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                if (dy > 0) { // Scrolling down
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    layoutManager?.let { manager ->
                        val visibleItemCount = manager.childCount
                        val totalItemCount = manager.itemCount
                        val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
                        
                        // Check if we're near the end (within 5 items)
                        val shouldLoadMore = (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 &&
                                firstVisibleItemPosition >= 0 &&
                                totalItemCount >= 0 &&
                                totalItemCount > 0
                        
                        val currentTime = System.currentTimeMillis()
                        val canLoadMore = currentTime - lastPaginationTime > paginationDelay
                        
                        if (shouldLoadMore && 
                            viewModel.hasMoreData.value == true && 
                            viewModel.isLoading.value != true &&
                            !isPaginationInProgress &&
                            canLoadMore) {
                            isPaginationInProgress = true
                            lastPaginationTime = currentTime
                            viewModel.loadMoreMovies()
                        }
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            // Use submitList with DiffUtil for smooth updates
            movieAdapter.submitList(movies.toMutableList()) {
                updateEmptyState(movies.isEmpty())
                updateResultsText(movies.size)
            }
        }

        viewModel.currentViewType.observe(viewLifecycleOwner) { viewType ->
            updateViewType(viewType)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoadingState(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                isPaginationInProgress = false
            }
        }
    }

    override fun handleLoadingState(isLoading: Boolean) {
        // Show main loading layout only when there are no movies (initial load)
        binding.loadingLayout.isVisible = isLoading && (viewModel.movies.value?.isEmpty() == true)
        
        // Show pagination loading when there are existing movies (loading more)
        binding.paginationLoadingLayout.isVisible = isLoading && (viewModel.movies.value?.isNotEmpty() == true)
        
        // Reset pagination flag when loading is complete
        if (!isLoading) {
            isPaginationInProgress = false
        }
    }

    override fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                viewModel.refreshMovies()
            }
            .show()
    }

    private fun updateViewType(viewType: ViewType) {
        if (movieAdapter.currentViewType != viewType) {
            val layoutManager = when (viewType) {
                ViewType.LIST -> LinearLayoutManager(requireContext())
                ViewType.GRID -> GridLayoutManager(requireContext(), 2)
            }
            binding.moviesRecyclerView.layoutManager = layoutManager
            movieAdapter.setViewType(viewType)

            val iconRes = if (viewType == ViewType.LIST) {
                R.drawable.ic_view_grid
            } else {
                R.drawable.ic_view_list
            }
            binding.viewToggleButton.setIconResource(iconRes)
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.isVisible = isEmpty && (viewModel.isLoading.value == false)
    }

    private fun updateResultsText(count: Int) {
        binding.resultsText.text = "Movies ($count)"
    }
}
