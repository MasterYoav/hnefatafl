package com.vikingschess.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vikingschess.ui.VikingsChessApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VikingsChessApp(
                supportsTransparentMode = false,
                supportsImagePicker = false,
            )
        }
    }
}
