package discord.commands.other

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands


object Help : ListenerAdapter() {
    private const val NAME = "help"

    val command = Commands.slash(NAME, "Справка по командам")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return
        event.deferReply().setEphemeral(true).queue()

        val embed = EmbedBuilder()
            .setTitle("Cristalix AccountChanger by matswuuu")
            .setColor(0xB360F7)
            .setDescription(
                "Программа для смены аккаунтов в лаунчере Cristalix\n" +
                        "-> https://github.com/matswuuu\n" +
                        "## Команды:\n" +
                        "**help** - справка по командам\n\n" +
                        "**add** - добавить аккаунт\n" +
                        "**remove** - удалить аккаунт\n" +
                        "**token** - добавить текущий аккаунт\n" +
                        "**settings** - настройки аккаунта\n" +
                        "**play** - запустить аккаунт\n" +
                        "**playall** - запустить все аккаунты в группе\n"
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
}