/*
package com.myimbd.app.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.myimbd.app.databinding.RecyclerHeaderBinding

class HeaderAdapter(
    private val onSearchQueryChanged: (String) -> Unit,
    private val onFilterSelected: (List<String>) -> Unit,
    private val onViewToggleSelected: (ViewType) -> Unit
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {


    inner class HeaderViewHolder(val binding: RecyclerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            RecyclerHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val binding = holder.binding

        // Listen to search input changes
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            onSearchQueryChanged(text?.toString().orEmpty())
        }

        // View toggle: grid/list
        binding.viewToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.gridViewButton.id -> onViewToggleSelected(ViewType.GRID)
                    binding.listViewButton.id -> onViewToggleSelected(ViewType.LIST)
                }
            }
        }

        // Filters
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedFilters = checkedIds.mapNotNull { id ->
                group.findViewById<com.google.android.material.chip.Chip>(id)?.text?.toString()
            }
            onFilterSelected(selectedFilters)
        }

        // Optionally: voice search button
        binding.voiceSearchButton.setOnClickListener {
            // You can trigger a voice input flow from your Fragment using a callback if needed
        }
    }

    override fun getItemCount(): Int = 1
}
*/
