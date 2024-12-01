package com.dicoding.learn.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.learn.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val storyRepository = Injection.provideStoryRepository(this.applicationContext)
        return StackRemoteViewsFactory(this.applicationContext, storyRepository)
    }
}