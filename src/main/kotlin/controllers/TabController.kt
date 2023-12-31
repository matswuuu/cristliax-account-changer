package org.matswuuu.cristalixaccountchanger.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.Main
import java.io.FileInputStream
import java.io.InputStream


class TabController {
    companion object {
        val tabControllerMap = HashMap<Tab, TabController>()

        fun readLauncherJson() : JSONObject {
            val inputStream: InputStream = FileInputStream(Main.launcherJsonPath)
            val jsonText: String = IOUtils.toString(inputStream, Charsets.UTF_8)

            return JSONObject(jsonText)
        }
    }

    @FXML
    lateinit var tab: Tab

    @FXML
    lateinit var getTokenButton: Button

    @FXML
    lateinit var addAccountButton: Button

    @FXML
    lateinit var playAllButton: Button

    @FXML
    lateinit var accountsGridPane: GridPane

    @FXML
    private fun initialize() {
        tabControllerMap[tab] = this
    }

    fun addAccount(accountJson: JSONObject) {
        val loginTextField: TextField = FXMLLoader.load(Main::class.java.getResource("/fxml/loginTextField.fxml"))
        val playButton: Button = FXMLLoader.load(Main::class.java.getResource("/fxml/playButton.fxml"))

        loginTextField.text = accountJson.getString("nick")
        loginTextField.focusedProperty().addListener(LoginFieldController::loginTextFieldFocused)

        Controller.lastField = loginTextField
        loginTextField.requestFocus()

        val rowIndex = accountsGridPane.children.size / 2 + 1

        accountsGridPane.add(loginTextField, 1, rowIndex)
        accountsGridPane.add(playButton, 3, rowIndex)

        Main.tabsMap[tab]?.put(loginTextField, accountJson)
    }

    @FXML
    private fun onGetTokenButtonClick() {
        val launcherJson = readLauncherJson()
        val currentAccount = launcherJson.getString("currentAccount")
        val token = launcherJson.getJSONObject("accounts").getString(currentAccount)

        val accountJson = JSONObject()
                .put("nick", currentAccount)
                .put("token", token)
                .put("backgroundIndex", launcherJson.getInt("backgroundIndex"))
                .put("minimalGraphics", launcherJson.getBoolean("minimalGraphics"))
                .put("fullscreen", launcherJson.getBoolean("fullscreen"))
                .put("discordRPC", !launcherJson.getBoolean("discordRPC"))
                .put("autoEnter", launcherJson.getBoolean("autoEnter"))
                .put("debugMode", launcherJson.getBoolean("debugMode"))
                .put("memoryAmount", launcherJson.getInt("memoryAmount").toDouble())

        addAccount(accountJson)
    }

    @FXML
    private fun onAddAccountButtonClick() {
        val accountJson = JSONObject()
                .put("nick", "")
                .put("token", "")
                .put("backgroundIndex", 0)
                .put("minimalGraphics", false)
                .put("fullscreen", false)
                .put("discordRPC", false)
                .put("autoEnter", false)
                .put("debugMode", false)
                .put("memoryAmount", -1.0)

        addAccount(accountJson)
    }

    @FXML
    private fun onPlayAllButtonClick() {
        val task = Runnable {
            for (node in accountsGridPane.children) {
                if (accountsGridPane.children.indexOf(node) % 2 == 0) continue
                Thread.sleep(10000)

                val playButton = (node as Button)
                playButton.fire()
            }
        }

        Thread(task).start()
    }

    @FXML
    fun closeTab() {
        val tabs = Controller.instance.accountsTabPane.tabs

        tabs.remove(tab)
        Main.tabsMap.remove(tab)

        for (i in 0 until tabs.size) {
            if (tabs[i] == Controller.instance.addNewTab) continue

            tabs[i].text = "${i + 1} группа"
        }
    }
}