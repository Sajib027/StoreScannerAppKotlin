package com.kodakalaris.advisor.utils

/**
 * App Constants
 */
class Constants {
    /**
     * basic app constants
     */
    object APP {
        const val DEFAULT_LOG_FLAG = false
        const val DEFAULT_AUTOCOMPLETE_FLAG = true
        const val TAG = "clerkApp:"
        const val APPID = "KAClerkApp"
        const val APPSECRET = "D5EAED68-740A-48DF-843D-758EEDCB63ED"
        const val CLOUD_NOTIFICATION = "gcm"
        const val LAST_SCREEN_IS_HOME = "LAST SCREEN IS HOME"
        const val PASSWORD_MODE = "a2c78b81ff89e21038ef14b9278fe5cb"
        const val API_MODE_DEV = "Development"
        const val API_MODE_STAGE = "Staging"
        const val API_MODE_PROD = "Production"

        const val API_BASE_URL = "https://kakioskaid.azurewebsites.net/"

        const val API_PROD_BASE_URL = "https://kakioskaid.azurewebsites.net/"
        const val API_STAGE_BASE_URL = "https://kakioskaid-stage.azurewebsites.net/"
        const val API_DEV_BASE_URL = "https://kakioskaiddev.azurewebsites.net/"

        const val GROUP_KEY_WORK_EMAIL = "com.kodakalaris.advisor"
        const val LAST_SCANNED_BARCODE_DATA = "last scanned barcode data"
        const val LAST_SCANNED_BARCODE_ERROR = "last scanned barcode error"
        const val LAST_SCANNED_STORE_ID = "last_scanned_store_id"
        const val REQUEST_CODE = 1000
        const val SUMMARY_ID = 0
        const val QRCODE_REQUESTCODE1 = 1
        const val QRCODE_REQUESTCODE2 = 2
        const val QRCODE_RESULTCODE1 = 11
        const val QRCODE_RESULTCODE2 = 12
        const val ENGLISH_HELP_URL = "https://advisor.kodakmoments.com/help-en"
        const val ENGLISH_NEWS_URL = "https://advisor.kodakmoments.com/news-en"
        const val GERMAN_HELP_URL = "https://advisor.kodakmoments.com/help-de"
        const val GERMAN_NEWS_URL = "https://advisor.kodakmoments.com/news-de"
    }

    object NOTIFICATION {
        const val DONOTDISTURB_CHANNEL_ID = "10001"
        const val DONOTDISTURB_CHANNEL_NAME = "do not disturb"
        const val ONLYVIBRATION_CHANNEL_ID = "10002"
        const val ONLYVIBRATION_CHANNEL_NAME = "only vibration"
        const val ONLYVOLUME_CHANNEL_ID = "10003"
        const val ONLYVOLUME_CHANNEL_NAME = "only volume"
        const val NORMAL_CHANNEL_ID = "10004"
        const val NORMAL_CHANNEL_NAME = "normal"
    }

    /**
     * events status constants
     */
    object EVENT_STATUS {
        const val CREATED = "CREATED"
        const val BEING_WORKED_ON = "BEING_WORKED_ON"
        const val COMPLETED = "COMPLETED"
        const val CANCELLED = "CANCELLED"
    }

    /**
     * events state constants
     */
    object EVENT_STATE {
        const val EVENT_STATE_OPEN = "OPEN"
        const val EVENT_STATE_ALL = "ALL"
    }

    /**
     * events type constants
     */
    object EVENT_TYPE {
        const val EVENT_TYPE_HELP = "HELP"
        const val EVENT_TYPE_ASSEMBLE = "ASSEMBLE"
        const val EVENT_TYPE_ERROR = "ERROR"
        const val EVENT_TYPE_REBOOT = "REBOOT"
    }

    /**
     * card swipe direction constants
     */
    private object SWIPEDIRECTION {
        const val RIGHT = "Right"
        const val LEFT = "Left"
    }

    /**
     * api responde code constants
     */
    private object API_RESPONSE_CODE {
        const val OK = 0 //success
        const val INTERNAL_SERVER_ERROR = -1 //Something unexpected happened.
        const val BAD_REQUEST_2 = -2 //No Request Body was given. 
        const val BAD_REQUEST_3 = -3 //No Device ID was provided. 
        const val BAD_REQUEST_4 = -4 //No ‘Platform’ value was given.
        const val BAD_REQUEST_5 =
            -5 //‘Platform’ value given was not a valid NotificationPlatform value.
        const val BAD_REQUEST_6 =
            -6 //‘Platform’ value given was a valid NotificationPlatform value, but the platform it represents is not supported at this time. 
        const val BAD_REQUEST_7 = -7 //No ‘StoreId’ value was given. 
        const val BAD_REQUEST_8 = -8 //No Message was given. 
        const val NOT_FOUND =
            -9 //Could not find a clerk device associated with the given device Id. 
        const val BAD_REQUEST_10 = -10 //No KNumber was given. 
        const val BAD_REQUEST_11 = -11 //No Event Type was given.
        const val BAD_REQUEST_12 = -12 //Event Type given is not valid. 
        const val BAD_REQUEST_13 = -13 //Event Type given is valid, but is currently not supported. 
        const val BAD_REQUEST_14 =
            -14 //Type-specific event data is missing (‘Type’ = ‘HELP’ with no ‘HelpData’, ‘Type’ = ‘ASSEMBLE’ with no ‘AssembleData’, ‘Type’ = ‘ERROR’ with no ‘ErrorData’).
        const val BAD_REQUEST_15 =
            -15 //Request to create a ‘HELP’ event did not contain a help value.
        const val BAD_REQUEST_16 =
            -16 //Request to create a ‘ASSEMBLE’ event did not contain a Product value.
        const val BAD_REQUEST_17 =
            -17 //Request to create a ‘ASSEMBLE’ event did not contain a Product Identifier. 
        const val BAD_REQUEST_18 =
            -18 //Request to create a ‘ERROR’ event did not contain an Error Code.
        const val BAD_REQUEST_19 =
            -19 //Request to create a ‘ERROR’ event did not contain a Device Type
        const val BAD_REQUEST_20 =
            -20 //Request to create a ‘ERROR’ event did not contain a Device Number.
        const val BAD_REQUEST_21 =
            -21 //Request to create a ‘ERROR’ event did not contain a Serial Number. 
        const val BAD_REQUEST_22 = -22 //No ID was given to look an event of. 
        const val BAD_REQUEST_23 = -23 //The event matching the given ID was not found. 
        const val BAD_REQUEST_24 =
            -24 //A value was given for ‘eventStateFilter’, but it does not match any expected value.
        const val BAD_REQUEST_25 =
            -25 //A valid value was given for ‘eventStateFilter’, but it is not currently in use.
        const val BAD_REQUEST_26 = -26 //No ‘State’ value was supplied to update the event to. 
        const val BAD_REQUEST_27 =
            -27 //An invalid ‘State’ value was supplied to update the event to. 
        const val BAD_REQUEST_28 =
            -28 //A valid ‘State’ value was supplied to update the event to, but the state change is not currently supported.
        const val BAD_REQUEST_29 =
            -29 //An attempt was made to ‘Respond’ to an event that has already been responded to. 
        const val BAD_REQUEST_30 =
            -30 //No Clerk Device could be found with the given Responder (device) id. 
        const val BAD_REQUEST_31 = -31 //An attempt was made to perform an illegal state change. 
        const val BAD_REQUEST_32 =
            -32 //An attempt was made to mark an event as ‘BEING_WORKED_ON’ without a ‘ResponderId’ being provided. 
    }

    internal object Printer {
        const val Error_605 = "Error_605"
        const val Error_6800 = "Error_6800"
        const val Error_6850 = "Error_6850"
        const val Error_6900 = "Error_6900"
        const val Error_8810 = "Error_8810"
        const val Error_D4000 = "Error_D4000"
        const val Error_D4600 = "Error_D4600"
        const val Error_7000 = "Error_7000"
        const val Error_DL2100 = "Error_DL2100"
        const val Error_DL2200 = "Error_DL2200"
        const val Error_MugPrinter = "Error_MugPrinter"
        const val Error_PosterPrint = "Error_PosterPrint"
        const val Error_RapidPrintScanner = "Error_RapidPrintScanner"
        const val Error_ReceiptPrinter = "Error_ReceiptPrinter"
        const val Printer_D4000 = "D4000 Duplex Photo Printer"
        const val Printer_DL2200 = "DL2200 Printer"
        const val Printer_6900 = "Kodak 6900 Photo Printer"
        const val Printer_DL2100 = "Kodak DL2100 Printer"
        const val Printer_605 = "Kodak Photo Printer 605"
        const val Printer_6800 = "Kodak Photo Printer 6800"
        const val Printer_6850 = "Kodak Photo Printer 6850"
        const val Printer_8810 = "Kodak Photo Printer 8810"
        const val Printer_7000 = "Kodak Photo Printer 7000"
        const val Printer_D4600 = "Kodak Photo Printer D4600"
        const val Printer_SG400 = "Sawgrass SG400"
        const val Scanner_RapidPrint = "Rapid Print Scanner"
        const val Printer_Nippon_Primex = "Nippon Primex USB Receipt Printer"

        //Extra
        const val Printer_Epson_Pro_78XX = "Epson Stylus Pro 78XX"
        const val Printer_Epson_SureColor_P6000 = "Epson SureColor P6000"
        const val Printer_8800 = "Kodak Photo Printer 8800"
    }

    object EventLog {
        const val TAG_GetAllEvent_Request = "Get AllEvent Request"
        const val TAG_GetAllEvent_Response = "Get AllEvent Response"
        const val TAG_Notification_Update_Event = "Notification_Data_Update_Event"
        const val TAG_Notification_Create_Event = "Notification_Data_Create_Event"
        const val TAG_AssignToMe_Request = "Action_AssignToMe_Request"
        const val TAG_AssignToMe_Response = "Action_AssignToMe_Response"
        const val TAG_CompleteEvent_Request = "Action_CompleteEvent_Request"
        const val TAG_CompleteEvent_Response = "Action_CompleteEvent_Response"
        const val TAG_OwnEventFromALLTab_Request = "Action_OwnEvnetFromALLTab_Request"
        const val TAG_OwnEventFromALLTab_Response = "Action_OwnEvnetFromALLTab_Response"
    }
}
