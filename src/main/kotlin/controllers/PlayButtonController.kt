package org.matswuuu.cristalixaccountchanger.controllers

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.Main
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArrayList

class PlayButtonController {
    @FXML
    private fun play(event: ActionEvent) {
        val button = event.source as Button
        val gridPane = button.parent as GridPane

        val nodes = gridPane.children
        val buttonIndex = nodes.indexOf(button)
        val textField = nodes[buttonIndex - 1] as TextField

        queue.add(textField)
    }

    companion object {
        private val queue = CopyOnWriteArrayList<TextField>()

        private fun writeLauncherJson(textField: TextField) {
            val launcherJson = TabController.readLauncherJson()
            val account = LoginFieldController.getAccount(textField) ?: return

            val accounts = JSONObject().put(account.nick, account.token)

            launcherJson
                .put("accounts", accounts)
                .put("currentAccount", account.nick)
                .put("backgroundIndex", account.backgroundIndex)
                .put("minimalGraphics", account.minimalGraphics)
                .put("fullscreen", account.fullscreen)
                .put("discordRPC", !account.discordRPC)
                .put("autoEnter", account.autoEnter)
                .put("debugMode", account.debugMode)
                .put("memoryAmount", account.memoryAmount)

            val outputStream = FileOutputStream(Main.launcherJsonPath)
            IOUtils.write(launcherJson.toString(), outputStream, Charsets.UTF_8)
        }

        private fun startLauncher() {
            ProcessBuilder(Main.launcherExePath).start()
        }
    }

    object StartThread : Thread() {
        var running = true
        override fun run() {
            while (running) {
                if (queue.isEmpty()) sleep(1_000)

                queue.forEach {
                    queue.remove(it)

                    writeLauncherJson(it)
                    startLauncher()

                    sleep(10_000)
                }
            }
        }
    }
}