package com.dicoding.learn

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBoundService : Service() {

    companion object {
        private val TAG = MyBoundService::class.java.simpleName
    }

    private var binder = MyBinder()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    val numberLiveData: MutableLiveData<Int> = MutableLiveData()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "OnBind:")
        serviceScope.launch {
            for (i in 1..50) {
                delay(1000)
                Log.d(TAG, "Do something $i")
                numberLiveData.postValue(i)
            }
            Log.d(TAG, "Service Dihentikan")
        }
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy:")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "OnUnbind:")
        serviceJob.cancel()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "OnRebind:")
    }

    internal inner class MyBinder: Binder() {
        val getService: MyBoundService = this@MyBoundService
    }
}