package com.listener.dynamicimages

import android.net.Uri
import android.util.Log

/**
 * Contract for clients to use when requesting icons
 */

interface ImageContract {

    companion object {
        val TAG: String = ImageContract::class.java.simpleName

        //Authority defined in manifest
        const val AUTHORITY: String = "com.listener.dynamicimage"

        //Base Uri from which all ERT uris are derived
        private val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

        //Strings that our used to build the routes of our Authority's API
        const val PATH_LOGO: String = "logo"
    }

    class Logo {
        companion object {
            @JvmStatic
            //API endpoint
            val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOGO).build()

            fun buildUriByMonth(monthAsInt: Int): Uri =
                CONTENT_URI.buildUpon().appendPath(monthAsInt.toString()).build()

            fun getMonth(logoUri: Uri): Int? {
                return logoUri.pathSegments[1].toInt()
            }
        }
    }
}