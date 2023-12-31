package org.matswuuu.cristalixaccountchanger.controllers

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.stage.Stage
import org.matswuuu.cristalixaccountchanger.Main


class Controller {
    companion object {
        lateinit var instance: Controller
        lateinit var lastField: TextField
    }

    @FXML
    lateinit var root: AnchorPane

    @FXML
    lateinit var accountsTabPane: TabPane

    @FXML
    lateinit var addNewTab: Tab

    @FXML
    lateinit var autoMemoryCheckBox: CheckBox

    @FXML
    lateinit var clientFullscreenCheckBox: CheckBox

    @FXML
    lateinit var autoEnterCheckBox: CheckBox

    @FXML
    lateinit var debugModeCheckBox: CheckBox

    @FXML
    lateinit var disableDiscordRPCCheckBox: CheckBox

    @FXML
    lateinit var minimalGraphicSettingsCheckBox: CheckBox

    @FXML
    lateinit var memoryLabel: Label

    @FXML
    lateinit var memorySlider: Slider

    @FXML
    lateinit var tokenTextField: PasswordField

    @FXML
    lateinit var backgroundsHBox: HBox

    @FXML
    lateinit var removeAccountButton: Button

    private var x = 0.0
    private var y = 0.0
    private lateinit var lastImage: Button

    init {
        instance = this
    }

    fun setMemory(memory: Double) {
        val auto = memory == -1.0
        memorySlider.isDisable = auto
        autoMemoryCheckBox.isSelected = auto

        if (auto) {
            memoryLabel.text = "RAM для игры: автоматически"
        } else {
            memorySlider.value = memory
            memoryLabel.text = "RAM для игры: ${memory.toInt()}"
        }
    }

    @FXML
    fun initialize() {
        memorySlider.valueProperty().addListener { _, _, newV: Number ->
            setMemory(newV.toDouble())

            onSettingsNodeAction()
        }

        lastImage = backgroundsHBox.children[0] as Button
        lastImage.fire()
    }

    @FXML
    private fun onAutoMemorySelect() {
        val memory = if (autoMemoryCheckBox.isSelected) -1.0 else memorySlider.value
        setMemory(memory)

        onSettingsNodeAction()
    }

    fun createTab() : Tab {
        val tabs = accountsTabPane.tabs
        val tab: Tab = FXMLLoader.load(Main::class.java.getResource("/fxml/tab.fxml"))
        tab.text = "${tabs.size} группа"

        tabs.add(tab)
        Main.tabsMap[tab] = LinkedHashMap()

        accountsTabPane.selectionModel.selectLast()

        tabs.remove(addNewTab)
        tabs.add(addNewTab)

        return tab
    }

    @FXML
    private fun onTabSelected() {
        val tab = createTab()

        val tabController = TabController.tabControllerMap[tab] ?: return
        tabController.addAccountButton.fire()
    }

    @FXML
    fun onSettingsNodeAction() {
        val memory = if (autoMemoryCheckBox.isSelected) -1.0 else memorySlider.value

        val accountJson = LoginFieldController.getAccountJson() ?: return
        accountJson
                .put("token", tokenTextField.text)
                .put("minimalGraphics", minimalGraphicSettingsCheckBox.isSelected)
                .put("fullscreen", clientFullscreenCheckBox.isSelected)
                .put("discordRPC", disableDiscordRPCCheckBox.isSelected)
                .put("autoEnter", autoEnterCheckBox.isSelected)
                .put("debugMode", debugModeCheckBox.isSelected)
                .put("memoryAmount", memory)
    }

    private fun getTab(): Tab? {
        for (v in Main.tabsMap) {
            for (k in v.value.keys) {
                if (k == lastField) return v.key
            }
        }
        return null
    }

    private fun setBackground() {
        val imageIndex = backgroundsHBox.children.indexOf(lastImage)

        val image = Image(Main::class.java.getResource("/images/backgrounds/$imageIndex.png")?.toString() ?: return)
        val size = BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
        val background = Background(BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size))

        root.background = background
        val accountJson = Main.tabsMap[getTab()]?.get(lastField) ?: return
        accountJson.put("backgroundIndex", imageIndex)
    }

    @FXML
    private fun onImageButtonClick(event: ActionEvent) {
        val button = event.source as Button

        lastImage.isDisable = false
        button.isDisable = true

        lastImage = button
        setBackground()
    }

    @FXML
    private fun onRemoveAccountButtonClick() {
        val currentTab = getTab()
        val tabController = TabController.tabControllerMap[currentTab]
        val nodes = tabController?.accountsGridPane?.children ?: return

        val index = nodes.indexOf(lastField)

        if (nodes.size == 2) {
            val tabs = accountsTabPane.tabs
            if (tabs.size == 2) return

            tabController.closeTab()

            val firstTabController = TabController.tabControllerMap[tabs[0]] ?: return
            lastField = firstTabController.accountsGridPane.children[0] as TextField

            accountsTabPane.selectionModel.select(tabs[0])
        } else {
            nodes.remove(nodes[index + 1])
            nodes.remove(lastField)

            Main.tabsMap[currentTab]?.remove(lastField)

            lastField = nodes[0] as TextField
        }

        lastField.requestFocus()
    }

    @FXML
    private fun close() {
        Main.saveConfig()

        val stage: Stage = root.scene.window as Stage
        stage.close()
    }

    @FXML
    private fun minimize() {
        val stage: Stage = root.scene.window as Stage
        stage.setIconified(true)
    }

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
}