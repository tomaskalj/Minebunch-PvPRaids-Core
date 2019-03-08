package com.minebunch.core.player;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.storage.database.MongoRequest;
import com.minebunch.core.utils.time.timer.Timer;
import com.minebunch.core.utils.time.timer.impl.DoubleTimer;
import com.minebunch.core.utils.time.timer.impl.IntegerTimer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.MongoCursor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;

@Setter
@Getter
public class CoreProfile extends PlayerProfile {
    private final List<UUID> ignored = new ArrayList<>();
    private final List<String> knownAddresses = new ArrayList<>();
    private final List<UUID> knownAlts = new ArrayList<>();
    private final String currentAddress;

    private final String name;
    private final UUID id;
    private final Timer commandCooldownTimer = new DoubleTimer(1);
    private final Timer reportCooldownTimer = new IntegerTimer(TimeUnit.SECONDS, 60);

    private Rank rank = Rank.MEMBER;
    private Rank tag;
    private Date firstLogin;
    private Date lastLogin;

    private boolean playingSounds = true;
    private boolean messaging = true;
    private boolean globalChatEnabled = true;
    private boolean inStaffChat;

    private UUID converser;
    private Location lastLocation;
    private boolean vanished;
    private long lastChatTime;
    private Timer chatCooldownTimer;

    // TODO: optimize loading and saving
    public CoreProfile(String name, UUID id, String address) {
        super(id, "players");
        this.name = name;
        this.id = id;
        this.currentAddress = address;
        this.knownAddresses.add(address);

        load();
        findAlts();
    }

    @Override
    public void deserialize(Document document) {
        this.inStaffChat = document.getBoolean("staff_chat_enabled", inStaffChat);
        this.messaging = document.getBoolean("messaging_enabled", messaging);
        this.playingSounds = document.getBoolean("playing_sounds", playingSounds);

        String tagName = document.getString("tag_name");

        Rank tag = Rank.getByName(tagName);

        if (tag != null) {
            this.tag = tag;
        }

        String rankName = document.get("rank_name", rank.name());
        Rank rank = Rank.getByName(rankName);

        if (rank != null) {
            this.rank = rank;
        }

        List<UUID> ignored = document.getList("ignored_ids", UUID.class);

        if (ignored != null) {
            this.ignored.addAll(ignored);
        }

        List<String> knownAddresses = document.getList("known_addresses", String.class);
        if (knownAddresses != null) this.knownAddresses.addAll(knownAddresses);

        List<UUID> knownAlts = document.getList("known_alts", UUID.class);
        if (knownAlts != null) this.knownAlts.addAll(knownAlts);

        this.firstLogin = document.getDate("first_login");
        this.lastLogin = document.getDate("last_login");
    }

    @Override
    public MongoRequest serialize() {
        return MongoRequest.newRequest("players", id)
                .put("uuid", id.toString())
                .put("name", name)
                .put("lowername", name.toLowerCase())
                .put("staff_chat_enabled", inStaffChat)
                .put("messaging_enabled", messaging)
                .put("playing_sounds", playingSounds)
                .put("rank_name", rank.name())
                .put("ignored_ids", ignored)
                .put("known_addresses", knownAddresses)
                .put("known_alts", knownAlts)
                .put("last_address", currentAddress)
                .put("first_login", firstLogin)
                .put("last_login", lastLogin)
                .put("tag_name", tag == null ? "null" : tag.name());
    }

    public Timer getChatCooldownTimer() {
        if (chatCooldownTimer == null) {
            if (hasDonor()) {
                chatCooldownTimer = new DoubleTimer(1);
            } else {
                chatCooldownTimer = new DoubleTimer(3);
            }
        }

        return chatCooldownTimer;
    }

    public String getChatFormat() {
        String rankColor = getVisibleRank().getColor();
        return String.format(getVisibleRank().getRawFormat(), rankColor) + name;
    }

    private void findAlts(){
        if (this.currentAddress != null) {
            try (MongoCursor<Document> cursor = CorePlugin.getInstance().getMongoStorage().getDocumentsByFilter(
                    getCollectionName(), "last_address", currentAddress)) {
                cursor.forEachRemaining(document -> {
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    if (!uuid.equals(id)) knownAlts.add(uuid);
                });
            }
        }
    }

    public Rank getVisibleRank() {
        return tag == null ? rank : tag;
    }

    public void updateLastChatTime() {
        lastChatTime = System.currentTimeMillis();
    }

    public boolean hasRank(Rank requiredRank) {
        return rank.ordinal() <= requiredRank.ordinal();
    }

    public boolean hasStaff() {
        return hasRank(Rank.LOWEST_STAFF);
    }

    public boolean hasDonor() {
        return hasRank(Rank.LOWEST_DONOR);
    }

    public void ignore(UUID id) {
        ignored.add(id);
    }

    public void unignore(UUID id) {
        ignored.remove(id);
    }

    public boolean hasPlayerIgnored(UUID id) {
        return ignored.contains(id);
    }
}
