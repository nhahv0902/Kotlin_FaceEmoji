package com.nhahv.faceemoji

import android.app.Application



/**
 * Created by nhahv0902 on 10/20/17.
 */
class AppApplication : Application() {
    companion object {
        val CREATIVE_SDK_CLIENT_ID = "ed7e8408383f4816aafccbd91dec8d3b"
        val CREATIVE_SDK_CLIENT_SECRET = "14cc52cf-3606-48cf-9123-9eac56460537"
        val CREATIVE_SDK_REDIRECT_URI = "ams+4ad97b7fd622cda05fdeec0a30b5b7f6e4da3fbf://adobeid/ed7e8408383f4816aafccbd91dec8d3b"
        val CREATIVE_SDK_SCOPES = arrayOf("email", "profile", "address")
    }

    override fun onCreate() {
        super.onCreate()
//        AdobeCSDKFoundation.initializeCSDKFoundation(applicationContext)
//        val builder = StrictMode.VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
    }

}