package org.matswuuu.cristalixaccountchanger.discord.commands.accounts

import javafx.application.Platform
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.matswuuu.cristalixaccountchanger.Main
import org.matswuuu.cristalixaccountchanger.controllers.Controller
import org.matswuuu.cristalixaccountchanger.controllers.TabController


object Token : ListenerAdapter() {
    private const val NAME = "token"

    val command = Commands.slash(NAME, "Добавить текущий аккаунт")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return
        event.deferReply().setEphemeral(true).queue()

        val selectMenu = StringSelectMenu
            .create("token-menu")
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
        if (event.componentId != "token-menu") return
        event.deferReply().setEphemeral(true).queue()

        event.message.delete().queue()

        val index = event.values[0].toInt()
        val tab = Controller.instance.accountsTabPane.tabs[index - 1]

        Platform.runLater { TabController.tabControllerMap[tab]!!.getTokenButton.fire() }

        event.hook
            .setEphemeral(true)
            .sendMessage("Успешно!")
            .queue()
    }
}