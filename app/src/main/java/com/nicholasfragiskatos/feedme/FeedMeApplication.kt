package com.nicholasfragiskatos.feedme

import android.app.Application
import com.nicholasfragiskatos.feedme.data.local.FeedMeDatabase

class FeedMeApplication : Application() {

    val database by lazy {
        FeedMeDatabase.getInstance(applicationContext)
    }
}
