package de.lmu.lmuconnect.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.home.data.News

class NewsFeedAdapter(private val itemList : ArrayList<News>,  private val context: Fragment) :
    RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val newsImageView : ImageView = itemView.findViewById(R.id.imgv_news)
        val dateTextView : TextView = itemView.findViewById(R.id.tv_news_date)
        val titleTextView : TextView = itemView.findViewById(R.id.tv_news_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ui_home_news, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.dateTextView.text = currentItem.date
        holder.titleTextView.text = currentItem.title
        holder.itemView.setOnClickListener {
            val url = currentItem.link
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}