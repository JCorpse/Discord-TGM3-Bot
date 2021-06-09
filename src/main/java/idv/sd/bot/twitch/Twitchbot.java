package idv.sd.bot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.pubsub.events.*;
import idv.sd.bot.discord.Discordbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

public class Twitchbot {
    //    private final Logger logger = LoggerFactory.getLogger(Twitchbot.class);
    private final String OAUTH = "0jawn0kj0wndipg34i714w9133vzz9";
    private final String ChannelName = "tetristhegrandmaster3";
    private OAuth2Credential Credential = new OAuth2Credential("twitch", OAUTH);
    private Discordbot Discordbot = new Discordbot();
    private DateTimeFormatter Formatter;
    private TwitchClient Client;
    private String ChannelId;
    private Integer LastLevel;
    private Integer LastProgress;
    private Integer LastGoal;
//    private String HypeTrainID;

    public static void main(String[] args) {
        Twitchbot a = new Twitchbot();
        a.start();
    }

    private void start() {
        init();
        getChannelId(ChannelName);
        HypeTrainlistener();
        Bitslistener();
    }

    private void init() {
        Client = TwitchClientBuilder.builder()
                .withEnablePubSub(true)
                .withEnableHelix(true)
                .withDefaultAuthToken(Credential)
//                .withFeignLogLevel()
                .build();
        Formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .withLocale(Locale.TAIWAN)
                .withZone(ZoneId.of("Asia/Taipei"));
    }

    public void getChannelId(String ChannelName) {
        UserList resultList = Client.getHelix().getUsers(null, null, Arrays.asList(ChannelName)).execute();
        resultList.getUsers().forEach(user -> {
            ChannelId = user.getId();
        });
    }

    public void HypeTrainlistener() {
        Client.getPubSub().listenForHypeTrainEvents(Credential, ChannelId);

        Client.getEventManager().onEvent(HypeTrainStartEvent.class, (Event) -> {
//            HypeTrainID = Event.getData().getId();
            System.out.println(Event.getData().getId());
        });

        Client.getEventManager().onEvent(HypeTrainProgressionEvent.class, (Event) -> {
            System.out.println(Event);
            LastLevel = Event.getData().getProgress().getLevel().getValue();
            LastProgress = Event.getData().getProgress().getValue();
            LastGoal = Event.getData().getProgress().getGoal();
            String Percent = (new DecimalFormat("#.##").format(((float) LastProgress / LastGoal) * 100));
            System.out.println(LastLevel + "-" + Percent + "%");
            System.out.println(LastLevel + "-" + Percent + "%");

        });
        Client.getEventManager().onEvent(HypeTrainLevelUpEvent.class, (Event) -> {
            System.out.println(Event);
            LastLevel = Event.getData().getProgress().getLevel().getValue();
            LastProgress = Event.getData().getProgress().getValue();
            LastGoal = Event.getData().getProgress().getGoal();
        });
        Client.getEventManager().onEvent(HypeTrainEndEvent.class, (Event) -> {
            System.out.println(Event);
            Instant EndTime = Event.getData().getEndedAt();
            String Percent = (new DecimalFormat("#.##").format((float) (LastProgress / LastGoal) * 100));
            Discordbot.sendMsg(
                    "==== 列車發車記錄(v0.3) ====\n" +
                            "列車離站時間: " + Formatter.format(EndTime) + "\n" +
                            "貼圖等級: " + LastLevel + "-" + Percent + "%\n" +
                            "========================");
        });
        Client.getEventManager().onEvent(HypeTrainConductorUpdateEvent.class, (Event) -> {
            System.out.println(Event);
//            Discordbot.sendMsg(Event.getData().toString());
        });
        Client.getEventManager().onEvent(HypeTrainCooldownExpirationEvent.class, (Event) -> {
            System.out.println(Event);
//            Discordbot.sendMsg(Event.getFiredAt().toString());
        });
    }

    public void Bitslistener() {
        Client.getPubSub().listenForPublicCheerEvents(Credential, ChannelId);
        Client.getEventManager().onEvent(CheerbombEvent.class, System.out::println);
    }
}
