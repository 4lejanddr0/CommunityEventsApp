package com.tuempresa.communityeventsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.tuempresa.communityeventsapp.ui.navigation.AppNavGraph
import com.tuempresa.communityeventsapp.ui.theme.CommunityEventsAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommunityEventsAppTheme {
                AppNavGraph()
            }
        }
    }
}
