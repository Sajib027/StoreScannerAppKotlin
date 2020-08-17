package com.kodakalaris.advisor.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.formatDateTimeToAppFormat
import com.kodakalaris.advisor.utils.Helper.getEventTitle
import com.kodakalaris.advisor.utils.Helper.getPrinterImage

/**
 * All Events Card Adapter
 */
class AllOpenEventsAdapter(private val mContext: Context) : ArrayAdapter<EventBean?>(mContext, 0) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, contentView: View?, parent: ViewGroup): View {
        var contentView = contentView
        val holder: ViewHolder
        if (contentView == null) {
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(R.layout.adapter_all_open_events, parent, false)
            holder = ViewHolder(contentView)
            contentView.tag = holder
        } else {
            holder = contentView.tag as ViewHolder
        }
        val event: EventBean? = getItem(position)
        if (event != null) {
            holder.img_bg.setImageResource(getPrinterImage(event))
            holder.txt_view2.text = formatDateTimeToAppFormat(mContext, event.CreationDateTime)
            if (event.Type.equals(Constants.EVENT_TYPE.EVENT_TYPE_HELP, ignoreCase = true)) {
                holder.txt_title.text = getEventTitle(context, event)
                if (event.KPEXId != null) {
                    holder.txt_description.setText(
                        event.KPEXId.toString() + ": " + mContext.resources.getString(
                            R.string.help_event_desc
                        )
                    )
                } else {
                    holder.txt_description.text =
                        mContext.resources.getString(R.string.help_event_desc)
                }
            } else if (event.Type.equals(
                    Constants.EVENT_TYPE.EVENT_TYPE_ERROR,
                    ignoreCase = true
                )
            ) {
                holder.txt_title.text = getEventTitle(context, event)
                if (event.NotificationText != null && event.KPEXId != null) {
                    holder.txt_description.text =
                        if (event.NotificationText == null) mContext.resources.getString(R.string.no_description_avail) else event.NotificationText.toString() + "(" + event.KPEXId + ")"
                } else if (event.NotificationText != null && event.KPEXId == null) {
                    holder.txt_description.text =
                        if (event.NotificationText == null) mContext.resources.getString(R.string.no_description_avail) else event.NotificationText
                }
            } else if (event.Type.equals(
                    Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE,
                    ignoreCase = true
                )
            ) {
                //Manage Assemble and Error event
                holder.txt_title.text = getEventTitle(context, event)
                if (event.NotificationText != null && event.KPEXId != null) {
                    holder.txt_description.text =
                        if (event.NotificationText == null) mContext.resources.getString(R.string.no_description_avail) else event.NotificationText.toString() + "(" + event.KPEXId + ")"
                } else if (event.NotificationText != null && event.KPEXId == null) {
                    holder.txt_description.text =
                        if (event.NotificationText == null) mContext.resources.getString(R.string.no_description_avail) else event.NotificationText
                }
            }
        }
        return contentView!!
    }

    /**
     * cardview adapter's viewholder
     */
    private class ViewHolder internal constructor(view: View?) {
        val img_bg: ImageView
        val imgbtn_help: ImageButton
        val txt_view1: TextView
        val txt_view2: TextView
        val txt_title: TextView
        val txt_description: TextView

        /**
         * initializing the view's variables
         * @param view
         */
        init {
            img_bg = view!!.findViewById(R.id.img_bg)
            imgbtn_help = view.findViewById(R.id.imgbtn_help)
            txt_view1 = view.findViewById(R.id.txt_view1)
            txt_view2 = view.findViewById(R.id.txt_view2)
            txt_title = view.findViewById(R.id.txt_title)
            txt_description = view.findViewById(R.id.txt_description)
        }
    }

}