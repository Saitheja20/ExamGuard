package com.saitheja.examguard.focus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class WhitelistRecyclerAdapter(
    private var items: List<AppInfo>,
    private val onCheckedChange: (String, Boolean) -> Unit
) : RecyclerView.Adapter<WhitelistRecyclerAdapter.ViewHolder>() {

    fun update(newItems: List<AppInfo>) {
        val oldItems = items
        val diff = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldItems.size

                override fun getNewListSize(): Int = newItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldItems[oldItemPosition].packageName == newItems[newItemPosition].packageName
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldItems[oldItemPosition] == newItems[newItemPosition]
                }
            }
        )
        items = newItems
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.checkedText.isChecked = item.selected
        holder.checkedText.text = item.appLabel
        holder.itemView.setOnClickListener {
            val next = !holder.checkedText.isChecked
            holder.checkedText.isChecked = next
            onCheckedChange(item.packageName, next)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkedText: CheckedTextView = view.findViewById(android.R.id.text1)
    }
}
