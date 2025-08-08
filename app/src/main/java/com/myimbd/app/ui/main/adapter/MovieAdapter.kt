package com.myimbd.app.ui.main.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myimbd.app.R
import com.myimbd.app.databinding.ItemMovieBinding
import com.myimbd.app.databinding.ItemMovieGridBinding
import com.myimbd.domain.model.MovieDomainEntity

enum class ViewType { LIST, GRID }

class MovieAdapter(
    private val onMovieClick: (MovieDomainEntity) -> Unit,
    private val onWishlistClick: (MovieDomainEntity) -> Unit,
    private val onWishlistAnimation: ((View, ViewGroup) -> Unit)? = null
) : ListAdapter<MovieDomainEntity, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    companion object {
        private const val TAG = "MovieAdapter"
    }

    var currentViewType: ViewType = ViewType.LIST
        private set

    fun setViewType(viewType: ViewType) {
        if (currentViewType != viewType) {
            currentViewType = viewType
            // Use a more efficient approach for view type changes
            val currentList = currentList.toMutableList()
            if (currentList.isNotEmpty()) {
                submitList(null) {
                    submitList(currentList)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (currentViewType == ViewType.LIST) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            ListViewHolder(ItemMovieBinding.inflate(inflater, parent, false))
        } else {
            GridViewHolder(ItemMovieGridBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        when (holder) {
            is ListViewHolder -> holder.bind(movie)
            is GridViewHolder -> holder.bind(movie)
        }
    }

    // --- ViewHolders ---
    inner class ListViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDomainEntity) {
            with(binding) {
                movieTitle.text = movie.title
                movieYear.text = movie.year
                movieDirector.text = movie.director.orEmpty()

                loadPoster(movie.posterUrl, moviePoster)
                wishlistButton.setImageResource(
                    if (movie.isWishlisted) R.drawable.ic_wishlist_filled
                    else R.drawable.ic_wishlist
                )
                
                // Set tint color based on wishlist state
                val tintColor = if (movie.isWishlisted) {
                    ContextCompat.getColor(root.context, android.R.color.holo_red_light)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.darker_gray)
                }
                wishlistButton.setColorFilter(tintColor)

                root.setOnClickListener { onMovieClick(movie) }
                wishlistButton.setOnClickListener {
                    Log.d(TAG, "Wishlist button clicked for movie: ${movie.title}")
                    animateWishlistButton(wishlistButton)
                    
                    // Check if we're adding to wishlist (not removing)
                    val isAddingToWishlist = !movie.isWishlisted
                    
                    onWishlistClick(movie)
                    
                    // Trigger the wishlist animation only when adding to wishlist
                    if (isAddingToWishlist) {
                        onWishlistAnimation?.let { animationCallback ->
                            Log.d(TAG, "Adding to wishlist - Animation callback available, finding parent view...")
                            // Find the parent view group (usually the fragment's root view)
                            val parentView = findParentViewGroup(root)
                            parentView?.let { parent ->
                                Log.d(TAG, "Parent view found: ${parent.id}, triggering animation...")
                                animationCallback(wishlistButton, parent)
                            } ?: run {
                                Log.e(TAG, "Parent view not found!")
                            }
                        } ?: run {
                            Log.w(TAG, "No animation callback provided")
                        }
                    } else {
                        Log.d(TAG, "Removing from wishlist - No animation needed")
                    }
                }
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemMovieGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDomainEntity) {
            with(binding) {
                movieTitle.text = movie.title
                loadPoster(movie.posterUrl, moviePoster)
                wishlistButton.setImageResource(
                    if (movie.isWishlisted) R.drawable.ic_wishlist_filled
                    else R.drawable.ic_wishlist
                )
                
                // Set tint color based on wishlist state
                val tintColor = if (movie.isWishlisted) {
                    ContextCompat.getColor(root.context, android.R.color.holo_red_light)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.darker_gray)
                }
                wishlistButton.setColorFilter(tintColor)

                root.setOnClickListener { onMovieClick(movie) }
                wishlistButton.setOnClickListener {
                    Log.d(TAG, "Wishlist button clicked for movie: ${movie.title}")
                    animateWishlistButton(wishlistButton)
                    
                    // Check if we're adding to wishlist (not removing)
                    val isAddingToWishlist = !movie.isWishlisted
                    
                    onWishlistClick(movie)
                    
                    // Trigger the wishlist animation only when adding to wishlist
                    if (isAddingToWishlist) {
                        onWishlistAnimation?.let { animationCallback ->
                            Log.d(TAG, "Adding to wishlist - Animation callback available, finding parent view...")
                            // Find the parent view group (usually the fragment's root view)
                            val parentView = findParentViewGroup(root)
                            parentView?.let { parent ->
                                Log.d(TAG, "Parent view found: ${parent.id}, triggering animation...")
                                animationCallback(wishlistButton, parent)
                            } ?: run {
                                Log.e(TAG, "Parent view not found!")
                            }
                        } ?: run {
                            Log.w(TAG, "No animation callback provided")
                        }
                    } else {
                        Log.d(TAG, "Removing from wishlist - No animation needed")
                    }
                }
            }
        }
    }

    // --- Helpers ---
    private fun loadPoster(url: String?, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_movie)
            .error(R.drawable.ic_movie)
            .into(imageView)
    }

    private fun animateWishlistButton(button: ImageView) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(button, "alpha", 1f, 0.7f, 1f)
            )
            duration = 300
            start()
        }
    }
    
    /**
     * Finds the parent ViewGroup that can be used for the animation
     * Prefers the root view of the activity for better visibility
     */
    private fun findParentViewGroup(view: View): ViewGroup? {
        var currentView: View? = view
        var bestParent: ViewGroup? = null
        
        while (currentView != null) {
            if (currentView is ViewGroup) {
                // Prefer the root view of the activity (usually CoordinatorLayout or DrawerLayout)
                if (currentView.id == android.R.id.content || 
                    currentView is androidx.coordinatorlayout.widget.CoordinatorLayout ||
                    currentView is androidx.drawerlayout.widget.DrawerLayout) {
                    bestParent = currentView
                    break
                }
                // Fallback to any ViewGroup that's not the content root
                if (currentView.id != android.R.id.content) {
                    bestParent = currentView
                }
            }
            currentView = currentView.parent as? View
        }
        
        Log.d(TAG, "Found parent view group: ${bestParent?.id ?: "null"}")
        return bestParent
    }

    // --- DiffUtil ---
    class MovieDiffCallback : DiffUtil.ItemCallback<MovieDomainEntity>() {
        override fun areItemsTheSame(oldItem: MovieDomainEntity, newItem: MovieDomainEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MovieDomainEntity, newItem: MovieDomainEntity) =
            oldItem == newItem
    }
}
