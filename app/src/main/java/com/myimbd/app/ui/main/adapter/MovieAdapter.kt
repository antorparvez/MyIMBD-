package com.myimbd.app.ui.main.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
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
    var currentViewType: ViewType = ViewType.GRID,
    private val onMovieClick: (MovieDomainEntity) -> Unit,
    private val onWishlistClick: (MovieDomainEntity, ImageView) -> Unit
) : ListAdapter<MovieDomainEntity, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    fun setViewType(viewType: ViewType) {
        currentViewType = viewType
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = currentViewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (ViewType.values()[viewType] == ViewType.LIST) {
            val binding =
                ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ListViewHolder(binding)
        } else {
            val binding =
                ItemMovieGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GridViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        when (holder) {
            is ListViewHolder -> holder.bind(movie)
            is GridViewHolder -> holder.bind(movie)
        }
    }

    inner class ListViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieDomainEntity) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = movie.year
            binding.movieDirector.text = movie.director ?: ""

            Glide.with(binding.moviePoster.context)
                .load(movie.poster)
                .placeholder(R.drawable.ic_movie)
                .into(binding.moviePoster)

            binding.wishlistButton.setImageResource(
                if (movie.isWishlisted) R.drawable.ic_wishlist_filled
                else R.drawable.ic_wishlist
            )

            binding.root.setOnClickListener { onMovieClick(movie) }
            binding.wishlistButton.setOnClickListener {
                animateWishlistButton(binding.wishlistButton)
                onWishlistClick(movie, binding.wishlistButton)
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemMovieGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieDomainEntity) {
            binding.movieTitle.text = movie.title

            Glide.with(binding.moviePoster.context)
                .load(movie.poster)
                .placeholder(R.drawable.ic_movie)
                .into(binding.moviePoster)

            binding.wishlistButton.setImageResource(
                if (movie.isWishlisted) R.drawable.ic_wishlist_filled
                else R.drawable.ic_wishlist
            )

            binding.root.setOnClickListener { onMovieClick(movie) }
            binding.wishlistButton.setOnClickListener {
                animateWishlistButton(binding.wishlistButton)
                onWishlistClick(movie, binding.wishlistButton)
            }
        }
    }

    private fun animateWishlistButton(button: ImageView) {
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f, 1f)
        val alpha = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.7f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.duration = 300
        animatorSet.start()
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<MovieDomainEntity>() {
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
