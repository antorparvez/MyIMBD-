package com.myimbd.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Observe common loading and error LiveData from ViewModel.
     */
    open fun <T : BaseViewModel> observeBaseViewModel(viewModel: T) {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoadingState(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showError(it)
                viewModel.setError(null)
            }
        }
    }

    /**
     * Override this in child fragment to show/hide progress UI.
     */
    open fun handleLoadingState(isLoading: Boolean) {
        // Example: Override in child to show/hide progressBar
    }

    /**
     * Show error using Snackbar. Override to customize.
     */
    open fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
