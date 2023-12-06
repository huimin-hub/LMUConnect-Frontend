package de.lmu.lmuconnect.menu.add_item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R

class IconItemAdapter(private val itemList: ArrayList<Int>) :
    RecyclerView.Adapter<IconItemAdapter.ViewHolder>() {

    var currentlySelectedItem: Int = itemList[0]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageButton: ImageButton = itemView as ImageButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ui_menu_item_icon, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.itemImageButton.setImageResource(currentItem)
        holder.itemImageButton.isSelected = currentItem == currentlySelectedItem
        holder.itemImageButton.setOnClickListener {
            currentlySelectedItem = currentItem
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = itemList.size
}