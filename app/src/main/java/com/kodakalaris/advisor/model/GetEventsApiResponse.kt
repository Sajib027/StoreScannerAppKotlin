package com.kodakalaris.advisor.model

/**
 * get all events api response
 */
class GetEventsApiResponse {
    /**
     * Error : 0
     * Message :
     * Events : [{"EventId":"F2A9806FE9D54B0192235A00075D03D5_CVS00001","KNumber":"KA012345","Type":"HELP","Status":"CREATED","Responder":"Silent Bob","CreationDateTime":1523622923,"ResponseDateTime":1523623032,"CompletionDateTime":1523623371,"CancellationReason":"TRIGGERED_BY_REBOOT","HelpData":{"HelpValue":"ON"},"ErrorData":{"ErrorCode":"FATAL_ERROR","ErrorDeviceType":"8x8Printer","ErrorDeviceNum":"Printer0001","ErrorSerialNum":"91238501-2123-9"},"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"}}]
     */
//{"Events":[{"Responder":"John","EventId":"3E5F755C20F246F5BCC2D53B2C91A52D_1234","KNumber":"KA012345","Type":"HELP","Status":"BEING_WORKED_ON","CreationDateTime":1531748330,"ResponseDateTime":1531833760,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"C7E93F08CA1A4CB18E2BF242C9BE1116_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CANCELLED","CreationDateTime":1531749972,"ResponseDateTime":null,"CompletionDateTime":1531840700,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":"TIMEOUT"},{"Responder":null,"EventId":"1750C075225A4F69B9EF213B09644F56_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CREATED","CreationDateTime":1531818394,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"330805318D7D47FB89344C31439CAB9C_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531840700,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":"Ashley","EventId":"C9D23066CF6145C280FD5F9ADEAC47EE_1234","KNumber":"KA012345","Type":"HELP","Status":"BEING_WORKED_ON","CreationDateTime":1531840863,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"E908BB3402CA4FFE9CC603B9796E0B69_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CREATED","CreationDateTime":1531842461,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":null},{"Responder":"John","EventId":"9AC679F65E754514B6946B01D8F50157_1234","KNumber":"KA012345","Type":"HELP","Status":"BEING_WORKED_ON","CreationDateTime":1531842471,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"58126A874C2B409EB10DCEA6CB99A604_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531842834,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":"John","EventId":"EB57055C963D42AE86907643A5964529_1234","KNumber":"KA012345","Type":"HELP","Status":"COMPLETED","CreationDateTime":1531843091,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null}],"Error":0,"Message":null}
//{"Events":[{"Responder":"","EventId":"3E5F755C20F246F5BCC2D53B2C91A52D_1234","KNumber":"KA012345","Type":"HELP","Status":"BEING_WORKED_ON","CreationDateTime":1531748330,"ResponseDateTime":1531833760,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"C7E93F08CA1A4CB18E2BF242C9BE1116_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CANCELLED","CreationDateTime":1531749972,"ResponseDateTime":null,"CompletionDateTime":1531840700,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":"TIMEOUT"},{"Responder":null,"EventId":"1750C075225A4F69B9EF213B09644F56_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CREATED","CreationDateTime":1531818394,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"330805318D7D47FB89344C31439CAB9C_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531840700,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"C9D23066CF6145C280FD5F9ADEAC47EE_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531840863,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"E908BB3402CA4FFE9CC603B9796E0B69_1234","KNumber":"KA012345","Type":"ASSEMBLE","Status":"CREATED","CreationDateTime":1531842461,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":null,"AssembleData":{"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"},"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"9AC679F65E754514B6946B01D8F50157_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531842471,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"58126A874C2B409EB10DCEA6CB99A604_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531842834,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null},{"Responder":null,"EventId":"EB57055C963D42AE86907643A5964529_1234","KNumber":"KA012345","Type":"HELP","Status":"CREATED","CreationDateTime":1531843091,"ResponseDateTime":null,"CompletionDateTime":null,"RemindMeDateTime":null,"HelpData":{"HelpValue":"ON"},"AssembleData":null,"ErrorData":null,"CancellationReason":null}],"Error":0,"Message":null}
    var Error = 0
    var Message: String? = null
    var Events: List<EventBean>? = null

    override fun toString(): String {
        return "GetEventsApiResponse{" +
                "Error=" + Error +
                ", Message='" + Message + '\'' +
                ", Events=" + Events +
                '}'
    }
}