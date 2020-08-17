package com.kodakalaris.advisor.fragments.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.custom.BadgeDrawable
import com.kodakalaris.advisor.eventbus.AppEvents.*
import com.kodakalaris.advisor.fragments.tab.AllTabFragment
import com.kodakalaris.advisor.fragments.tab.MyTabFragment
import com.kodakalaris.advisor.model.APIError
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.model.GetEventsApiResponse
import com.kodakalaris.advisor.utils.APIErrorUtil.parseError
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.getCurrentDate
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.onlyRtrnDate
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.stream.Collectors

/**
 * first fragment from the navigation drawer/side menu
 */
class AssignmentsFragment : Fragment(), View.OnClickListener {

    private lateinit var loader_event_cards: ProgressBar
    private lateinit var btn_all: TextView
    private lateinit var btn_my: TextView
    private lateinit var frame_alltab_mytab_container: FrameLayout
    private lateinit var btn_all_container: LinearLayout
    private lateinit var btn_my_container: LinearLayout
    private lateinit var btn_all_badge: ImageView
    private lateinit var btn_my_badge: ImageView
    private lateinit var eventFilterer: EventFilterer
    lateinit var Events: MutableList<EventBean>
    lateinit var filteredAllOpenEventsList: MutableList<EventBean>
    private lateinit var filteredMyEventsList: MutableList<EventBean>
    lateinit var filteredMyInprogressEventsList: MutableList<EventBean>
    lateinit var filteredMyCompletedEventsList: MutableList<EventBean>
    lateinit var filteredOthersInprogressEventsList: MutableList<EventBean>
    private lateinit var mActivity: HomeActivity

    /**
     * get alltab fragment instance
     * @return
     */
    private var allTabFragment: AllTabFragment? = null
        get() {
            if (field == null) {
                field = AllTabFragment.newInstance(this)
            }
            return field
        }

    /**
     * get mytab fragment instance
     * @return
     */
    private var myTabFragment: MyTabFragment? = null
        get() {
            if (field == null) {
                field = MyTabFragment.newInstance(this)
            }
            return field
        }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_assignments, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initListener()
        bindData()
    }

    /**
     * initialize the view/UI
     * @param view
     */
    private fun initView(view: View) {
        loader_event_cards = view.findViewById(R.id.loader_event_cards)
        frame_alltab_mytab_container = view.findViewById(R.id.frame_alltab_mytab_container)
        btn_all = view.findViewById(R.id.btn_all)
        btn_my = view.findViewById(R.id.btn_my)
        btn_all_badge = view.findViewById(R.id.btn_all_badge)
        btn_my_badge = view.findViewById(R.id.btn_my_badge)
        btn_all_container = view.findViewById(R.id.btn_all_container)
        btn_my_container = view.findViewById(R.id.btn_my_container)
    }

    /**
     * attach the listeners
     */
    private fun initListener() {
        btn_all_container.setOnClickListener(this)
        btn_my_container.setOnClickListener(this)
    }

    /**
     * bind the data to the view/UI
     */
    private fun bindData() {
        eventFilterer = EventFilterer()
        Events = ArrayList<EventBean>()
        filteredAllOpenEventsList = ArrayList<EventBean>()
        filteredMyEventsList = ArrayList<EventBean>()
        filteredMyInprogressEventsList = ArrayList<EventBean>()
        filteredMyCompletedEventsList = ArrayList<EventBean>()
        filteredOthersInprogressEventsList = ArrayList<EventBean>()
        btn_all_container.isSelected = true
        btn_my_container.isSelected = false
    }

    /**
     * attach the tabs (alltab & mytab) to this fragment
     */
    private fun attachTabsView() {
        val fragmentManager: FragmentManager = mActivity.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.frame_alltab_mytab_container, myTabFragment!!)
            .add(R.id.frame_alltab_mytab_container, allTabFragment!!)
            .hide(myTabFragment!!)
            .commit()
    }

    /**
     * update the badge count on any dataset changes
     */
    private fun updateBadges() {
        val alltab_badge_count =
            if (filteredAllOpenEventsList == null) 0 else filteredAllOpenEventsList.size
        val mytab_badge_count =
            if (filteredMyInprogressEventsList == null) 0 else filteredMyInprogressEventsList.size //getMyTabFragment().getBadgeCount();
        var btnall_drawable: BadgeDrawable? = null
        if (alltab_badge_count > 0) {
            btnall_drawable = BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(alltab_badge_count)
                .padding(4f, 4f, 4f, 4f, 4f)
                .build()
        }
        var btnmy_drawable: BadgeDrawable? = null
        if (mytab_badge_count > 0) {
            btnmy_drawable = BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(mytab_badge_count)
                .padding(4f, 4f, 4f, 4f, 4f)
                .build()
        }
        btn_all_badge.setImageDrawable(btnall_drawable)
        btn_my_badge.setImageDrawable(btnmy_drawable)
        EventBus.getDefault()
            .post(UpdateOthersInprogressBadge(filteredOthersInprogressEventsList.size))
    }

    /**
     * getallevents api call
     * further filter the Events according to open Events, my inprogress Events, my completed Events & other's inprogress Events
     */
    fun fetchEvents() {
        if (!isConnectedToInternet(mActivity)) {
            loader_event_cards.visibility = View.GONE
            filterEvents(null)
            showPrompt(mActivity, getResources().getString(R.string.error_no_internet_connection))
            return
        }
        loader_event_cards.visibility = View.VISIBLE
        val storeId = getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_STOREID, null)
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
                @NonNull call: Call<GetEventsApiResponse?>,
                response: Response<GetEventsApiResponse?>
            ) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body()!!.Error == 0 && response.body()!!.Events != null) { //Add log for get all event response
                        Log.e("Kodak", "Get All Response " + response.raw())
                        filterEvents(response.body()!!.Events)
                        //Save KNumber of Events
                        if (response.body()!!.Events!!.size > 0) {
                            setSharedPreferenceString(
                                mActivity,
                                SharedPreferenceHelper.KEY_STORE_KNUMBER,
                                response.body()!!.Events!!.get(0).KNumber
                            )
                        }
                    }
                } else {
                    val error: APIError = parseError(response)
                    filterEvents(null)
                    showPrompt(
                        mActivity,
                        getResources().getString(R.string.msg_fetching_events_error)
                    )
                }
                loader_event_cards.visibility = View.GONE
            }

            override fun onFailure(call: Call<GetEventsApiResponse?>, t: Throwable) {
                loader_event_cards.visibility = View.GONE
                filterEvents(null)
                showPrompt(mActivity, getResources().getString(R.string.msg_fetching_events_error))
            }
        })
    }

    /**
     * filter Events to open Events, my inprogress Events, my completed Events & other's inprogress Events
     * @param eventsResponse list of all Events
     */
    private fun filterEvents(eventsResponse: List<EventBean>?) {
        Events.clear()
        if (eventsResponse != null && eventsResponse.size > 0) {
            Events.addAll(eventsResponse)
            eventFilterer.updateAllOpenEvents()
            eventFilterer.updateMyEvents()
            eventFilterer.updateMyInprogressEvents()
            eventFilterer.updateMyCompletedEvents()
            eventFilterer.updateOthersInprogressEvents()
        }
        updateBadges()
        allTabFragment!!.updateAllOpenEvents()
        myTabFragment!!.updateMyEvents()
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.btn_all_container -> {
                if (!btn_all_container.isSelected) {
                    btn_all_container.isSelected = true
                    btn_my_container.isSelected = false
                    val fragmentManager: FragmentManager = mActivity.supportFragmentManager
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.animator_alltab_in, 0)
                        .hide(myTabFragment!!).show(allTabFragment!!).commit()
                    EventBus.getDefault().post(EnterIntoAllTabViewEvent())
                }
            }
            R.id.btn_my_container -> {
                if (!btn_my_container.isSelected) {
                    btn_all_container.isSelected = false
                    btn_my_container.isSelected = true
                    val fragmentManager: FragmentManager = mActivity.supportFragmentManager
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(0, R.animator.animator_alltab_out)
                        .hide(allTabFragment!!).show(myTabFragment!!).commit()
                    EventBus.getDefault().post(EnterIntoMyTabViewEvent())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_assignments))
        fetchEvents()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            mActivity = context
            attachTabsView()
        } else {
            throw NullPointerException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        try {
            val fragmentManager: FragmentManager = mActivity.supportFragmentManager
            fragmentManager.beginTransaction()
                .remove(allTabFragment!!)
                .remove(myTabFragment!!)
                .commit()
            //mActivity = null;
        } catch (ise: IllegalStateException) {
            ise.printStackTrace()
        }
    }

    /**
     * handle the eventbus on accepting or picking event from open event
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: AcceptEvent?) {
        Handler().postDelayed({ updateBadges() }, 400)
    }

    /**
     * handle the eventbus when open event is swipped/ignored by user
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: IgnoreEvent?) {
    }

    /**
     * handle the eventbus when user's picked event is marked as completed
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: CompleteEvent?) {
        Handler().postDelayed({ updateBadges() }, 400)
    }

    /**
     * handle the eventbus when system need to update its Events
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: UpdateAllOpenEvent?) {
        fetchEvents()
    }

    /**
     * helper class to filter the Events to open Events, my inprogress Events, my completed Events & other's inprogress Events
     * using stream api, which required min api level 24 i.e. android 7.0
     */
    private inner class EventFilterer {
        /**
         * filter all open Events
         */
        internal fun updateAllOpenEvents() {
            val Responder =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, null)
                    ?: return
            val _allOpenEvents: List<EventBean>? = Events
                .stream()
                .filter { event: EventBean -> event.Responder == null }
                ?.filter { event: EventBean ->
                    event.Status!!.contains(Constants.EVENT_STATUS.CREATED)
                }
                ?.collect(Collectors.toList<EventBean>())
            filteredAllOpenEventsList.clear()
            if (_allOpenEvents != null && _allOpenEvents.size > 0) {
                filteredAllOpenEventsList.addAll(_allOpenEvents)
//                filteredAllOpenEventsList.reverse()
                Collections.sort(filteredAllOpenEventsList, Comparator<EventBean?> { o1, o2 ->
                    o2!!.CreationDateTime.compareTo(o1!!.CreationDateTime)
                })
            }
        }

        /**
         * filter all my Events include inprogress & completed
         */
        internal fun updateMyEvents() {
            val Responder =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, null)
                    ?: return
            val _myEvents: List<EventBean>? = Events
                .stream()
                .filter { event: EventBean -> event.Responder != null }
                ?.filter { event: EventBean ->
                    event.Responder!!.toLowerCase().equals(Responder.toLowerCase())
                }
                ?.filter { event: EventBean ->
                    event.Status!!.contains(
                        Constants.EVENT_STATUS.BEING_WORKED_ON
                    ) || event.Status!!.contains(Constants.EVENT_STATUS.COMPLETED)
                }
                ?.collect(Collectors.toList<EventBean>())
            filteredMyEventsList.clear()
            if (_myEvents != null && _myEvents.size > 0) {
                filteredMyEventsList.addAll(_myEvents)
//                filteredMyEventsList.reverse()
                Collections.sort(filteredMyEventsList, Comparator<EventBean?> { o1, o2 ->
                    o2!!.CreationDateTime.compareTo(o1!!.CreationDateTime)
                })
            }
        }

        /**
         * filter all my inprogress Events
         */
        internal fun updateMyInprogressEvents() {
            val Responder =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, null)
            val deviceid =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_DEVICEID, null)
            if (Responder == null || deviceid == null) {
                return
            }
            val _myInprogressEvents: List<EventBean>? = Events
                .stream()
                .filter { event: EventBean -> event.Responder != null }
                ?.filter { event: EventBean ->
                    (event.Responder!!.toLowerCase().equals(Responder.toLowerCase())
                            || event.Responder!!.toLowerCase().equals(deviceid.toLowerCase()))
                }
                ?.filter { event: EventBean ->
                    event.Status!!.contains(
                        Constants.EVENT_STATUS.BEING_WORKED_ON
                    )
                }
                ?.collect(Collectors.toList<EventBean>())
            filteredMyInprogressEventsList.clear()
            if (_myInprogressEvents != null && _myInprogressEvents.size > 0) {
                filteredMyInprogressEventsList.addAll(_myInprogressEvents)
//                filteredMyInprogressEventsList.reverse()
                Collections.sort(filteredMyInprogressEventsList, Comparator<EventBean?> { o1, o2 ->
                    o2!!.CreationDateTime.compareTo(o1!!.CreationDateTime)
                })
            }
        }

        /**
         * filter all my completed Events
         */
        internal fun updateMyCompletedEvents() {
            val Responder =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, null)
            val deviceid =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_DEVICEID, null)
            if (Responder == null || deviceid == null) {
                return
            }
            val _myCompletedEvents: List<EventBean>? = Events
                .stream()
                .filter { event: EventBean -> event.Responder != null }
                ?.filter { event: EventBean ->
                    (event.Responder!!.toLowerCase().equals(Responder.toLowerCase())
                            || event.Responder!!.toLowerCase().equals(deviceid.toLowerCase()))
                }
                ?.filter { event: EventBean ->
                    event.Status!!.contains(
                        Constants.EVENT_STATUS.COMPLETED
                    )
                }
                ?.filter { event: EventBean ->
                    (onlyRtrnDate(mActivity, event.CompletionDateTime).equals(
                        getCurrentDate(
                            mActivity
                        )
                    ))
                }
                ?.collect(Collectors.toList<EventBean>())
            //filteredMyCompletedEventsList.clear();
            if (_myCompletedEvents != null && _myCompletedEvents.size > 0) {
                filteredMyCompletedEventsList.clear();
                filteredMyCompletedEventsList.addAll(_myCompletedEvents)
//                filteredMyCompletedEventsList.reverse()
                Collections.sort(filteredMyCompletedEventsList, Comparator<EventBean?> { o1, o2 ->
                    o2!!.CompletionDateTime.compareTo(o1!!.CompletionDateTime)
                })
            }
        }

        /**
         * filter all other's inprogress Events
         */
        internal fun updateOthersInprogressEvents() {
            val Responder =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_RESPONDER, null)
            val deviceid =
                getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_DEVICEID, null)
            if (Responder == null || deviceid == null) {
                return
            }
            val _inprogressOrCompletedEvents: List<EventBean>? = Events
                .stream()
                .filter { event: EventBean -> event.Responder != null }
                ?.filter { event: EventBean ->
                    (!event.Responder!!.toLowerCase().equals(Responder.toLowerCase())
                            || event.Responder!!.toLowerCase().equals(deviceid.toLowerCase()))
                }
                ?.filter { event: EventBean ->
                    event.Status!!.contains(
                        Constants.EVENT_STATUS.BEING_WORKED_ON
                    )
                }
                ?.collect(Collectors.toList<EventBean>())
            filteredOthersInprogressEventsList.clear()
            if (_inprogressOrCompletedEvents != null && _inprogressOrCompletedEvents.size > 0) {
                filteredOthersInprogressEventsList.addAll(_inprogressOrCompletedEvents)
//                filteredOthersInprogressEventsList.reverse()
                Collections.sort(
                    filteredOthersInprogressEventsList,
                    Comparator<EventBean?> { o1, o2 ->
                        o2!!.CreationDateTime.compareTo(o1!!.CreationDateTime)
                    })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if(resultCode==1000)
        fetchEvents();*/
    }

    companion object {
        private val TAG = AssignmentsFragment::class.java.simpleName
        private var mInstance: AssignmentsFragment? = null
        private var allTabFragment: AllTabFragment? = null
        private var myTabFragment: MyTabFragment? = null

        /**
         * get this fragment instance, if already created
         * otherwise create new
         * @return
         */
        @Synchronized
        fun newInstance(): AssignmentsFragment? {
            if (mInstance == null) {
                mInstance = AssignmentsFragment()
            }
            return mInstance
        }
    }
}
