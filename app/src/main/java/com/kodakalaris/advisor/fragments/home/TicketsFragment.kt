package com.kodakalaris.advisor.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity

/**
 * third fragment from the navigation drawer/side menu
 * A simple [Fragment] subclass.
 * Use the [TicketsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TicketsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tickets, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_tickets))
    }

    companion object {
        private var fragment: TicketsFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ServiceCallFragment.
         */
        @Synchronized
        fun newInstance(): TicketsFragment? {
            if (fragment == null) {
                fragment = TicketsFragment()
            }
            return fragment
        }
    }
}
