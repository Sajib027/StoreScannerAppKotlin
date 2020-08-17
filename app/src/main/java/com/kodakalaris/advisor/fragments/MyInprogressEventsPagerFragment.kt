package com.kodakalaris.advisor.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.eventbus.AppEvents.CompleteEvent
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.model.EventInfoApiResponse
import com.kodakalaris.advisor.model.RequestUpdateEventApi
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.formatDateTimeToAppFormat
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.getEventTitle
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * my inprogress event fragment for viewpager
 */
class MyInprogressEventsPagerFragment : Fragment(), View.OnClickListener {

    private lateinit var myInprogressEvents: List<EventBean>
    private lateinit var fbtn_ignore: FloatingActionButton
    private lateinit var txt_title: TextView
    private lateinit var txt_description: TextView
    private lateinit var txt_event_completedtime: TextView
    private lateinit var fbtn_tool: FloatingActionButton
    private lateinit var mActivity: HomeActivity
    // Store instance variables based on arguments passed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pager_my_inprogress_events, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txt_title = view.findViewById(R.id.txt_title)
        txt_description = view.findViewById(R.id.txt_description)
        fbtn_ignore = view.findViewById(R.id.fbtn_ignore)
        fbtn_tool = view.findViewById(R.id.fbtn_tool)
        txt_event_completedtime = view.findViewById(R.id.txt_event_completedtime)
        val myInprogressEvent: EventBean? = getArguments()!!.getParcelable("myInprogressEvent")
        if (myInprogressEvent!!.Type.equals(
                Constants.EVENT_TYPE.EVENT_TYPE_HELP,
                ignoreCase = true
            )
        ) {
            fbtn_tool.setImageResource(R.drawable.ic_user_white)
        }
        myInprogressEvents = ArrayList<EventBean>()
        txt_title.setText(getEventTitle(getContext()!!, myInprogressEvent))
        if (mActivity != null) {
            txt_event_completedtime.setText(
                formatDateTimeToAppFormat(
                    mActivity,
                    myInprogressEvent.CreationDateTime
                )
            )
            if (myInprogressEvent.Type.equals(
                    Constants.EVENT_TYPE.EVENT_TYPE_HELP,
                    ignoreCase = true
                )
            ) {
                if (myInprogressEvent.KPEXId != null) {
                    txt_description.setText(
                        myInprogressEvent.KPEXId.toString() + ": " + getActivity()!!.getResources()
                            .getString(
                                R.string.help_event_desc
                            )
                    )
                } else {
                    txt_description.setText(
                        getActivity()!!.getResources().getString(R.string.help_event_desc)
                    )
                }
            } else if (myInprogressEvent.Type.equals(
                    Constants.EVENT_TYPE.EVENT_TYPE_ERROR,
                    ignoreCase = true
                )
            ) {
                txt_description.setText(
                    if (myInprogressEvent.NotificationText == null) getActivity()!!.getResources()
                        .getString(
                            R.string.no_description_avail
                        ) else myInprogressEvent.NotificationText
                )
            } else if (myInprogressEvent.Type.equals(
                    Constants.EVENT_TYPE.EVENT_TYPE_ASSEMBLE,
                    ignoreCase = true
                )
            ) {
                txt_description.setText(
                    if (myInprogressEvent.NotificationText == null) getActivity()!!.getResources()
                        .getString(
                            R.string.no_description_avail
                        ) else myInprogressEvent.NotificationText
                )
            }
            //txt_description.setText(myInprogressEvent.NotificationText == null ? mActivity.getResources().getString(R.string.no_description_avail) : myInprogressEvent.NotificationText);
        }
        fbtn_ignore.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.fbtn_ignore -> {
                markEventCompleted()
            }
        }
    }

    /**
     * mark mine picked event as completed
     */
    private fun markEventCompleted() {
        if (!isConnectedToInternet(mActivity)) {
            showPrompt(mActivity, getResources().getString(R.string.error_no_internet_connection))
            return
        }
        val myInprogressEvent: EventBean? = getArguments()!!.getParcelable("myInprogressEvent")
        val position: Int = getArguments()!!.getInt("position")
        val responderId = SharedPreferenceHelper.getSharedPreferenceString(
            mActivity,
            SharedPreferenceHelper.KEY_DEVICEID,
            null
        )
        val params = RequestUpdateEventApi(Constants.EVENT_STATUS.COMPLETED, responderId)
        //Add request to logger
        generateLogsOnStorage("Update Event Request")
        AppController.service!!.updateEvent(myInprogressEvent!!.EventId, params)
            ?.enqueue(object : Callback<EventInfoApiResponse?> {
                override fun onResponse(
                    call: Call<EventInfoApiResponse?>,
                    response: Response<EventInfoApiResponse?>
                ) {
                    try {
                        if (response.code() == 200 && response.body() != null && response.body()!!.Error == 0) {
                            EventBus.getDefault()
                                .post(CompleteEvent(response.body()!!.Event!!, position))
                            showPrompt(
                                mActivity,
                                mActivity.resources.getString(R.string.lb_event_marked_completed)
                            )
                        } else {
                            showPrompt(
                                mActivity,
                                getActivity()!!.getResources()
                                    .getString(R.string.lb_error_event_marked_completed)
                            )
                        }
                    } catch (nex: NullPointerException) {
                        nex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<EventInfoApiResponse?>, t: Throwable) {
                    try {
                        showPrompt(
                            mActivity,
                            getActivity()!!.getResources()
                                .getString(R.string.lb_error_event_marked_completed)
                        )
                    } catch (nex: NullPointerException) {
                        nex.printStackTrace()
                    }
                }
            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = if (context is HomeActivity) {
            context
        } else {
            throw NullPointerException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        //mActivity = null;
    }

    companion object {
        private val TAG = MyInprogressEventsPagerFragment::class.java.simpleName

        // newInstance constructor for creating fragment with arguments
        fun newInstance(
            position: Int,
            myInprogressEvent: EventBean?
        ): MyInprogressEventsPagerFragment {
            val fragmentFirst = MyInprogressEventsPagerFragment()
            val bundle = Bundle()
            bundle.putParcelable("myInprogressEvent", myInprogressEvent)
            bundle.putInt("position", position)
            fragmentFirst.setArguments(bundle)
            return fragmentFirst
        }
    }
}
