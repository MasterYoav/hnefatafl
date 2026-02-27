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
import java.awt.Frame
import java.io.File
import javax.swing.JColorChooser
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
                if (isDarkMode) "NSAppearanceNameVibrantDark" else "NSAppearanceNameAqua",
            )
        }
        VikingsChessApp(
            onPickImage = { pickImagePath(window) },
            onPickColor = { current -> pickColorHex(window as? Frame, current) },
            imagePainter = ::loadImagePainter,
            onThemeChanged = { isDarkMode = it },
            onTransparencyModeChanged = { transparent ->
                window.background = if (transparent) java.awt.Color(0, 0, 0, 0) else java.awt.Color(11, 18, 32, 255)
            },
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

private fun pickColorHex(frame: Frame?, currentHex: String): String? {
    val initial = runCatching {
        java.awt.Color.decode(if (currentHex.startsWith("#")) currentHex else "#$currentHex")
    }.getOrNull() ?: java.awt.Color.WHITE
    val chosen = JColorChooser.showDialog(frame, "Choose color", initial) ?: return null
    return String.format("#%02X%02X%02X", chosen.red, chosen.green, chosen.blue)
}

private fun loadImagePainter(path: String): Painter? {
    val file = File(path)
    if (!file.exists()) return null
    return runCatching {
        val image = Image.makeFromEncoded(file.readBytes())
        BitmapPainter(image.toComposeImageBitmap())
    }.getOrNull()
}
