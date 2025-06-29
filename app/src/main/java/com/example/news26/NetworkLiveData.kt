package com.example.news26

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData

class NetworkLiveData(private val connectivityManager: ConnectivityManager): LiveData<Boolean>() {
    constructor(application: Application) : this(
        application.getSystemService(ConnectivityManager::class.java)
    )
    private val networkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        postValue(
            capabilities != null && ( (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) )
                    || (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ) || ( capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ) )
        )
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}
