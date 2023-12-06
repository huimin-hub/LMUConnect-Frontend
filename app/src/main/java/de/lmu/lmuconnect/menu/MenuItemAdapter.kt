package de.lmu.lmuconnect.menu

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.menu.data.MenuItem

class MenuItemAdapter(private val itemList : ArrayList<MenuItem>, private val context: Fragment) :
    RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon : ImageButton = itemView.findViewById(R.id.img_menu_item_icon)
        val itemBottomName : TextView = itemView.findViewById(R.id.tv_menu_item_name_main)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ui_menu_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        if (currentItem.itemIconUrl.startsWith("icon:")) {
            val drawableName = "menu_icon_" + currentItem.itemIconUrl.replaceFirst("icon:", "")
            val drawableResId = holder.itemView.context.resources.getIdentifier(drawableName, "drawable", holder.itemView.context.packageName)
            holder.itemIcon.setImageResource(drawableResId)
        } else Picasso.get().load(currentItem.itemIconUrl).into(holder.itemIcon)
        holder.itemBottomName.text = currentItem.itemName

        // Open url in browser
        holder.itemIcon.setOnClickListener {
            val url = currentItem.itemLink
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}