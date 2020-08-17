package com.kodakalaris.advisor.adapters


import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kodakalaris.advisor.fragments.MyInprogressEventsPagerFragment
import com.kodakalaris.advisor.model.EventBean

/**
 * my inprogress event viewpager's adapter
 */
@Suppress("DEPRECATION")
class MyInprogressEventsPagerAdapter(
    fragmentManager: FragmentManager?,
    myInprogressEventsList: List<EventBean>?
) : FragmentStatePagerAdapter(fragmentManager!!) {

    private val myInprogressEvents: List<EventBean>?
    override fun getItem(i: Int): Fragment {
        return MyInprogressEventsPagerFragment.newInstance(i, myInprogressEvents!![i])
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getPageWidth(position: Int): Float {
        return 0.93f
    }

    override fun getCount(): Int {
        return myInprogressEvents?.size ?: 0
    }

    override fun getItemPosition(@NonNull `object`: Any): Int {
        return POSITION_NONE
    }

    init {
        myInprogressEvents = myInprogressEventsList
    }
}
