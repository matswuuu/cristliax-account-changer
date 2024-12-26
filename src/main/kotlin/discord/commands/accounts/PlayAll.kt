package org.matswuuu.cristalixaccountchanger.discord.commands.accounts

import javafx.application.Platform
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.matswuuu.cristalixaccountchanger.controllers.Controller
import org.matswuuu.cristalixaccountchanger.controllers.TabController

object PlayAll : ListenerAdapter() {
    private const val NAME = "playall"

    val command = Commands.slash(NAME, "Запустить все аккаунты в группе")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return
        event.deferReply().setEphemeral(true).queue()

        val selectMenu = createMenu()

        event.hook
            .setEphemeral(true)
            .sendMessage("Выберите группу")
            .addActionRow(selectMenu)
            .queue()
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId != "playall-menu") return
        event.deferReply().setEphemeral(true).queue()

        event.message.delete().queue()

        event.hook
            .setEphemeral(true)
            .sendMessage("Успешно!")
            .queue()

        event.values.forEach {
            val tab = Controller.instance.accountsTabPane.tabs[it.toInt() - 1]
            val tabController = TabController.tabControllerMap[tab]!!

            Platform.runLater { tabController.playAllButton.fire() }
        }
    }

    private fun createMenu(): SelectMenu {
        val size = Controller.instance.accountsTabPane.tabs.size

        val builder = StringSelectMenu
            .create("playall-menu")
            .setPlaceholder("Группы")
            .setMinValues(1)
            .setMaxValues(size)

        for (i in 1..<size) {
            builder.addOption("Группа $i", i.toString(), "")
        }

        return builder.build()
    }
}