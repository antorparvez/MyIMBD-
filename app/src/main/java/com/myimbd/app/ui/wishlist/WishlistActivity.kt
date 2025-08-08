package com.myimbd.app.ui.wishlist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.myimbd.app.R
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.TypedValue
import com.myimbd.app.ui.details.MovieDetailsActivity
import com.myimbd.app.ui.main.adapter.MovieAdapter
import com.myimbd.app.ui.main.adapter.ViewType
import com.myimbd.app.databinding.ActivityWishlistBinding
import com.myimbd.app.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private val viewModel: WishlistViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure status bar uses primary color on this screen
        val typedValue = TypedValue()
        theme.resolveAttribute(R.color.primary_color, typedValue, true)
        window.statusBarColor = typedValue.data

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        viewModel.loadWishlistedMovies()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Wishlist"
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            //currentViewType = ViewType.LIST,
            onMovieClick = { movie ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                startActivity(intent)
            },
            onWishlistClick = { movie,  ->
                viewModel.removeFromWishlist(movie.id)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@WishlistActivity)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.wishlistedMovies.observe(this) { movies ->
            movieAdapter.submitList(movies)
            binding.emptyState.visibility = if (movies.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
} 