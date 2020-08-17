package com.kodakalaris.advisor.fragments.tab

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.adapters.MyCompletedEventsAdapter
import com.kodakalaris.advisor.adapters.MyInprogressEventsPagerAdapter
import com.kodakalaris.advisor.custom.CircleIndicator
import com.kodakalaris.advisor.eventbus.AppEvents.*
import com.kodakalaris.advisor.fragments.home.AssignmentsFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 * Use the [MyTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyTabFragment : Fragment(), OnTouchListener {

    private var parentFragment: AssignmentsFragment? = null
    private lateinit var txt_my_inprogress_events_notavail: TextView
    private lateinit var txt_my_completed_events_notavail: TextView
    private lateinit var txt_completed: TextView
    private lateinit var recycler_myevents_history: RecyclerView
    private lateinit var container_mycompleted: ConstraintLayout
    private lateinit var myCompletedEventsAdapter: MyCompletedEventsAdapter
    private lateinit var pager_myevents: ViewPager
    private lateinit var myInprogressEventsPagerAdapter: MyInprogressEventsPagerAdapter
    private var pager_indicator: CircleIndicator? = null
    private lateinit var mActivity: HomeActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_tab, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container_mycompleted = view.findViewById(R.id.container_mycompleted)
        txt_completed = view.findViewById(R.id.textView5)
        txt_my_inprogress_events_notavail = view.findViewById(R.id.txt_myevent_notavail)
        txt_my_completed_events_notavail = view.findViewById(R.id.txt_myevent_history_notavail)
        recycler_myevents_history = view.findViewById(R.id.recycler_myevents_history)
        pager_myevents = view.findViewById(R.id.pager_myevents)
        pager_indicator = view.findViewById(R.id.pager_indicator)
        initMyInprogressEvents()
        initMyCompletedEvents()
    }

    /**
     * tweek to handle the viewpager flickering effect
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun handleViewpagerFlickeringEffect() {
        pager_myevents.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // requestDisallowInterceptTouchEvent(true); // not sure if this is required
        // consume the move event if we have only one page full - removes flickering artifact
        // getNumberOfPagesOnScreen() is a mehtod we have to get the number of pages we are going to display. ymmv
        return if (myInprogressEventsPagerAdapter.count <= 1 && event.action == MotionEvent.ACTION_MOVE) {
            true
        } else {
            false
        }
    }

    /**
     * initialize the my inprogress viewpager view/UI
     */
    private fun initMyInprogressEvents() {
        myInprogressEventsPagerAdapter = MyInprogressEventsPagerAdapter(
            mActivity.supportFragmentManager,
            parentFragment!!.filteredMyInprogressEventsList
        )
        pager_myevents.setAdapter(myInprogressEventsPagerAdapter)
        pager_indicator!!.setViewPager(pager_myevents)
        pager_myevents.setClipToPadding(false)
        pager_myevents.setPageMargin(64)
        myInprogressEventsPagerAdapter.registerDataSetObserver(pager_indicator!!.dataSetObserver)
        handleViewpagerFlickeringEffect()
        txt_my_inprogress_events_notavail.visibility =
            if (myInprogressEventsPagerAdapter.count == 0) View.VISIBLE else View.GONE
    }

    /**
     * initialize the my completed recylcerview view/UI
     */
    private fun initMyCompletedEvents() {
        myCompletedEventsAdapter =
            MyCompletedEventsAdapter(getContext()!!, parentFragment!!.filteredMyCompletedEventsList)
        val linearLayoutManager = LinearLayoutManager(getContext())
        recycler_myevents_history.setLayoutManager(linearLayoutManager)
        recycler_myevents_history.addItemDecoration(DividerItemDecoration(mActivity, 0))
        recycler_myevents_history.setAdapter(myCompletedEventsAdapter)
        txt_my_completed_events_notavail.visibility =
            if (myCompletedEventsAdapter.itemCount == 0) View.VISIBLE else View.GONE
        recycler_myevents_history.setVisibility(if (myCompletedEventsAdapter.itemCount == 0) View.GONE else View.VISIBLE)
        txt_completed.visibility =
            if (parentFragment!!.filteredMyCompletedEventsList.size == 0) View.GONE else View.VISIBLE
        Log.e("Advisor", "initMyCompetedEvents()")
    }

    /**
     * update my event both my inprogress and my completed events
     */
    fun updateMyEvents() {
        if (myInprogressEventsPagerAdapter != null && myCompletedEventsAdapter != null) {
            myInprogressEventsPagerAdapter.notifyDataSetChanged()
            myCompletedEventsAdapter.notifyDataSetChanged()
        }
        if (txt_my_inprogress_events_notavail != null) txt_my_inprogress_events_notavail.visibility =
            if (myInprogressEventsPagerAdapter.count == 0) View.VISIBLE else View.GONE
        if (txt_my_completed_events_notavail != null) txt_my_completed_events_notavail.visibility =
            if (myCompletedEventsAdapter.itemCount == 0) View.VISIBLE else View.GONE
        if (recycler_myevents_history != null) recycler_myevents_history.setVisibility(if (myCompletedEventsAdapter.itemCount == 0) View.GONE else View.VISIBLE)
        if (txt_completed != null) txt_completed.visibility =
            if (parentFragment!!.filteredMyCompletedEventsList.size == 0) View.GONE else View.VISIBLE
        Log.e("Advisor", "updateMyEvents()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            mActivity = context
            EventBus.getDefault().register(this)
        } else {
            throw NullPointerException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        //mActivity = null;
        EventBus.getDefault().unregister(this)
    }

    /**
     * handle the eventbus when user is enter into mytab
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: EnterIntoMyTabViewEvent?) {
        val animator =
            AnimatorInflater.loadAnimator(getContext(), R.animator.animator_myevent_pager_in)
        animator.setTarget(pager_myevents)
        animator.start()
        val animator1 =
            AnimatorInflater.loadAnimator(getContext(), R.animator.animator_my_completed_event_in)
        animator1.setTarget(recycler_myevents_history)
        animator1.start()
    }

    /**
     * handle the eventbus when user is exit from mytab
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: EnterIntoAllTabViewEvent?) {
        val animator =
            AnimatorInflater.loadAnimator(getContext(), R.animator.animator_myevent_pager_out)
        animator.setTarget(pager_myevents)
        animator.start()
        val animator1 =
            AnimatorInflater.loadAnimator(getContext(), R.animator.animator_my_completed_event_out)
        animator1.setTarget(recycler_myevents_history)
        animator1.start()
    }

    /**
     * handle the eventbus when open event is accepted/picked by user
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: AcceptEvent) {
        parentFragment!!.filteredMyInprogressEventsList.add(event.getAcceptEvent())
        parentFragment!!.Events.add(event.getAcceptEvent())
        updateMyEvents()
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
    fun onBusEvent(event: CompleteEvent) {
        parentFragment!!.filteredMyInprogressEventsList.removeAt(event.position)
        parentFragment!!.filteredMyCompletedEventsList.add(event.getCompleteEvent())
        updateMyEvents()
    }

    companion object {
        private val TAG = MyTabFragment::class.java.simpleName
        private lateinit var fragment: MyTabFragment
//        private var parentFragment: AssignmentsFragment? = null
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MyTabFragment.
         */
        fun newInstance(pFragment: AssignmentsFragment?): MyTabFragment? {
            fragment = MyTabFragment()
            fragment.parentFragment = pFragment
            return fragment
        }
    }
}