package com.kodakalaris.advisor.eventbus

import com.kodakalaris.advisor.model.EventBean

interface AppEvents {
    /**
     * this event call when clerk accept/pick any open event
     * it contain the event object & position from allopenevents list
     */
    class AcceptEvent(event: EventBean, position: Int) {
        private val acceptEvent: EventBean
        val position: Int

        fun getAcceptEvent(): EventBean {
            return acceptEvent
        }

        init {
            acceptEvent = event
            this.position = position
        }
    }

    /**
     * this event call when user ignore/swipe any open event
     */
    class IgnoreEvent

    /**
     * this event call when clerk mark any his/her event/assignment as completed
     * it contain the event object & position corresponding in completedevents list
     */
    class CompleteEvent(event: EventBean, position: Int) {
        private val completeEvent: EventBean
        val position: Int

        fun getCompleteEvent(): EventBean {
            return completeEvent
        }

        init {
            completeEvent = event
            this.position = position
        }
    }

    /**
     * this event call when user navigated to alltab
     */
    class EnterIntoAllTabViewEvent

    /**
     * this event call when user navigated to mytab
     */
    class EnterIntoMyTabViewEvent

    /**
     * this event call when user is registering store
     */
    class RegisteringStoreEvent

    /**
     * this event call when user open the navigation drawer/app side-menu
     */
    class NavigationDrawerOpenedEvent

    /**
     * this event call when user close the navigation drawer/app side-menu
     */
    class NavigationDrawerClosedEvent

    /**
     * this event call when user navigating to settings screen
     */
    class BacktoSettingsScreenEvent

    /**
     * this event call when system wants to update other's inprogress badge count
     */
    class UpdateOthersInprogressBadge(val badgeCount: Int)

    /**
     * this event call when system wants to update the all open events
     */
    class UpdateAllOpenEvent

    class OnNotificationEvent
}