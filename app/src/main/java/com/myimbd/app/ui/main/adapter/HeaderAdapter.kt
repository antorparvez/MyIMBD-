package com.myimbd.app.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myimbd.app.databinding.ItemHeaderBinding

class HeaderAdapter(
    private val onSearchQueryChanged: (String) -> Unit,
    private val onGenreFilterChanged: (String?) -> Unit,
    private val availableGenres: List<String>
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.searchEditText.setOnEditorActionListener { _, _, _ ->
                val query = binding.searchEditText.text.toString()
                onSearchQueryChanged(query)
                true
            }

            // Setup genre filter spinner
            val genres = listOf("All Genres") + availableGenres
            val adapter = android.widget.ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                genres
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.genreSpinner.adapter = adapter

            binding.genreSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    val selectedGenre = if (position == 0) null else genres[position]
                    onGenreFilterChanged(selectedGenre)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                    onGenreFilterChanged(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1
}
