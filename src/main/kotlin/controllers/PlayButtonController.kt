package org.matswuuu.cristalixaccountchanger.controllers

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.Main
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class PlayButtonController {
    private fun writeLauncherJson(textField: TextField) {
        val launcherJson = TabController.readLauncherJson()
        val accountJson = LoginFieldController.getAccountJson(textField) ?: return

        val nick = accountJson.getString("nick")
        val accounts = JSONObject().put(nick, accountJson.getString("token"))

        launcherJson
                .put("accounts", accounts)
                .put("currentAccount", nick)
                .put("backgroundIndex", accountJson.getInt("backgroundIndex"))
                .put("minimalGraphics", accountJson.getBoolean("minimalGraphics"))
                .put("fullscreen", accountJson.getBoolean("fullscreen"))
                .put("discordRPC", !accountJson.getBoolean("discordRPC"))
                .put("autoEnter", accountJson.getBoolean("autoEnter"))
                .put("debugMode", accountJson.getBoolean("debugMode"))
                .put("memoryAmount", accountJson.getDouble("memoryAmount").toInt())

        val outputStream = FileOutputStream(Main.launcherJsonPath)
        IOUtils.write(launcherJson.toString(), outputStream, Charsets.UTF_8)
    }

    private fun startLauncher() {
        ProcessBuilder(Main.launcherExePath).start()
    }

    @FXML
    private fun play(event: ActionEvent) {
        val button = event.source as Button
        val gridPane = button.parent as GridPane

        val nodes: List<Node> = gridPane.children
        val buttonIndex = nodes.indexOf(button)
        val textField = nodes[buttonIndex - 1] as TextField

        writeLauncherJson(textField)
        startLauncher()
    }
}