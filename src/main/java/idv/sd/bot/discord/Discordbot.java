package idv.sd.bot.discord;


import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;


import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.ChannelData;
import discord4j.discordjson.possible.Possible;
import idv.sd.api.google.safe.Google_Safe_Browsing;
import idv.sd.api.gov.cwb.Cwd_Earthquake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discordbot {
    private final Logger logger = LoggerFactory.getLogger(Discordbot.class);
    private final String TOKEN = System.getenv("Discord_TOKEN");
    private final String ChannelId = "812636716640501760";
    private GatewayDiscordClient Client;

    public Discordbot() {
        start();
    }

    public static void main(String[] args) {
        Discordbot a = new Discordbot();
        a.start();
    }


    private void start() {
        try {
            Client = DiscordClientBuilder.create(TOKEN)
                    .build()
                    .login()
                    .block();
            isSafe();
            ReportEarthquake();
            AtTest();
        } catch (Exception e) {
            logger.error("Discordbot start error", e);
        }
    }

    public void sendMsg(String content) {
        Client.getRestClient().getChannelById(Snowflake.of(Id.of(ChannelId))).createMessage(content).subscribe();
    }

    private void isSafe() {
        Client.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if (message.getContent().contains("!safe")) {
                final MessageChannel channel = message.getChannel().block();
                String Url = message.getContent().split(" ")[1];
                String Res = Google_Safe_Browsing.isSafe(Url);
                channel.createMessage(Res).block();
            }
        });
    }

    private void ReportEarthquake() {
        Client.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if (message.getContent().equalsIgnoreCase("!震")) {
                final MessageChannel channel = message.getChannel().block();
                EmbedCreateSpec Embed = Cwd_Earthquake.getEarthquakeReport();
                if (Embed != null) {
                    channel.createMessage(Embed).block();
                } else {
                    channel.createMessage("ㄅ欠，政府的API太廢，沒反應，你等等在試試吧 ㄏ").block();
                }

            }
        });
    }

    private void AtTest() {
        Client.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if (message.getContent().equalsIgnoreCase("!at")) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("@ "+event.getMessage().getUserData().username()).block();
            }
        });
    }
}
