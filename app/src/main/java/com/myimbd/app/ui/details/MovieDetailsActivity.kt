package com.myimbd.app.ui.details

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.util.TypedValue
import com.myimbd.app.R
import com.myimbd.app.databinding.ActivityMovieDetailsBinding
import com.myimbd.app.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure status bar uses primary color on this screen
        val typedValue = TypedValue()
        theme.resolveAttribute(R.color.primary_color, typedValue, true)
        window.statusBarColor = typedValue.data

        setupToolbar()
        setupObservers()

        val movieId = intent.getIntExtra("movie_id", -1)
        if (movieId != -1) {
            viewModel.loadMovie(movieId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupObservers() {
        viewModel.movie.observe(this) { movie ->
            movie?.let {
                binding.apply {
                    movieTitle.text = it.title
                    movieYear.text = it.year
                    movieDirector.text = it.director ?: "N/A"
                    movieActors.text = it.actors ?: "N/A"
                    moviePlot.text = it.plot ?: "N/A"
                    movieRuntime.text = it.runtime ?: "N/A"
                    movieGenres.text = it.genres.joinToString(", ")

                    Glide.with(this@MovieDetailsActivity)
                        .load(it.posterUrl)
                        .placeholder(R.drawable.ic_movie)
                        .into(moviePoster)

                    wishlistButton.setImageResource(
                        if (it.isWishlisted) R.drawable.ic_wishlist_filled
                        else R.drawable.ic_wishlist
                    )

                    wishlistButton.setOnClickListener {
                        viewModel.toggleWishlist(movie.id)
                    }
                }
            }
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