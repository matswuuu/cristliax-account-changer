package org.matswuuu.cristalixaccountchanger.discord.commands.accounts

import net.dv8tion.jda.api.EmbedBuilder
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

object Settings : ListenerAdapter() {
    private const val NAME = "settings"

    val command = Commands.slash(NAME, "Настройки аккаунта")

    private val previous = Button.primary("settings-previous", "<-")
    private val next = Button.primary("settings-next", "->")

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
        if (event.componentId != "settings-menu") return
        event.deferReply().setEphemeral(true).queue()

        event.message.delete().queue()

        val index = event.message.contentStripped.substringAfter("#").toInt()
        val nick = event.values[0]

        val tab = Controller.instance.accountsTabPane.tabs[index - 1]
        val account = Main.tabsMap[tab]!!.values.filter { it.nick == nick }[0]

        val embed = EmbedBuilder()
            .setTitle(account.nick)
            .setColor(0xB360F7)
            .setDescription(
                "Минимальныe настройки графики: ${check(account.minimalGraphics)}\n" +
                        "Отключить Discord RPC: ${check(account.discordRPC)}\n" +
                        "Автовход на сервер: ${check(account.autoEnter)}\n" +
                        "Клиент в полный экран: ${check(account.fullscreen)}\n" +
                        "Режим отладки: ${check(account.debugMode)}\n" +
                        "Фон лаунчера: ${account.backgroundIndex + 1}\n" +
                        "Токен: ||${account.token}||"
            )
            .setAuthor(
                "matswuuu",
                "https://github.com/matswuuu",
                "https://avatars.githubusercontent.com/u/115926657?s=400&u=9897fc4258f1bbd53a1832f680c706bfa4ab88b6&v=4"
            )
            .build()

        event.hook
            .setEphemeral(true)
            .sendMessageEmbeds(embed)
            .queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        var index = event.message.contentStripped.substringAfter("#").toInt()

        when (event.componentId) {
            "settings-previous" -> index -= 1
            "settings-next" -> index += 1
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
            .create("settings-menu")
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

    private fun check(b: Boolean): String {
        return if (b) "✅" else "❌"
    }
}