package idv.sd.bot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.*;
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
    private final Logger logger = LoggerFactory.getLogger(Twitchbot.class);
    private final String OAUTH = "hwf7l22bvrpxshg1242xrzm9y67sqg";
    private final String ChannelName = "tetristhegrandmaster3";
    private OAuth2Credential Credential = new OAuth2Credential("twitch", OAUTH);
    private Discordbot Discordbot = new Discordbot();
    private DateTimeFormatter Formatter;
    private TwitchClient Client;
    private String ChannelId;
    private Integer LastLevel;
    private Integer LastProgress;
    private Integer LastGoal;
    private String Percent;
    private boolean dondon_on = false;
    private boolean Training = false;
//    private String HypeTrainID;


    public static void main(String[] args) {
        Twitchbot a = new Twitchbot();
        a.start();
    }

    private void start() {
        init();
        getChannelId(ChannelName);
        HypeTrainlistener();
        Chatlistener();
        logger.info("Twitchbot Ready");
    }

    private void init() {
        Client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(Credential)
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

    private void Chatlistener() {
        Client.getChat().joinChannel(ChannelName);
        Client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            logger.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage());
        });
        Client.getEventManager().onEvent(CheerEvent.class, event -> {
            logger.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage() + "Cheer ：" + event.getBits());
            if (Training && event.getUser().getName().equalsIgnoreCase("donjen1330")) {
                dondon_on = true;
            }
        });
        Client.getEventManager().onEvent(DonationEvent.class, event -> {
            logger.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage() + "Donation ：" + event.getAmount() + "(" + event.getSource() + ")");
            if (Training && event.getUser().getName().equalsIgnoreCase("donjen1330")) {
                dondon_on = true;
            }
        });
        Client.getEventManager().onEvent(ExtendSubscriptionEvent.class, event -> {
            logger.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + "ExtendSubscription ：" + event.getCumulativeMonths());
            if (Training && event.getUser().getName().equalsIgnoreCase("donjen1330")) {
                dondon_on = true;
            }
        });
        Client.getEventManager().onEvent(GiftSubscriptionsEvent.class, event -> {
            logger.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + "GiftSubscriptionsEvent ：" + event.getSubscriptionPlan());
            if (Training && event.getUser().getName().equalsIgnoreCase("donjen1330")) {
                dondon_on = true;
            }
        });

    }

    public void HypeTrainlistener() {
        Client.getPubSub().listenForHypeTrainEvents(Credential, ChannelId);

        Client.getEventManager().onEvent(HypeTrainApproachingEvent.class, (Event) -> {
            logger.info(Event.toString());
        });

        Client.getEventManager().onEvent(HypeTrainStartEvent.class, (Event) -> {
//            HypeTrainID = Event.getData().getId();
//            System.out.println(Event.getData().getId());
            logger.info(Event.getData().getId());
            Discordbot.sendMsg(
                    "==== 列車發車記錄(v0.4) ====\n" +
                            "開車時間: " + Event.getData().getStartedAt().atZone(ZoneId.of("Asia/Taipei")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                            "預計離站: " + Event.getData().getExpiresAt().atZone(ZoneId.of("Asia/Taipei")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                            "=======================");
            Training = true;
            dondon_on = false;
        });

        Client.getEventManager().onEvent(HypeTrainProgressionEvent.class, (Event) -> {
            logger.info(Event.toString());
            LastLevel = Event.getData().getProgress().getLevel().getValue();
            LastProgress = Event.getData().getProgress().getValue();
            LastGoal = Event.getData().getProgress().getGoal();
            Percent = (new DecimalFormat("#.##").format(((float) LastProgress / LastGoal) * 100));

        });
        Client.getEventManager().onEvent(HypeTrainLevelUpEvent.class, (Event) -> {
            logger.info(Event.toString());
            LastLevel = Event.getData().getProgress().getLevel().getValue();
            LastProgress = Event.getData().getProgress().getValue();
            LastGoal = Event.getData().getProgress().getGoal();
        });
        Client.getEventManager().onEvent(HypeTrainEndEvent.class, (Event) -> {
            logger.info(Event.toString());
            Instant EndTime = Event.getData().getEndedAt();
            if (dondon_on) {
                Discordbot.sendMsg(
                        "==== 列車發車記錄(v0.4) ====\n" +
                                "列車離站時間: " + Formatter.format(EndTime) + "\n" +
                                "貼圖等級: " + LastLevel + "-" + Percent + "%\n" +
                                "東~~~~: " + "這次居然上到車了\n" +
                                "=======================");
            } else {
                Discordbot.sendMsg(
                        "==== 列車發車記錄(v0.4) ====\n" +
                                "列車離站時間: " + Formatter.format(EndTime) + "\n" +
                                "貼圖等級: " + LastLevel + "-" + Percent + "%\n" +
                                "東~~~~: " + "這次沒上車\n" +
                                "=======================");
            }
            dondon_on = false;
            Training = false;
        });
        Client.getEventManager().onEvent(HypeTrainConductorUpdateEvent.class, (Event) -> {
            logger.info(Event.toString());
//            Discordbot.sendMsg(Event.getData().toString());
        });
        Client.getEventManager().onEvent(HypeTrainCooldownExpirationEvent.class, (Event) -> {
            logger.info(Event.toString());
            //            Discordbot.sendMsg(Event.getFiredAt().toString());
        });
    }

    public void Bitslistener() {
        Client.getPubSub().listenForCheerEvents(Credential, ChannelId);
        Client.getEventManager().onEvent(ChannelBitsEvent.class, (Event) -> {
            logger.info(Event.toString());
        });
    }
}
