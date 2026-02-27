package com.vikingschess.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vikingschess.ui.VikingsChessApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Hnefatafl") {
        window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
        window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
        VikingsChessApp()
    }
}
