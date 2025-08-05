package com.myimbd.app.ui.main

import com.myimbd.app.ui.main.viewmodel.MainViewModel

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myimbd.app.R
import com.myimbd.app.databinding.ActivityMainBinding
import com.myimbd.app.ui.main.adapter.MovieAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSearchView()
        setupWishlistButton()
        viewModel.loadMovies()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        MovieAdapter(
            onMovieClick = { movie ->
                /*  val intent = Intent(this, MovieDetailsActivity::class.java)
                  intent.putExtra("movie_id", movie.id)
                  startActivity(intent)*/
            },
            onWishlistClick = { movie, wishlistButton ->
                /* viewModel.toggleWishlist(movie.id)
                 animateWishlistToHeader(wishlistButton)*/
            }
        ).also { movieAdapter = it }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.movies.observe(this) { movies ->
            movieAdapter.submitList(movies)
            binding.emptyState.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
        }

        /*     viewModel.wishlistCount.observe(this) { count ->
                 binding.wishlistBadge.text = count.toString()
                 binding.wishlistBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
             }
             */
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                // Show error message
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //viewModel.searchMovies(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // viewModel.searchMovies(newText ?: "")
                return true
            }
        })
    }

    private fun setupWishlistButton() {
        binding.wishlistButton.setOnClickListener {
            /* val intent = Intent(this, WishlistActivity::class.java)
             startActivity(intent)*/
        }
    }

    private fun animateWishlistToHeader(wishlistButton: ImageView) {
        // Create a copy of the wishlist button for animation
        val animatedView = ImageView(this).apply {
            setImageDrawable(wishlistButton.drawable)
            layoutParams = ViewGroup.LayoutParams(
                wishlistButton.width,
                wishlistButton.height
            )
        }

        // Add the animated view to the root layout
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(animatedView)

        // Get the positions
        val startLocation = IntArray(2)
        wishlistButton.getLocationInWindow(startLocation)

        val endLocation = IntArray(2)
        binding.wishlistButton.getLocationInWindow(endLocation)

        // Set initial position
        animatedView.x = startLocation[0].toFloat()
        animatedView.y = startLocation[1].toFloat()

        // Create and start the animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.wishlist_to_header)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Remove the animated view
                rootView.removeView(animatedView)

                // Add a small bounce animation to the header wishlist button
                val bounceAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.bounce)
                binding.wishlistButton.startAnimation(bounceAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        animatedView.startAnimation(animation)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        /* viewModel.genres.observe(this) { genres ->
             val genreArray = genres.toTypedArray()
             val selectedIndex = viewModel.selectedGenreIndex.value ?: 0

             MaterialAlertDialogBuilder(this)
                 .setTitle(R.string.filter_by_genre)
                 .setSingleChoiceItems(genreArray, selectedIndex) { _, which ->
                   //  viewModel.filterByGenre(which)
                 }
                 .setPositiveButton("OK") { _, _ ->
                     // Dialog will handle the selection
                 }
                 .setNegativeButton("Cancel", null)
                 .show()
         }*/
    }
} 