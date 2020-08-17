package com.kodakalaris.advisor.fragments.tab

import android.animation.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.activities.InprogressOrCompletedActivity
import com.kodakalaris.advisor.adapters.AllOpenEventsAdapter
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.custom.CounterFab
import com.kodakalaris.advisor.custom.cardstackview.CardStackView
import com.kodakalaris.advisor.custom.cardstackview.CardStackView.CardEventListener
import com.kodakalaris.advisor.custom.cardstackview.SwipeDirection
import com.kodakalaris.advisor.eventbus.AppEvents.*
import com.kodakalaris.advisor.fragments.home.AssignmentsFragment
import com.kodakalaris.advisor.model.EventBean
import com.kodakalaris.advisor.model.EventInfoApiResponse
import com.kodakalaris.advisor.model.RequestUpdateEventApi
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.Helper.isConnectedToInternet
import com.kodakalaris.advisor.utils.Helper.showPrompt
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [AllTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllTabFragment : Fragment(), View.OnClickListener,

    CardEventListener {
    private var parentFragment: AssignmentsFragment? = null
    private var cardStackView: CardStackView? = null
    private lateinit var fbtn_ignore: FloatingActionButton
    private lateinit var fbtn_check: FloatingActionButton
    private var fbtn_sandhour: CounterFab? = null
    private lateinit var txt_noevents: TextView
    private lateinit var adapter: AllOpenEventsAdapter
    private lateinit var mActivity: HomeActivity
    private lateinit var container_event_cards: ConstraintLayout
    private lateinit var layout_main: ConstraintLayout
    private lateinit var img_kodak_logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_all_tab, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container_event_cards = view.findViewById(R.id.container_event_cards)
        layout_main = view.findViewById(R.id.layout_main)
        txt_noevents = view.findViewById(R.id.txt_noevents)
        fbtn_ignore = view.findViewById(R.id.fbtn_ignore)
        fbtn_sandhour = view.findViewById(R.id.fbtn_sandhour)
        fbtn_check = view.findViewById(R.id.fbtn_check)
        cardStackView = view.findViewById(R.id.event_cards)
        img_kodak_logo = view.findViewById(R.id.img_kodak_logo)
        initCardStackView()
        initListeners()
    }

    /**
     * initializing the open event card stack
     */
    private fun initCardStackView() {
        adapter = AllOpenEventsAdapter(mActivity)
//        Collections.sort(parentFragment!!.filteredAllOpenEventsList)
        adapter.addAll(parentFragment!!.filteredAllOpenEventsList)
        cardStackView!!.setAdapter(adapter)
    }

    /**
     * bind the listeners to the view/UI
     */
    private fun initListeners() {
        fbtn_ignore.setOnClickListener(this)
        fbtn_sandhour!!.setOnClickListener(this)
        fbtn_check.setOnClickListener(this)
        cardStackView!!.setCardEventListener(this)
    }

    /**
     * perform card left swipe, animate the left swipe with translate, rotate & fadeout
     * this function call on ignore button click
     */
    private fun swipeLeft() {
        val target: View = cardStackView!!.topView
        val targetOverlay: View? = cardStackView!!.topView.overlayContainer
        val rotation: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("rotation", -10f)
        )
        rotation.duration = 600
        val translateX: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("translationX", 0f, -2000f)
        )
        val translateY: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("translationY", 0f, -1000f)
        )
        translateX.startDelay = 100
        translateY.startDelay = 100
        translateX.duration = 1000
        translateY.duration = 1000
        val cardAnimationSet = AnimatorSet()
        cardAnimationSet.playTogether(rotation, translateX, translateY)
        val overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f)
        overlayAnimator.duration = 600
        val overlayAnimationSet = AnimatorSet()
        overlayAnimationSet.playTogether(overlayAnimator)
        cardStackView!!.swipe(SwipeDirection.Left, cardAnimationSet, overlayAnimationSet)
    }

    /**
     * perform card right swipe, animate the right swipe with translate, rotate & fadeout
     * this function call on accept button click, call the acceptevent() which further pick the event to itself
     */
    private fun swipeRight() {
        val target: View = cardStackView!!.topView
        val targetOverlay: View? = cardStackView!!.topView.overlayContainer
        val rotation: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("rotation", 10f)
        )
        rotation.duration = 1000
        val translateX: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("translationX", 0f, 4000f)
        )
        val translateY: ValueAnimator = ObjectAnimator.ofPropertyValuesHolder(
            target,
            PropertyValuesHolder.ofFloat("translationY", 0f, -3000f)
        )
        translateX.startDelay = 100
        translateY.startDelay = 100
        translateX.duration = 1000
        translateY.duration = 1000
        val overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f)
        overlayAnimator.duration = 1000
        val set = AnimatorSet()
        set.playTogether(rotation, translateX, translateY, overlayAnimator)
        set.start()
        val position = 0
        val eventid = parentFragment!!.filteredAllOpenEventsList[position].EventId
        acceptEvent(eventid, position)
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.fbtn_ignore -> {
                swipeLeft()
            }
            R.id.fbtn_sandhour -> {
                /**
                 * navigate to other's inprogress screen
                 */
                if (parentFragment!!.filteredOthersInprogressEventsList != null && parentFragment!!.filteredOthersInprogressEventsList.size > 0) {
                    getActivity()!!.startActivityForResult(
                        Intent(
                            mActivity,
                            InprogressOrCompletedActivity::class.java
                        ), Constants.APP.REQUEST_CODE
                    )
                } else {
                    showPrompt(mActivity, getResources().getString(R.string.msg_noothersinprogress))
                }
            }
            R.id.fbtn_check -> {
                swipeRight()
            }
        }
    }

    override fun onCardDragging(percentX: Float, percentY: Float) {
        val alpha =
            if (Math.abs(percentX) > Math.abs(percentY)) Math.abs(percentX) else Math.abs(percentY)
        cardStackView!!.topView.alpha = 1.6f - alpha
    }

    /**
     * handling the swipe & remove the event from the list & add to stack at the back
     * @param direction gives the swipe direction LEFT or RIGHT
     */
    override fun onCardSwiped(direction: SwipeDirection?) {
        if (parentFragment!!.filteredAllOpenEventsList != null && parentFragment!!.filteredAllOpenEventsList.size > 0) {
            val position = 0
            val swipedEvent: EventBean = parentFragment!!.filteredAllOpenEventsList[position]
            parentFragment!!.filteredAllOpenEventsList.removeAt(position)
            parentFragment!!.filteredAllOpenEventsList.add(
                parentFragment!!.filteredAllOpenEventsList.size,
                swipedEvent
            )
            adapter.clear()
            adapter.addAll(parentFragment!!.filteredAllOpenEventsList)
            adapter.notifyDataSetChanged()
            updateAllOpenEvents()
            EventBus.getDefault().post(IgnoreEvent())
            if (parentFragment!!.filteredAllOpenEventsList.size == 0) {
                parentFragment!!.fetchEvents()
            }
        }
    }

    override fun onCardReversed() {
        Log.d(TAG, "onCardReversed:")
    }

    override fun onCardMovedToOrigin() {
        Log.d(TAG, "onCardMovedToOrigin:")
    }

    override fun onCardClicked(index: Int) {
        Log.d(TAG, "onCardClicked:")
    }

    /**
     * update all open events & update the view/UI
     */
    fun updateAllOpenEvents() {
        if (parentFragment!!.filteredAllOpenEventsList != null && parentFragment!!.filteredAllOpenEventsList.size > 0) {
            val sizeZero = parentFragment!!.filteredAllOpenEventsList.size == 0
            adapter.clear()
            adapter.addAll(parentFragment!!.filteredAllOpenEventsList)
            cardStackView!!.visibility = if (sizeZero) View.GONE else View.VISIBLE
            fbtn_ignore.setVisibility(if (sizeZero) View.GONE else View.VISIBLE)
            fbtn_check.setVisibility(if (sizeZero) View.GONE else View.VISIBLE)
            txt_noevents.visibility = if (sizeZero) View.VISIBLE else View.GONE
            fbtn_sandhour!!.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        } else {
            if (cardStackView != null) cardStackView!!.visibility = View.GONE
            if (fbtn_ignore != null) fbtn_ignore.setVisibility(View.GONE)
            if (fbtn_sandhour != null) fbtn_sandhour!!.visibility = View.VISIBLE
            if (fbtn_check != null) fbtn_check.setVisibility(View.GONE)
            if (txt_noevents != null) txt_noevents.visibility = View.VISIBLE
        }
    }

    /**
     * accept/pick the event
     * @param eventId event id to pick
     * @param position position of the event corresponding to the allopenevents list
     */
    private fun acceptEvent(eventId: String?, position: Int) {
        if (!isConnectedToInternet(mActivity)) {
            showPrompt(mActivity, getResources().getString(R.string.error_no_internet_connection))
            return
        }

        val responderId =
            getSharedPreferenceString(mActivity, SharedPreferenceHelper.KEY_DEVICEID, null)
        val autocompleteState = SharedPreferenceHelper.getSharedPreferenceBoolean(
            mActivity,
            SharedPreferenceHelper.KEY_AUTOCOMPLETE_TASK,
            Constants.APP.DEFAULT_AUTOCOMPLETE_FLAG
        )

        // Check if autocomplete task is true or not,
        // if true then directly call the Api with status marked as Completed,
        // else then call Api by changing the status of the task to Bring Worked On

        if (autocompleteState) {
            val params = RequestUpdateEventApi(Constants.EVENT_STATUS.COMPLETED, responderId)
            //Add request to logger
            generateLogsOnStorage("Update Event Request")
            AppController.service!!.updateEvent(eventId, params)
                ?.enqueue(object : Callback<EventInfoApiResponse?> {
                    override fun onResponse(
                        call: Call<EventInfoApiResponse?>,
                        response: Response<EventInfoApiResponse?>
                    ) {
                        try {
                            if (response.code() == 200 && response.body() != null && response.body()!!.Error == 0) {
                                parentFragment!!.filteredAllOpenEventsList.removeAt(position)
                                adapter.clear()
                                adapter.addAll(parentFragment!!.filteredAllOpenEventsList)
                                adapter.notifyDataSetChanged()
                                updateAllOpenEvents()
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
        } else {
            val params = RequestUpdateEventApi(Constants.EVENT_STATUS.BEING_WORKED_ON, responderId)
            //Add request to logger
            generateLogsOnStorage("Update Event Request")
            AppController.service!!.updateEvent(eventId, params)
                ?.enqueue(object : Callback<EventInfoApiResponse?> {
                    override fun onResponse(
                        call: Call<EventInfoApiResponse?>,
                        response: Response<EventInfoApiResponse?>
                    ) {
                        if (response.code() == 200 && response.body() != null && response.body()!!.Error == 0 && response.body()!!.Event != null) {
                            parentFragment!!.filteredAllOpenEventsList.removeAt(position)
                            adapter.clear()
                            adapter.addAll(parentFragment!!.filteredAllOpenEventsList)
                            adapter.notifyDataSetChanged()
                            updateAllOpenEvents()
                            EventBus.getDefault()
                                .post(AcceptEvent(response.body()!!.Event!!, position))
                        } else {
                            showPrompt(
                                mActivity,
                                getResources().getString(R.string.error_accepting_events)
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<EventInfoApiResponse?>,
                        t: Throwable
                    ) {
                        showPrompt(
                            mActivity,
                            getResources().getString(R.string.error_accepting_events)
                        )
                    }
                })
        }
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
     * handle the eventbus to update othersinprogress badge cound
     * @param event event object to update
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: UpdateOthersInprogressBadge) {
        fbtn_sandhour!!.count = event.badgeCount
    }

    /**
     * handle the eventbus to enter into alltab, animate the enter animation to the alltab view
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: EnterIntoAllTabViewEvent?) {
        val animator = AnimatorInflater.loadAnimator(getContext(), R.animator.animator_alltab_in)
        animator.setTarget(cardStackView)
        animator.start()
    }

    /**
     * handle the eventbus to exit into alltab, animate the exit animation to the alltab view
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBusEvent(event: EnterIntoMyTabViewEvent?) {
        val animator1 = AnimatorInflater.loadAnimator(getContext(), R.animator.animator_alltab_out)
        animator1.setTarget(cardStackView)
        animator1.start()
    }

    companion object {
        //        private lateinit var parentFragment: AssignmentsFragment
        private val TAG = AllTabFragment::class.java.simpleName
        private lateinit var fragment: AllTabFragment

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AllTabFragment.
         */
        fun newInstance(pFragment: AssignmentsFragment): AllTabFragment {
            fragment = AllTabFragment()
            fragment.parentFragment = pFragment
            return fragment
        }
    }
}
