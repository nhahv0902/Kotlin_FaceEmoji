package com.nhahv.faceemoji.networking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Created by hoang.van.nha on 12/18/2017.
 */
class NetworkReceiver : BroadcastReceiver() {
    private var networkListener: NetworkReceiverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            networkListener?.onNetworkConnectChange(isConnect(it))
        }
    }

    private fun isConnect(context: Context): Boolean {
        val connectManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }


    fun  setNetworkListener(listener: NetworkReceiverListener){
        networkListener = listener
    }
    interface NetworkReceiverListener {
        fun onNetworkConnectChange(isConnected: Boolean)
    }
}