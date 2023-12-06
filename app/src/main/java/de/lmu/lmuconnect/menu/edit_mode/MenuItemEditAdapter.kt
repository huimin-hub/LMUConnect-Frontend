package de.lmu.lmuconnect.menu.edit_mode

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.menu.data.MenuItem

private const val TAG = "MenuItemEditAdapter"

class MenuItemEditAdapter(private val itemList : ArrayList<MenuItem>, private val context: Context) :
    RecyclerView.Adapter<MenuItemEditAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val itemIcon : ImageButton = itemView.findViewById(R.id.img_menu_item_icon)
        val itemTitle : TextView = itemView.findViewById(R.id.tv_menu_item_name)
        val itemLink : TextView = itemView.findViewById(R.id.tv_menu_item_link)
        val plusMinusButton : ImageButton = itemView.findViewById(R.id.imgbtn_menu_edit_plusminus)
        val deleteButton : ImageView = itemView.findViewById(R.id.imgv_menu_delete_item)

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ui_menu_edit_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        if (currentItem.itemIconUrl.startsWith("icon:")) {
            val drawableName = "menu_icon_" + currentItem.itemIconUrl.replaceFirst("icon:", "")
            val drawableResId = holder.itemView.context.resources.getIdentifier(drawableName, "drawable", holder.itemView.context.packageName)
            holder.itemIcon.setImageResource(drawableResId)
        } else Picasso.get().load(currentItem.itemIconUrl).into(holder.itemIcon)
        holder.itemTitle.text = currentItem.itemName
        holder.itemLink.text = currentItem.itemLink

        if (currentItem.isFavourite) {
            holder.plusMinusButton.setImageResource(R.drawable.menu_edit_remove)
        } else {
            holder.plusMinusButton.setImageResource(R.drawable.menu_edit_add)
        }

        holder.plusMinusButton.setOnClickListener {
            if (context is MenuEditModeActivity)
                context.setFavourite(currentItem, !currentItem.isFavourite)
        }

        holder.deleteButton.setOnClickListener {
            if (context is MenuEditModeActivity)
                context.deleteLink(currentItem.itemId)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}