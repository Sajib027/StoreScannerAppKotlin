package com.kodakalaris.advisor.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.InprogressOrCompletedActivity
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.model.EventInfoApiResponse
import com.kodakalaris.advisor.model.RequestUpdateEventApi
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.formatDateTimeToAppFormat
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.getEventTitle
import com.kodakalaris.advisor.utils.Helper.getUserName2Char
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/*
* Other users inprogress cards adapter
* */
class InProgressDoneEventsAdapter(context: Context, eventsBeanList: List<EventBean>?) :
    RecyclerView.Adapter<InProgressDoneEventsAdapter.ViewHolder?>() {

    private val eventsBeanList: MutableList<EventBean>?
    private val context: Context
    private var selectedPosition = -1
    var language: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inprogress_completed_events, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event: EventBean = eventsBeanList!![position]
        holder.txt_event_title.text = getEventTitle(context, event)
        holder.txt_event_completedtime.text =
            formatDateTimeToAppFormat(context, event.ResponseDateTime)
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
        if (event.Status.equals(Constants.EVENT_STATUS.COMPLETED, ignoreCase = true)) {
            holder.btn_take_event.visibility = View.GONE
        } else holder.btn_take_event.visibility = View.VISIBLE
        if (event.Responder != null) {
            holder.tvUserName.text = getUserName2Char(event.Responder)
        } else {
            holder.tvUserName.text = ""
        }
        holder.btn_take_event.tag = position
        holder.btn_take_event.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val pos = view.tag as Int
                val Responder =
                    getSharedPreferenceString(context, SharedPreferenceHelper.KEY_RESPONDER, null)
                val event: EventBean = eventsBeanList[pos]
                selectedPosition = pos
                run {
                    val activity = context as InprogressOrCompletedActivity
                    acceptEvent(event.EventId!!)
                }
            }
        })
    }

    /**
     * viewholder for the card of other's inprogress event
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val txt_event_title: TextView
        internal val txt_event_description: TextView
        internal val txt_event_completedtime: TextView
        internal val tvUserName: TextView
        internal val btn_take_event: Button

        /**
         * initializing the view's variables
         * @param view viewholder main view
         */
        init {
            txt_event_title = view.findViewById(R.id.txt_event_title)
            txt_event_description = view.findViewById(R.id.txt_event_description)
            txt_event_completedtime = view.findViewById(R.id.txt_event_completedtime)
            tvUserName = view.findViewById(R.id.tvUserName)
            btn_take_event = view.findViewById(R.id.btn_take_event)
        }
    }

    /**
     * acceptevent api to pick event from your colleague
     * @param EventId the id of the event which need to pick
     */
    private fun acceptEvent(EventId: String) {
        val responderId =
            getSharedPreferenceString(context, SharedPreferenceHelper.KEY_DEVICEID, null)
        val params = RequestUpdateEventApi(Constants.EVENT_STATUS.BEING_WORKED_ON, responderId!!)
        //Add request to logger
        generateLogsOnStorage("Update Event Request")
        AppController.service!!.updateEvent(EventId, params)
            ?.enqueue(object : Callback<EventInfoApiResponse?> {
                override fun onResponse(
                    call: Call<EventInfoApiResponse?>,
                    response: Response<EventInfoApiResponse?>
                ) {
                    if (response.code() == 200 && response.body() != null && response.body()!!.Error == 0 && response.body()!!.Event != null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_allocation_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(Constants.APP.TAG, "" + response.body()!!.Event)
                        if (selectedPosition >= 0) {
                            eventsBeanList!!.removeAt(selectedPosition)
                            notifyDataSetChanged()
                        }
                        val activity = context as InprogressOrCompletedActivity
                        if (eventsBeanList!!.size == 0) {
                            activity.setEmptyTExt()
                        }
                        activity.mSuccess = 1
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_common),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<EventInfoApiResponse?>, t: Throwable) {
                }
            })
    }

    companion object {
        private val TAG = InProgressDoneEventsAdapter::class.java.simpleName
    }

    init {
        this.eventsBeanList = eventsBeanList as MutableList<EventBean>?
        this.context = context
        language = Locale.getDefault().displayLanguage
    }

    override fun getItemCount(): Int {
        return eventsBeanList?.size ?: 0
    }
}
