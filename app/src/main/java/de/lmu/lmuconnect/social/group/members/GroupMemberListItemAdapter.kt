package de.lmu.lmuconnect.social.group.members

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.social.profile.SocialProfileOthersActivity

class GroupMemberListItemAdapter(idList: ArrayList<String>, private val activity: GroupMemberListActivity) :
    RecyclerView.Adapter<GroupMemberListItemAdapter.ViewHolder>(), Filterable {

    private var finalList : ArrayList<String> = idList

    // You can grab a room from the session
    // If the room is not known (not received from sync) it will return null

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val memberProfile : ImageView = itemView.findViewById(R.id.group_member_item_image)
        val memberName : TextView = itemView.findViewById(R.id.group_member_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ui_group_member_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return finalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = finalList[position]

        holder.memberName.text = currentItem
        holder.memberProfile.setImageResource(R.drawable.app_profile)

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, SocialProfileOthersActivity::class.java)
            intent.putExtra("name", currentItem)
            activity.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}