package com.day.track.listner;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiscordEventListener extends ListenerAdapter {

	private final Map<String, List<String>> userAnswers = new HashMap<>();

	@Value("${discord.report.channel-id}")
	private String reportChannelId;

	private final JDA jda;

	public DiscordEventListener(@Lazy JDA jda) {
		this.jda = jda;
	}

	public void startCheckin(String userId) {

		userAnswers.put(userId, new ArrayList<>());

		jda.retrieveUserById(userId).queue(user -> {

			String mention = "<@" + userId + ">";

			user.openPrivateChannel()
					.flatMap(channel -> channel.sendMessage(
							"Good morning, " + mention + "! 🌞\n\n" +
									"Time for your daily check-in for Cohort 33.4 Java!\n" +
									"I'll ask you 3 questions one by one.\n\n" +
									"Let's start with the first question:\n" +
									"Question 1: What did you complete yesterday?"
					))
					.queue();
		});
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getAuthor().isBot()) return;
		if (!event.isFromType(ChannelType.PRIVATE)) return;

		String userId = event.getAuthor().getId();
		String message = event.getMessage().getContentRaw();

		if (!userAnswers.containsKey(userId)) return;

		List<String> answers = userAnswers.get(userId);
		answers.add(message);

		if (answers.size() == 1) {
			event.getChannel().sendMessage("Question 2: What are you planning today?").queue();
		}
		else if (answers.size() == 2) {
			event.getChannel().sendMessage("Question 3: Any blockers?").queue();
		}
		else if (answers.size() == 3) {

			event.getChannel().sendMessage("Check-in completed! Thank you").queue();

			sendSummary(userId, answers);
			userAnswers.remove(userId);
		}
	}

	private void sendSummary(String userId, List<String> answers) {

		String mention = "<@" + userId + ">";

		String summary = """
			**Daily Check-In Report**
			
			👤 %s
			
			━━━━━━━━━━━━━━━━━━
			
			**1️⃣ Yesterday**
			%s
			
			**2️⃣ Today’s Plan**
			%s
			
			**3️⃣ Blockers**
			%s
			
			━━━━━━━━━━━━━━━━━━
			""".formatted(
				mention,
				answers.get(0),
				answers.get(1),
				answers.get(2)
		);

		jda.getTextChannelById(reportChannelId)
				.sendMessage(summary)
				.queue();
	}
}