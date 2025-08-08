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
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.navigation.NavigationView
import com.myimbd.app.R
import com.myimbd.app.databinding.ActivityMainBinding
import com.myimbd.app.ui.details.MovieDetailsActivity
import com.myimbd.app.ui.main.viewmodel.MainViewModel
import com.myimbd.app.ui.wishlist.WishlistActivity
import com.myimbd.app.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var themeSwitch: SwitchCompat

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
        val themeMenuItem = binding.navigationView.menu.findItem(R.id.nav_theme)
        themeSwitch = SwitchCompat(this)
        themeSwitch.isChecked = ThemeManager.isDarkModeEnabled(this)
        themeMenuItem.actionView = themeSwitch
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDarkMode(this, isChecked)
            // Apply immediately and recreate this activity for full UI refresh
            delegate.applyDayNight()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            binding.drawerLayout.post { recreate() }
        }
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

        // Filter button
        binding.filterButton.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragment is MovieListFragment) {
                fragment.toggleFilterVisibility()
            }
        }


        // Wishlist button
        binding.wishlistButton.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
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
        val newValue = !ThemeManager.isDarkModeEnabled(this)
        ThemeManager.setDarkMode(this, newValue)
        delegate.applyDayNight()
        if (::themeSwitch.isInitialized) themeSwitch.isChecked = newValue
        binding.drawerLayout.post { recreate() }
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

    fun navigateToMovieDetails(movieId: Int) {
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
                if (::themeSwitch.isInitialized) {
                    themeSwitch.toggle()
                } else {
                    toggleTheme()
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

}
