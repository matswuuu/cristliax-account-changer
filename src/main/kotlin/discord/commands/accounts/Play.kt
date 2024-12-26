package discord.commands.accounts

import javafx.application.Platform
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.matswuuu.cristalixaccountchanger.Main
import org.matswuuu.cristalixaccountchanger.controllers.Controller
import org.matswuuu.cristalixaccountchanger.controllers.TabController

object Play : ListenerAdapter() {
    private const val NAME = "play"

    val command = Commands.slash(NAME, "Запустить аккаунт")

    private val previous = Button.primary("play-previous", "<-")
    private val next = Button.primary("play-next", "->")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return
        event.deferReply().setEphemeral(true).queue()

        val index = 1
        val selectMenu = createMenu(index)

        event.hook
            .setEphemeral(true)
            .sendMessage("Выберите аккаунт. Группа **#$index**")
            .addActionRow(selectMenu)
            .addActionRow(previous, next)
            .queue()
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId != "play-menu") return
        event.deferReply().setEphemeral(true).queue()

        event.message.delete().queue()

        event.hook
            .setEphemeral(true)
            .sendMessage("Успешно!")
            .queue()

        val index = event.message.contentStripped.substringAfter("#").toInt()
        val nick = event.values[0]

        val tab = Controller.instance.accountsTabPane.tabs[index - 1]
        Main.tabsMap[tab]!!.forEach { (k, v) ->
            if (v.nick == nick) {
                val tabController = TabController.tabControllerMap[tab]!!
                val nodes = tabController.accountsGridPane.children
                val button = nodes[nodes.indexOf(k) + 1] as javafx.scene.control.Button

                Platform.runLater { button.fire() }
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        var index = event.message.contentStripped.substringAfter("#").toInt()

        when (event.componentId) {
            "play-previous" -> index -= 1
            "play-next" -> index += 1
            else -> return
        }

        val size = Controller.instance.accountsTabPane.tabs.size - 1
        if (index < 1) index = size else if (index > size) index = 1

        val components = event.message.actionRows

        val selectMenu = createMenu(index)
        components[0] = ActionRow.of(selectMenu)

        event
            .editMessage("Выберите аккаунт. Группа **#$index**")
            .setComponents(components)
            .queue()
    }

    private fun createMenu(index: Int): SelectMenu {
        val builder = StringSelectMenu
            .create("play-menu")
            .setPlaceholder("Группа $index")

        val tab = Controller.instance.accountsTabPane.tabs[index - 1]
        for (account in Main.tabsMap[tab]!!.values) {
            if (
                (builder.options.find { it.label == account.nick } != null) ||
                builder.options.size >= 25 ||
                account.nick.isEmpty()
            ) continue

            builder.addOption(account.nick, account.nick, "")
        }

        return builder.build()
    }
}