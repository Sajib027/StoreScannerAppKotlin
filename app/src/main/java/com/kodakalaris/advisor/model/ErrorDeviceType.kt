package com.kodakalaris.advisor.model

class ErrorDeviceType {
    var ErrorDeviceType = ""
        set(ErrorDeviceType) {
            field = ErrorDeviceType
        }
    var GermanName = ""
        set(GermanName) {
            field = GermanName
        }
    var EnglishName = ""
        set(EnglishName) {
            field = EnglishName
        }

    constructor() {}
    constructor(deviceType: String, germanName: String, englishName: String) {
        this.ErrorDeviceType = deviceType
        this.GermanName = germanName
        this.EnglishName = englishName
    }

}