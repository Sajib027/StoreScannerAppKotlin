package com.kodakalaris.advisor.model

/**
 * event info api response body
 */
class EventInfoApiResponse {
    /**
     * Error : 0
     * Message :
     * Event : {"EventId":"F2A9806FE9D54B0192235A00075D03D5_CVS00001","KNumber":"KA012345","Type":"HELP","Status":"CREATED","Responder":"Silent Bob","CreationDateTime":1523622923,"ResponseDateTime":1523623032,"CompletionDateTime":1523623371,"CancellationReason":"TRIGGERED_BY_REBOOT","HelpData":{"HelpValue":"ON"},"ErrorData":{"ErrorCode":"FATAL_ERROR","ErrorDeviceType":"8x8Printer","ErrorDeviceNum":"Printer0001","ErrorSerialNum":"91238501-2123-9"},"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"}}
     */
    var Error = 0
    var Message: String? = null
    var Event: EventBean? = null

    override fun toString(): String {
        return "EventInfoApiResponse{" +
                "Error=" + Error +
                ", Message='" + Message + '\'' +
                ", Event=" + Event +
                '}'
    }
}
