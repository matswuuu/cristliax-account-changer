package discord

import discord.commands.accounts.Add
import discord.commands.accounts.Play
import discord.commands.accounts.Remove
import discord.commands.other.Help
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.matswuuu.cristalixaccountchanger.discord.commands.accounts.PlayAll
import org.matswuuu.cristalixaccountchanger.discord.commands.accounts.Settings
import org.matswuuu.cristalixaccountchanger.discord.commands.accounts.Token


object Discord : ListenerAdapter() {
    var token = ""
    lateinit var jda: JDA
    fun initialize(token: String) {
        this.token = token

        try {
            jda = JDABuilder
                .createDefault(token)
                .addEventListeners(
                    this,
                    Help,
                    Add, Remove, Token, Play, PlayAll, Settings
                )
                .build()
        } catch (_: InvalidTokenException) {}
    }

    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.updateCommands().addCommands(
            Help.command,
            Add.command, Remove.command, Token.command, Play.command,
            PlayAll.command, Settings.command
        ).queue()
    }
}