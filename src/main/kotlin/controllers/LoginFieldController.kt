package org.matswuuu.cristalixaccountchanger.controllers

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.Main

class LoginFieldController {
    companion object {
        fun getAccountJson(textField: TextField = Controller.lastField): JSONObject? {
            var accountJson: JSONObject? = null
            for (k in Main.tabsMap.values) {
                if (!k.keys.contains(textField)) continue

                for (v in k) {
                    if (v.key == textField) accountJson = v.value
                }
            }

            return accountJson
        }

        fun loginTextFieldFocused(ov: ObservableValue<out Boolean?>, oldV: Boolean, newV: Boolean) {
            if (!newV) return

            val textField = (ov as ReadOnlyBooleanProperty).bean as TextField
            Controller.lastField =  textField

            val accountJson = getAccountJson() ?: return
            val controller = Controller.instance

            controller.tokenTextField.text = accountJson.getString("token")
            controller.minimalGraphicSettingsCheckBox.isSelected = accountJson.getBoolean("minimalGraphics")
            controller.clientFullscreenCheckBox.isSelected = accountJson.getBoolean("fullscreen")
            controller.disableDiscordRPCCheckBox.isSelected = accountJson.getBoolean("discordRPC")
            controller.autoEnterCheckBox.isSelected = accountJson.getBoolean("autoEnter")
            controller.debugModeCheckBox.isSelected = accountJson.getBoolean("debugMode")

            val imageButton: Button = controller.backgroundsHBox.children[accountJson.getInt("backgroundIndex")] as Button
            imageButton.fire()

            val memory = accountJson.getDouble("memoryAmount")
            controller.setMemory(memory)
        }
    }

    @FXML
    lateinit var loginField: TextField

    @FXML
    private fun onKeyRelease() {
        getAccountJson()?.put("nick", loginField.text)
    }
}