package com.kodakalaris.advisor.utils

import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.model.APIError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

/**
 * handling api error util
 */
object APIErrorUtil {
    fun parseError(response: Response<*>): APIError {
        val mBaseUrl: String = Helper.getBaseUrl(AppController.appContext)!!
        val converter: Converter<ResponseBody, APIError> = AppController.getRetrofit()
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))
        val error: APIError
        error = try {
            converter.convert(response.errorBody())
        } catch (e: IOException) {
            return APIError()
        }
        return error
    }
}
