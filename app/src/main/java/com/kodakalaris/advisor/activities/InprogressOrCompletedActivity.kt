package com.kodakalaris.advisor.activities

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.adapters.InProgressDoneEventsAdapter
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.eventbus.AppEvents.OnNotificationEvent
import com.kodakalaris.advisor.model.APIError
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.model.GetEventsApiResponse
import com.kodakalaris.advisor.utils.APIErrorUtil.parseError
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.Helper.tonePhone
import com.kodakalaris.advisor.utils.Helper.vibratePhone
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.stream.Collectors

/*
* InprogressOrCompleted activity screen shows the list of the events occupy by other users of the same kiosk
* */
class InprogressOrCompletedActivity : BaseActivity(), View.OnClickListener {

    private lateinit var btn_back: ImageButton
    private lateinit var events: MutableList<EventBean>
    private lateinit var progressDoneEventsAdapter: InProgressDoneEventsAdapter
    private lateinit var filteredOthersInprogressOrCompletedEventsList: MutableList<EventBean>
    private lateinit var mRecyclerViewEvents: RecyclerView
    private lateinit var mTvEmpty: TextView
    var mSuccess = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inprogress_or_completed)
        initView()
        initListener()
        fetchEvents()
    }

    /**
     * initializes the views
     */
    private fun initView() {
        btn_back = findViewById(R.id.btn_back)
        mRecyclerViewEvents = findViewById(R.id.recycler_inprogress_done_events_history)
        mTvEmpty = findViewById(R.id.txt_inprog_doneevent_history_notavail)
        filteredOthersInprogressOrCompletedEventsList = ArrayList<EventBean>()
        events = ArrayList<EventBean>()
    }

    /**
     * bind the listeners
     */
    private fun initListener() {
        btn_back.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.btn_back -> {
                onBackPressed()
            }
        }
    }

    /**
     * fetch the events via getallevents api, then filter the other's inprogress events/assignments
     */
    private fun fetchEvents() {
        if (!isConnectedToInternet(this@InprogressOrCompletedActivity)) {
            showPrompt(
                this@InprogressOrCompletedActivity,
                resources.getString(R.string.error_no_internet_connection)
            )
            return
        }
        val storeId = getSharedPreferenceString(
            this@InprogressOrCompletedActivity,
            SharedPreferenceHelper.KEY_STOREID,
            null
        )
        //Add request to logger
        generateLogsOnStorage("Gell All Event Request")
        AppController.service!!.getEvents(
            storeId,
            Constants.EVENT_STATE.EVENT_STATE_ALL,
            null,
            null,
            null
        )?.enqueue(object : Callback<GetEventsApiResponse?> {
            override fun onResponse(
                call: Call<GetEventsApiResponse?>,
                response: Response<GetEventsApiResponse?>
            ) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body()!!.Error == 0 && response.body()!!.Events != null && response.body()!!.Events!!.size > 0) {
                        filterEvents(response.body()!!.Events!!)
                    } else setEmptyTExt()
                } else {
                    val error: APIError = parseError(response)
                    setEmptyTExt()
                    Log.d("error message", error.Message!!)
                }
            }

            override fun onFailure(call: Call<GetEventsApiResponse?>, t: Throwable) {
                Log.d(Constants.APP.TAG, t.localizedMessage!!)
                setEmptyTExt()
            }
        })
    }

    private fun filterEvents(eventsResponse: List<EventBean>) {
        events.clear()
        events.addAll(eventsResponse)
        updateOthersInprogressOrCompletedEvents()
    }

    private fun updateOthersInprogressOrCompletedEvents() {
        val responder = getSharedPreferenceString(
            this@InprogressOrCompletedActivity,
            SharedPreferenceHelper.KEY_RESPONDER,
            null
        ) ?: return
        val _inprogressOrCompletedEvents: List<EventBean>? = events
            .stream()
            .filter { event: EventBean -> event.Responder != null }
            ?.filter { event: EventBean ->
                !event.Responder!!.toLowerCase().contains(responder.toLowerCase())
            }
            ?.filter { event: EventBean -> event.Status!!.contains(Constants.EVENT_STATUS.BEING_WORKED_ON) }
            ?.collect(Collectors.toList<EventBean>())
        filteredOthersInprogressOrCompletedEventsList.clear()
        if (_inprogressOrCompletedEvents != null && _inprogressOrCompletedEvents.size > 0) {
            filteredOthersInprogressOrCompletedEventsList.addAll(_inprogressOrCompletedEvents)
            Collections.sort(
                filteredOthersInprogressOrCompletedEventsList,
                Comparator<EventBean?> { o1, o2 ->
                    o2!!.CreationDateTime.compareTo(o1!!.CreationDateTime)
                })
            initInProgressCompletedEvents()
            Log.e(Constants.APP.TAG, "" + filteredOthersInprogressOrCompletedEventsList.size)
        } else setEmptyTExt()
    }

    private fun initInProgressCompletedEvents() {
        progressDoneEventsAdapter = InProgressDoneEventsAdapter(
            this@InprogressOrCompletedActivity,
            filteredOthersInprogressOrCompletedEventsList
        )
        val linearLayoutManager = LinearLayoutManager(this@InprogressOrCompletedActivity)
        mRecyclerViewEvents.setLayoutManager(linearLayoutManager)
        mRecyclerViewEvents.addItemDecoration(
            DividerItemDecoration(
                this@InprogressOrCompletedActivity,
                0
            )
        )
        mRecyclerViewEvents.setAdapter(progressDoneEventsAdapter)
        setEmptyTExt()
    }

    fun setEmptyTExt() {
        if (progressDoneEventsAdapter != null) mTvEmpty.visibility =
            if (progressDoneEventsAdapter.getItemCount() == 0) View.VISIBLE else View.GONE else mTvEmpty.visibility =
            View.VISIBLE
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: OnNotificationEvent?) {
        fetchEvents()
        val vibration = getSharedPreferenceBoolean(
            this@InprogressOrCompletedActivity,
            SharedPreferenceHelper.KEY_VIBRATION,
            true
        )
        val tone = getSharedPreferenceBoolean(
            this@InprogressOrCompletedActivity,
            SharedPreferenceHelper.KEY_TONE,
            true
        )
        if (vibration && !tone) {
            vibratePhone(this@InprogressOrCompletedActivity)
        } else if (!vibration && tone) {
            tonePhone(this@InprogressOrCompletedActivity)
        } else if (vibration && tone) {
            tonePhone(this@InprogressOrCompletedActivity)
            vibratePhone(this@InprogressOrCompletedActivity)
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed();
        val intent = intent
        intent.putExtra("key", "" + mSuccess)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}