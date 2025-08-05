package com.myimbd.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.myimbd.app.R
import com.myimbd.app.databinding.ActivitySplashBinding
import com.myimbd.app.ui.main.MainActivity
import com.myimbd.app.ui.splash.viewmodel.SplashUiState
import com.myimbd.app.ui.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Splash activity that checks internet connectivity and loads data once
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //setupObservers()
        //checkDataAndProceed()
        navigateToMain()
    }
    
    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is SplashUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.statusText.text = getString(R.string.loading_data)
                }
                is SplashUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.statusText.text = getString(R.string.data_loaded)
                    navigateToMain()
                }
                is SplashUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.statusText.text = getString(R.string.error_loading_data)
                    // Still navigate to main as we can work offline
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(2000)
                        navigateToMain()
                    }
                }
            }
        }
    }
    
    private fun checkDataAndProceed() {
        viewModel.checkDataAndLoadIfNeeded()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
} 