package com.kodakalaris.advisor.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.formatDateTimeToAppFormat
import com.kodakalaris.advisor.utils.Helper.getEventTitle

/**
 * my completed events adapter
 */
class MyCompletedEventsAdapter(context: Context, eventsBeanList: List<EventBean>?) :
    RecyclerView.Adapter<MyCompletedEventsAdapter.ViewHolder?>() {

    private val eventsBeanList: List<EventBean>?
    private val context: Context

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_my_completed_events, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val event: EventBean = eventsBeanList!![position]
        holder.txt_event_title.text = getEventTitle(context, event)
        holder.txt_event_completedtime.text =
            formatDateTimeToAppFormat(context, event.CompletionDateTime)
        if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_HELP, ignoreCase = true)) {
            if (event.KPEXId != null) {
                holder.txt_event_description.setText(
                    event.KPEXId.toString() + ": " + context.resources.getString(
                        R.string.help_event_desc
                    )
                )
            } else {
                holder.txt_event_description.text =
                    context.resources.getString(R.string.help_event_desc)
            }
        } else if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_ERROR, ignoreCase = true)) {
            holder.txt_event_description.text =
                if (event.NotificationText == null) context.resources.getString(R.string.no_description_avail) else event.NotificationText
        } else if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE, ignoreCase = true)) {
            holder.txt_event_description.text =
                if (event.NotificationText == null) context.resources.getString(R.string.no_description_avail) else event.NotificationText
        }
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        internal val txt_event_title: TextView
        internal val txt_event_description: TextView
        internal val txt_event_completedtime: TextView

        /**
         * initializing all the view variable
         * @param view
         */
        init {
            txt_event_title = view.findViewById(R.id.txt_event_title)
            txt_event_description = view.findViewById(R.id.txt_event_description)
            txt_event_completedtime = view.findViewById(R.id.txt_event_completedtime)
        }
    }

    companion object {
        private val TAG = MyCompletedEventsAdapter::class.java.simpleName
    }

    init {
        this.eventsBeanList = eventsBeanList
        this.context = context
    }

    override fun getItemCount(): Int {
        return eventsBeanList?.size ?: 0
    }
}
