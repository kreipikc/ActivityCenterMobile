package com.kreipikc.activitycenter.presentation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kreipikc.activitycenter.R
import com.kreipikc.activitycenter.domain.model.AppUsageInfo
import com.kreipikc.activitycenter.domain.utils.TimeFormatter
import com.kreipikc.activitycenter.presentation.ui.appdetail.StatActivity

class StatsAdapter(private var stats: List<AppUsageInfo>, private var context: Context) : RecyclerView.Adapter<StatsAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val appIcon: ImageView = view.findViewById<ImageView>(R.id.appIcon)
        val nameItem: TextView = view.findViewById<TextView>(R.id.nameItem)
        val allTimeItem: TextView = view.findViewById<TextView>(R.id.allTimeItem)
        val lastTimeItem: TextView = view.findViewById<TextView>(R.id.lastTimeItem)
        val btnDetailsButton: Button = view.findViewById<Button>(R.id.detailsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stats.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameItem.text = stats[position].appName

        val allTime = TimeFormatter.formatDetailedTime(stats[position].usageTime)
        val allTimeText = context.getString(R.string.total_time, allTime)
        holder.allTimeItem.text = allTimeText

        val useLastTime = TimeFormatter.formatLastUsed(stats[position].lastUsedTime)
        val lastTimeText = context.getString(R.string.last_time, useLastTime)
        holder.lastTimeItem.text = lastTimeText

        holder.btnDetailsButton.setOnClickListener {
            val intent = Intent(context, StatActivity::class.java)

            intent.putExtra("appName", stats[position].appName)
            intent.putExtra("usageTime", allTime)
            intent.putExtra("lastUsedTime", useLastTime)
            intent.putExtra("packageName", stats[position].packageName)

            context.startActivity(intent)
        }
    }
}