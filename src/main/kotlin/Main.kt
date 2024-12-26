package org.matswuuu.cristalixaccountchanger

import com.google.gson.Gson
import discord.Discord
import javafx.application.Application
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.io.IOUtils
import org.json.JSONArray
import org.json.JSONObject
import org.matswuuu.cristalixaccountchanger.controllers.Controller
import org.matswuuu.cristalixaccountchanger.controllers.PlayButtonController
import org.matswuuu.cristalixaccountchanger.controllers.TabController
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL


class Main : Application() {

    override fun start(primaryStage: Stage) {
        PlayButtonController.StartThread.start()

        stage = primaryStage
        stage.icons.add(Image(javaClass.getResource("/images/icons/icon.png")?.toString()))

        stage.title = "Cristalix AccountChanger by matswuuu"
        stage.initStyle(StageStyle.TRANSPARENT)

        loadFonts()

        if (configFile.exists()) {
            showMain()
            loadConfig()
        } else {
            createConfigFile()
        }
    }

    private fun loadFonts() {
        Font.loadFont(javaClass.getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 14.0)
        Font.loadFont(javaClass.getResourceAsStream("/fonts/Montserrat-Medium.ttf"), 14.0)
        Font.loadFont(javaClass.getResourceAsStream("/fonts/Montserrat-SemiBold.ttf"), 14.0)
    }

    private fun createAlert() {
        val parent: Parent = FXMLLoader.load(Main::class.java.getResource("/fxml/alert.fxml"))
        val scene = Scene(parent)
        scene.fill = Color.TRANSPARENT

        stage.scene = scene
        stage.show()
    }

    private fun loadConfig() {
        val inputStream = FileInputStream(configFile)
        val jsonText = IOUtils.toString(inputStream, Charsets.UTF_8)

        val configJson = JSONObject(jsonText)


        if (configJson.has("discordToken")) {
            val discordToken = configJson.getString("discordToken")

            if (discordToken.isNotEmpty()) {
                Controller.instance.discordTextField.text = discordToken
                Discord.initialize(discordToken)
            }
        }

        if (!configJson.has("launcherExePath") || !configJson.has("launcherJsonPath")) {
            createConfigFile()
            return
        }

        launcherExePath = configJson.getString("launcherExePath")
        launcherJsonPath = configJson.getString("launcherJsonPath")

        for (tabJson in configJson.getJSONArray("tabs") as JSONArray) {
            val tab = Controller.instance.createTab()

            for (json in tabJson as JSONArray) {
                TabController.tabControllerMap[tab]!!.addAccount(
                    Gson().fromJson(json.toString(), Account::class.java)
                )
            }
        }

        val tabs = Controller.instance.accountsTabPane.tabs

        TabController.tabControllerMap[tabs[0]]!!.closeTab()
    }

    private fun createConfigFile() {
        configFile.createNewFile()

        val outputStream = FileOutputStream(configFile)
        IOUtils.write(JSONObject().toString(), outputStream, Charsets.UTF_8)

        launcherExePath = ""
        launcherJsonPath = ""

        createAlert()
        Thread(checkPaths()).start()
    }

    private fun showMain() {
        val parent: Parent = FXMLLoader.load(Main::class.java.getResource("/fxml/main.fxml"))
        val scene = Scene(parent)
        scene.fill = Color.TRANSPARENT

        stage.scene = scene
        stage.show()
    }

    private fun checkPaths(): Task<Void> {
        val task = object : Task<Void>() {
            override fun call(): Void? {
                if (launcherExePath.isEmpty() || !File(launcherExePath).exists()) downloadLauncher()
                if (launcherJsonPath.isEmpty() || !File(launcherJsonPath).exists()) searchFile("C:/")

                return null
            }
        }

        task.setOnSucceeded { showMain() }

        return task
    }

    private fun downloadLauncher() {
//        val url = URL("https://webdata.c7x.dev/sworroo/CristalixLauncher.exe")
        val url = URL("https://download.jetbrains.com/idea/ideaIU-243.15521.24.exe")
        val bis = BufferedInputStream(url.openStream())

        val launcherExeFile = File("CristalixLauncher.exe")
        val fos = FileOutputStream(launcherExeFile)

        val buffer = ByteArray(1024)
        var count: Int

        while (bis.read(buffer, 0, 1024).also { count = it } != -1) {
            fos.write(buffer, 0, count)
        }

        launcherExePath = launcherExeFile.absolutePath

        fos.close()
        bis.close()
    }


    private fun searchFile(path: String) {
        val file = File(path)
        if (file.isFile && file.toString().endsWith(".launcher")) {
            launcherJsonPath = file.absolutePath
            return
        }

        val listFiles = file.listFiles()
        if (file.isFile || (listFiles == null)) return

        for (listFile in listFiles) {
            searchFile(listFile.absolutePath)
        }
    }

    companion object {
        val tabsMap = LinkedHashMap<Tab, LinkedHashMap<TextField, Account>>()

        val configFile = File("config.json")

        lateinit var launcherExePath: String
        lateinit var launcherJsonPath: String

        lateinit var stage: Stage

        fun saveConfig() {
            val configJson = JSONObject()

            val tabJsonArray = JSONArray()
            tabsMap.values.forEach { v ->
                val tabJson = JSONArray()
                v.values.forEach { tabJson.put(Gson().toJson(it)) }

                tabJsonArray.put(tabJson)
            }

            configJson
                .put("launcherExePath", launcherExePath)
                .put("launcherJsonPath", launcherJsonPath)
                .put("discordToken", Discord.token)
                .put("tabs", tabJsonArray)

            val outputStream = FileOutputStream(configFile)
            IOUtils.write(configJson.toString(), outputStream, Charsets.UTF_8)
        }
    }
}

fun main() {
    Application.launch(Main::class.java)
}

