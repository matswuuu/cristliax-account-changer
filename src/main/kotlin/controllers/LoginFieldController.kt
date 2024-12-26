package org.matswuuu.cristalixaccountchanger.controllers

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.matswuuu.cristalixaccountchanger.Account
import org.matswuuu.cristalixaccountchanger.Main

class LoginFieldController {
    companion object {
        fun getAccount(textField: TextField = Controller.lastField): Account? {
            var account: Account? = null
            for (k in Main.tabsMap.values) {
                if (!k.keys.contains(textField)) continue

                for (v in k) {
                    if (v.key == textField) account = v.value
                }
            }

            return account
        }

        fun loginTextFieldFocused(ov: ObservableValue<out Boolean?>, oldV: Boolean, newV: Boolean) {
            if (!newV) return

            val textField = (ov as ReadOnlyBooleanProperty).bean as TextField
            Controller.lastField = textField

            val account = getAccount() ?: return
            val controller = Controller.instance

            controller.tokenTextField.text = account.token
            controller.minimalGraphicSettingsCheckBox.isSelected = account.minimalGraphics
            controller.clientFullscreenCheckBox.isSelected = account.fullscreen
            controller.disableDiscordRPCCheckBox.isSelected = account.discordRPC
            controller.autoEnterCheckBox.isSelected = account.autoEnter
            controller.debugModeCheckBox.isSelected = account.debugMode

            val imageButton: Button = controller.backgroundsHBox.children[account.backgroundIndex] as Button
            imageButton.fire()

            controller.setMemory(account.memoryAmount)
        }
    }

    @FXML
    lateinit var loginField: TextField

    @FXML
    private fun onKeyRelease() {
        getAccount()?.nick = loginField.text
    }
}