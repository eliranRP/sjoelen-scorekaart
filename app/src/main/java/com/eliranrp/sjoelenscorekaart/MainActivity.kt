package com.eliranrp.sjoelenscorekaart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eliranrp.sjoelenscorekaart.ui.ScorekaartRoute
import com.eliranrp.sjoelenscorekaart.ui.theme.SjoelenScorekaartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SjoelenScorekaartTheme {
                ScorekaartRoute()
            }
        }
    }
}
