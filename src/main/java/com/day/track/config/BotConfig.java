package com.day.track.config;

import com.day.track.listner.DiscordEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

	@Value("${discord.bot.token}")
	private String token;

	private final DiscordEventListener listener;

	public BotConfig(DiscordEventListener listener) {
		this.listener = listener;
	}

	@Bean
	public JDA jda() throws Exception {
		JDA jda = JDABuilder.createDefault(token)
				.enableIntents(
						GatewayIntent.MESSAGE_CONTENT,
						GatewayIntent.GUILD_MEMBERS
				)
				.addEventListeners(listener)
				.build();

		jda.awaitReady();
		return jda;
	}
}