package com.day.track.scheduler;

import com.day.track.listner.DiscordEventListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyReminderScheduler {

	private final JDA jda;
	private final DiscordEventListener listener;

	@Scheduled(cron = "0 55 11 * * ?", zone = "Asia/Kolkata")
	public void sendDailyReminder() {

		jda.getGuilds().forEach(guild -> {
			guild.loadMembers().onSuccess(members -> {
				members.stream()
						.filter(member -> !member.getUser().isBot())
						.forEach(member ->
								listener.startCheckin(member.getUser().getId())
						);
			});
		});
	}
}