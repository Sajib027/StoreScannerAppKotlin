package com.kodakalaris.advisor.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceString

/**
 * third fragment from the navigation drawer/side menu
 * A simple [Fragment] subclass.
 * Use the [ServiceCallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceCallFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val serviceCallView = inflater.inflate(R.layout.fragment_service_call, container, false)
        val serviceCallNo = serviceCallView.findViewById<TextView>(R.id.textView)
        serviceCallNo.text = "Help needed at kiosk (" + getSharedPreferenceString(
            getActivity()!!,
            SharedPreferenceHelper.KEY_STORE_KNUMBER,
            ""
        ) + ")"
        return serviceCallView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        (getActivity() as HomeActivity).setWindowTitle(getResources().getString(R.string.lb_menu_servicecall))
    }

    companion object {
        private var fragment: ServiceCallFragment? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ServiceCallFragment.
         */
        @Synchronized
        fun newInstance(): ServiceCallFragment? {
            if (fragment == null) {
                fragment = ServiceCallFragment()
            }
            return fragment
        }
    }
}
