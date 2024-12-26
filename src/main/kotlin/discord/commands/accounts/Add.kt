package discord.commands.accounts

import javafx.application.Platform
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.matswuuu.cristalixaccountchanger.Account
import org.matswuuu.cristalixaccountchanger.Main
import org.matswuuu.cristalixaccountchanger.controllers.Controller
import org.matswuuu.cristalixaccountchanger.controllers.TabController


object Add : ListenerAdapter() {
    private const val NAME = "add"

    private val nickOption = OptionData(OptionType.STRING, "nick", "Ник", true)
    private val tokenOption = OptionData(OptionType.STRING, "token", "Токен", true)

    private val memoryOption = OptionData(OptionType.INTEGER, "memory", "RAM для игры")
        .setMinValue(-1)
        .setMaxValue(8192)
    private val minimalOption = OptionData(OptionType.BOOLEAN, "minimal", "Минимальные настройки графики")
    private val discordRPCOption = OptionData(OptionType.BOOLEAN, "discordrpc", "Отключить Discord RPC")
    private val autoEnterOption = OptionData(OptionType.BOOLEAN, "autoenter", "Автовход на сервер")
    private val fullscreenOption = OptionData(OptionType.BOOLEAN, "fullscreen", "Клиент в полный экран")
    private val debugModeOption = OptionData(OptionType.BOOLEAN, "debugmode", "Режим отладки")
    private val backgroundOption = OptionData(OptionType.INTEGER, "background", "Фон лаунчера")
        .addChoice("1", 1)
        .addChoice("2", 2)
        .addChoice("3", 3)
        .addChoice("4", 4)
        .addChoice("5", 5)
        .addChoice("6", 6)
        .addChoice("7", 7)


    val command = Commands.slash(NAME, "Добавить аккаунт")
        .addOptions(
            nickOption, tokenOption, memoryOption, minimalOption, discordRPCOption,
            autoEnterOption, fullscreenOption, debugModeOption, backgroundOption
        )

    private val map = HashMap<String, Account>()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return
        event.deferReply().setEphemeral(true).queue()

        val nick = event.getOption("nick")!!.asString
        val token = event.getOption("token")!!.asString

        val memory = event.getOption("memory")?.asInt ?: -1
        val discordRPC = event.getOption("discordRPC")?.asBoolean ?: true
        val minimal = event.getOption("minimal")?.asBoolean ?: false
        val autoEnter = event.getOption("autoEnter")?.asBoolean ?: false
        val fullscreen = event.getOption("fullscreen")?.asBoolean ?: false
        val debugMode = event.getOption("debugMode")?.asBoolean ?: false
        val background = event.getOption("background")?.asInt ?: 1

        map[event.member!!.id] = Account(
            nick,
            token,
            background - 1,
            minimal,
            discordRPC,
            autoEnter,
            fullscreen,
            debugMode,
            memory
        )

        val selectMenu = StringSelectMenu
            .create("add-menu")
            .setPlaceholder("Группы")

        for (i in 1..Main.tabsMap.size) {
            selectMenu.addOption("$i", "$i", "")
        }

        event.hook
            .setEphemeral(true)
            .sendMessage("Выберите номер группы")
            .addActionRow(selectMenu.build())
            .queue()
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId != "add-menu") return
        event.deferReply().setEphemeral(true).queue()

        event.message.delete().queue()

        val account = map[event.member!!.id]!!
        val index = event.values[0].toInt()
        val tab = Controller.instance.accountsTabPane.tabs[index - 1]

        Platform.runLater { TabController.tabControllerMap[tab]!!.addAccount(account) }

        event.hook
            .setEphemeral(true)
            .sendMessage("Успешно!")
            .queue()
    }
}