package org.matswuuu.cristalixaccountchanger.controllers

import javafx.fxml.FXML
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class AlertController {
    @FXML
    lateinit var root: AnchorPane

    private var x = 0.0
    private var y = 0.0

    @FXML
    private fun windowDragged(event: MouseEvent) {
        val stage: Stage = root.scene.window as Stage

        stage.y = event.screenY - y
        stage.x = event.screenX - x
    }

    @FXML
    private fun windowPressed(event: MouseEvent) {
        x = event.sceneX
        y = event.sceneY
    }

    @FXML
    private fun close() {
        val stage: Stage = root.scene.window as Stage
        stage.close()
    }
}