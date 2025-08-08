package com.myimbd.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.myimbd.app.R
import com.myimbd.app.databinding.ActivityMainBinding
import com.myimbd.app.ui.details.MovieDetailsActivity
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import com.myimbd.app.ui.wishlist.WishlistActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigationDrawer()
        setupClickListeners()
        setupObservers()
        setupSearchListener()
        
        // Load initial fragment
        if (savedInstanceState == null) {
            loadMovieListFragment()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MyIMBD"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupClickListeners() {
        // Menu button
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Search button
        binding.searchButton.setOnClickListener {
            showSearchBar()
        }

        // Theme toggle
        binding.themeToggleButton.setOnClickListener {
            toggleTheme()
        }

        // Wishlist button
        binding.wishlistButton.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        // Search functionality
        binding.searchBackButton.setOnClickListener {
            hideSearchBar()
        }

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.text?.clear()
            viewModel.clearSearch()
            hideSearchBar()
        }
    }

    private fun setupSearchListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.searchMovies(query)
            }
        })
    }

    private fun setupObservers() {
        viewModel.wishlistCount.observe(this) { count ->
            updateWishlistBadge(count)
        }
    }

    private fun showSearchBar() {
        binding.searchCard.visibility = View.VISIBLE
        binding.searchEditText.requestFocus()
        binding.searchCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down))
    }

    private fun hideSearchBar() {
        binding.searchCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        binding.searchCard.visibility = View.GONE
        binding.searchEditText.clearFocus()
    }

    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun updateWishlistBadge(count: Int) {
        if (count > 0) {
            binding.wishlistBadge.text = count.toString()
            binding.wishlistBadge.visibility = View.VISIBLE
        } else {
            binding.wishlistBadge.visibility = View.GONE
        }
    }

    private fun loadMovieListFragment() {
        val fragment = MovieListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToMovieDetails(movieId: String) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("movie_id", movieId)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                loadMovieListFragment()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_wishlist -> {
                startActivity(Intent(this, WishlistActivity::class.java))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_theme -> {
                toggleTheme()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_wishlist -> {
                startActivity(Intent(this, WishlistActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
