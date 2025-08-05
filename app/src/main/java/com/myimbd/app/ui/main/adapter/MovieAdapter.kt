package com.myimbd.app.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myimbd.app.R
import com.myimbd.app.databinding.ItemMovieBinding
import com.myimbd.domain.model.MovieDomainEntity

/**
 * Adapter for displaying movies in RecyclerView
 */
class MovieAdapter(
    private val onMovieClick: (MovieDomainEntity) -> Unit,
    private val onWishlistClick: (MovieDomainEntity, android.widget.ImageView) -> Unit
) : ListAdapter<MovieDomainEntity, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick, onWishlistClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding,
        private val onMovieClick: (MovieDomainEntity) -> Unit,
        private val onWishlistClick: (MovieDomainEntity, android.widget.ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDomainEntity) {
            binding.apply {
                movieTitle.text = movie.title
                movieYear.text = movie.year
                movieDirector.text = movie.director ?: ""
                // movieGenre.text = movie.genre.joinToString(", ")

                // Load movie poster
                Glide.with(moviePoster)
                    .load(movie.poster)
                    .placeholder(R.drawable.ic_movie)
                    .error(R.drawable.ic_movie)
                    .into(moviePoster)

                // Set wishlist button state
                wishlistButton.setImageResource(
                    if (movie.isWishlisted) R.drawable.ic_wishlist_filled
                    else R.drawable.ic_wishlist
                )

                // Set click listeners
                root.setOnClickListener { onMovieClick(movie) }
                wishlistButton.setOnClickListener { onWishlistClick(movie, wishlistButton) }
            }
        }
    }

    private class MovieDiffCallback : DiffUtil.ItemCallback<MovieDomainEntity>() {
        override fun areItemsTheSame(
            oldItem: MovieDomainEntity,
            newItem: MovieDomainEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MovieDomainEntity,
            newItem: MovieDomainEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
} 