package com.kodakalaris.advisor.model

import android.os.Parcel
import android.os.Parcelable

/**
 * event/assignment object
 */
class EventBean : Parcelable {
    /**
     * EventId : F2A9806FE9D54B0192235A00075D03D5_CVS00001
     * KNumber : KA012345
     * Type : HELP
     * Status : CREATED
     * Responder : Silent Bob
     * CreationDateTime : 1523622923
     * ResponseDateTime : 1523623032
     * CompletionDateTime : 1523623371
     * CancellationReason : TRIGGERED_BY_REBOOT
     * HelpData : {"HelpValue":"ON"}
     * ErrorData : {"ErrorCode":"FATAL_ERROR","ErrorDeviceType":"8x8Printer","ErrorDeviceNum":"Printer0001","ErrorSerialNum":"91238501-2123-9"}
     * AssembleData : {"Product":"8x8 Flexbind Photo Book","ProductIdentifier":"DuplexPhotoBook8x8Flexbind"}
     */
    var EventId: String? = null
    var KNumber: String? = null
    var Type: String? = null
    var Status: String? = null
    var Responder: String? = null
    var CreationDateTime = 0
    var ResponseDateTime = 0
    var CompletionDateTime = 0
    var RemindMeDateTime = 0
    var CancellationReason: String? = null
    var HelpData: HelpDataBean? = null
    var ErrorData: ErrorDataBean? = null
    var AssembleData: AssembleDataBean? = null
    var NotificationText: String? = null
    var KPEXId: String? = null

    class HelpDataBean : Parcelable {
        /**
         * HelpValue : ON
         */
        var HelpValue: String? = null

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(HelpValue)
        }

        internal constructor() {}
        internal constructor(`in`: Parcel) {
            HelpValue = `in`.readString()
        }

        companion object {
            val CREATOR: Parcelable.Creator<HelpDataBean?> =
                object : Parcelable.Creator<HelpDataBean?> {
                    override fun createFromParcel(source: Parcel): HelpDataBean? {
                        return HelpDataBean(source)
                    }

                    override fun newArray(size: Int): Array<HelpDataBean?> {
                        return arrayOfNulls(size)
                    }
                }
        }

        object CREATOR : Parcelable.Creator<HelpDataBean> {
            override fun createFromParcel(parcel: Parcel): HelpDataBean {
                return HelpDataBean(parcel)
            }

            override fun newArray(size: Int): Array<HelpDataBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    class ErrorDataBean : Parcelable {
        /**
         * ErrorCode : FATAL_ERROR
         * ErrorDeviceType : 8x8Printer
         * ErrorDeviceNum : Printer0001
         * ErrorSerialNum : 91238501-2123-9
         */
        var ErrorCode: String? = null
        var ErrorDeviceType: String? = null
        var ErrorDeviceNum: String? = null
        var ErrorSerialNum: String? = null
        var ErrorDeviceDisplayText: String? = null

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(ErrorCode)
            dest.writeString(ErrorDeviceType)
            dest.writeString(ErrorDeviceNum)
            dest.writeString(ErrorSerialNum)
            dest.writeString(ErrorDeviceDisplayText)
        }

        internal constructor() {}
        internal constructor(`in`: Parcel) {
            ErrorCode = `in`.readString()
            ErrorDeviceType = `in`.readString()
            ErrorDeviceNum = `in`.readString()
            ErrorSerialNum = `in`.readString()
            ErrorDeviceDisplayText = `in`.readString()
        }

        companion object {
            val CREATOR: Parcelable.Creator<ErrorDataBean?> =
                object : Parcelable.Creator<ErrorDataBean?> {
                    override fun createFromParcel(source: Parcel): ErrorDataBean? {
                        return ErrorDataBean(source)
                    }

                    override fun newArray(size: Int): Array<ErrorDataBean?> {
                        return arrayOfNulls(size)
                    }
                }
        }

        object CREATOR : Parcelable.Creator<ErrorDataBean> {
            override fun createFromParcel(parcel: Parcel): ErrorDataBean {
                return ErrorDataBean(parcel)
            }

            override fun newArray(size: Int): Array<ErrorDataBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    class AssembleDataBean : Parcelable {
        /**
         * Product : 8x8 Flexbind Photo Book
         * ProductIdentifier : DuplexPhotoBook8x8Flexbind
         */
        var Product: String? = null
        var ProductIdentifier: String? = null

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(Product)
            dest.writeString(ProductIdentifier)
        }

        internal constructor() {}
        internal constructor(`in`: Parcel) {
            Product = `in`.readString()
            ProductIdentifier = `in`.readString()
        }

        companion object {
            val CREATOR: Parcelable.Creator<AssembleDataBean?> =
                object : Parcelable.Creator<AssembleDataBean?> {
                    override fun createFromParcel(source: Parcel): AssembleDataBean? {
                        return AssembleDataBean(source)
                    }

                    override fun newArray(size: Int): Array<AssembleDataBean?> {
                        return arrayOfNulls(size)
                    }
                }
        }

        object CREATOR : Parcelable.Creator<AssembleDataBean> {
            override fun createFromParcel(parcel: Parcel): AssembleDataBean {
                return AssembleDataBean(parcel)
            }

            override fun newArray(size: Int): Array<AssembleDataBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(EventId)
        dest.writeString(KNumber)
        dest.writeString(Type)
        dest.writeString(Status)
        dest.writeString(Responder)
        dest.writeInt(CreationDateTime)
        dest.writeInt(ResponseDateTime)
        dest.writeInt(CompletionDateTime)
        dest.writeInt(RemindMeDateTime)
        dest.writeString(CancellationReason)
        dest.writeString(NotificationText)
        dest.writeString(KPEXId)
        dest.writeParcelable(HelpData, flags)
        dest.writeParcelable(ErrorData, flags)
        dest.writeParcelable(AssembleData, flags)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        EventId = `in`.readString()
        KNumber = `in`.readString()
        Type = `in`.readString()
        Status = `in`.readString()
        Responder = `in`.readString()
        CreationDateTime = `in`.readInt()
        ResponseDateTime = `in`.readInt()
        CompletionDateTime = `in`.readInt()
        RemindMeDateTime = `in`.readInt()
        CancellationReason = `in`.readString()
        NotificationText = `in`.readString()
        KPEXId = `in`.readString()
        HelpData = `in`.readParcelable(HelpDataBean::class.java.classLoader)
        ErrorData = `in`.readParcelable(ErrorDataBean::class.java.classLoader)
        AssembleData = `in`.readParcelable(AssembleDataBean::class.java.classLoader)
    }

    override fun toString(): String {
        return "EventBean{" +
                "EventId='" + EventId + '\'' +
                ", KNumber='" + KNumber + '\'' +
                ", Type='" + Type + '\'' +
                ", Status='" + Status + '\'' +
                ", Responder='" + Responder + '\'' +
                ", CreationDateTime=" + CreationDateTime +
                ", ResponseDateTime=" + ResponseDateTime +
                ", CompletionDateTime=" + CompletionDateTime +
                ", RemindMeDateTime=" + RemindMeDateTime +
                ", CancellationReason='" + CancellationReason + '\'' +
                ", HelpData=" + HelpData +
                ", ErrorData=" + ErrorData +
                ", AssembleData=" + AssembleData +
                ", NotificationText='" + NotificationText + '\'' +
                ", KPEXId='" + KPEXId + '\'' +
                '}'
    }

    companion object {
        val CREATOR: Parcelable.Creator<EventBean?> = object : Parcelable.Creator<EventBean?> {
            override fun createFromParcel(source: Parcel): EventBean? {
                return EventBean(source)
            }

            override fun newArray(size: Int): Array<EventBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    object CREATOR : Parcelable.Creator<EventBean> {
        override fun createFromParcel(parcel: Parcel): EventBean {
            return EventBean(parcel)
        }

        override fun newArray(size: Int): Array<EventBean?> {
            return arrayOfNulls(size)
        }
    }
}
