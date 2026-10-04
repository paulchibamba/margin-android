package com.paulchibamba.margin

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.paulchibamba.margin.feature.appearance.ProvideAppearance
import com.paulchibamba.margin.feature.tracking.SessionLifecycle
import com.paulchibamba.margin.navigation.MarginApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionLifecycle: SessionLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) sessionLifecycle.noteLaunch(intent)
        enableEdgeToEdge()
        setContent { ProvideAppearance { MarginApp() } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        sessionLifecycle.noteLaunch(intent)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) sessionLifecycle.onInput()
        return super.dispatchTouchEvent(event)
    }
}
