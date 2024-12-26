package org.matswuuu.cristalixaccountchanger.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.Account
import org.matswuuu.cristalixaccountchanger.Main
import java.io.FileInputStream


class TabController {
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

    fun addAccount(account: Account) {
        if (accountsGridPane.children.size == 50) return

        val loginTextField: TextField = FXMLLoader.load(javaClass.getResource("/fxml/loginTextField.fxml"))
        val playButton: Button = FXMLLoader.load(javaClass.getResource("/fxml/playButton.fxml"))

        loginTextField.text = account.nick
        loginTextField.focusedProperty().addListener(LoginFieldController::loginTextFieldFocused)

        Controller.lastField = loginTextField
        loginTextField.requestFocus()

        val rowIndex = accountsGridPane.children.size / 2 + 1

        accountsGridPane.add(loginTextField, 1, rowIndex)
        accountsGridPane.add(playButton, 3, rowIndex)

        Main.tabsMap[tab]!![loginTextField] = account
    }

    @FXML
    private fun onGetTokenButtonClick() {
        val launcherJson = readLauncherJson()
        val currentAccount = launcherJson.getString("currentAccount")
        val token = launcherJson.getJSONObject("accounts").getString(currentAccount)

        val account = Account(
            currentAccount,
            token,
            launcherJson.getInt("backgroundIndex"),
            launcherJson.getBoolean("minimalGraphics"),
            launcherJson.getBoolean("fullscreen"),
            launcherJson.getBoolean("discordRPC"),
            launcherJson.getBoolean("autoEnter"),
            launcherJson.getBoolean("debugMode"),
            launcherJson.getInt("memoryAmount")
        )

        addAccount(account)
    }

    @FXML
    private fun onAddAccountButtonClick() {
        addAccount(Account())
    }

    @FXML
    private fun onPlayAllButtonClick() {
        for (node in accountsGridPane.children) {
            if (accountsGridPane.children.indexOf(node) % 2 == 0) continue

            val playButton = (node as Button)
            playButton.fire()
        }
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

    companion object {
        val tabControllerMap = HashMap<Tab, TabController>()

        fun readLauncherJson(): JSONObject {
            val inputStream = FileInputStream(Main.launcherJsonPath)
            val jsonText = IOUtils.toString(inputStream, Charsets.UTF_8)

            return JSONObject(jsonText)
        }
    }
}