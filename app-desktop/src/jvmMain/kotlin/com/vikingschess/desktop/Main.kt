package com.vikingschess.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vikingschess.ui.VikingsChessApp
import java.awt.FileDialog
import java.io.File
import org.jetbrains.skia.Image

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Hnefatafl",
        transparent = false,
    ) {
        var isDarkMode by mutableStateOf(true)
        window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
        window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
        if (isMacOs()) {
            window.rootPane.putClientProperty("apple.awt.windowTitleVisible", true)
            window.rootPane.putClientProperty(
                "apple.awt.windowTitleBarAppearance",
                if (isDarkMode) "NSAppearanceNameDarkAqua" else "NSAppearanceNameAqua",
            )
        }
        VikingsChessApp(
            onPickImage = { pickImagePath(window) },
            imagePainter = ::loadImagePainter,
            onThemeChanged = { isDarkMode = it },
        )
    }
}

private fun isMacOs(): Boolean =
    System.getProperty("os.name")?.contains("Mac", ignoreCase = true) == true

private fun pickImagePath(window: java.awt.Window): String? {
    val dialog = FileDialog(window as? java.awt.Frame, "Choose background image", FileDialog.LOAD)
    dialog.setVisible(true)
    val fileName = dialog.getFile() ?: return null
    val directory = dialog.getDirectory() ?: return null
    return File(directory, fileName).absolutePath
}

private fun loadImagePainter(path: String): Painter? {
    val file = File(path)
    if (!file.exists()) return null
    return runCatching {
        val image = Image.makeFromEncoded(file.readBytes())
        BitmapPainter(image.toComposeImageBitmap())
    }.getOrNull()
}
