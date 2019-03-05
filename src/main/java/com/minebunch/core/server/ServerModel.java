package com.minebunch.core.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minebunch.core.event.server.RedisServerSaveEvent;
import com.minebunch.core.player.rank.Rank;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.UUID;

public @Getter @ToString class ServerModel implements Runnable {
    private final String id;

    @Setter private String displayName;
    @Setter private int onlinePlayers, slots, overflow;
    @Setter private boolean online;
    @Setter private long heartbeat;

    private final BasicDBObject playerData = new BasicDBObject();
    private final HashMap<String, String> extraFields = Maps.newHashMap();

    @Setter private Rank lockRank = Rank.MEMBER;
    @Setter private String lockMessage = "";

    private long uptime;

    /**
     * Create a server information instance
     * <p>If eventManager is not null the internal event
     * {@link RedisServerSaveEvent} will be called to obtain
     * additional data and, if available, player information.
     *
     * @param id server id for redis
     */
    public ServerModel(String id) {
        this.id = id;
    }

    public void run() {
        // TODO Call event with Server.this
        // TODO Call saveServer with the jedis instance
        Bukkit.getServer().getPluginManager().callEvent(new RedisServerSaveEvent(this));
//        saveServer(jedis, false);
    }

//    Disabled until Redis is added to core
//
//    public void saveServer(Jedis jedis, boolean stopping) {
//        String str = "servers:" + id;
//
//        if(jedis.hexists(str, "displayName")) {
//            jedis.hset(str, "displayName", displayName == null || displayName.isEmpty() ? "" : displayName);
//
//            jedis.hset(str, "slots", String.valueOf(slots));
//            jedis.hset(str, "overflow", String.valueOf(overflow));
//            jedis.hset(str, "onlinePlayers", String.valueOf(stopping ? 0 : onlinePlayers));
//
//            if(!stopping) {
//                heartbeat = System.currentTimeMillis() / 1000;
//            }
//            jedis.hset(str, "heartbeat", String.valueOf(heartbeat));
//
//            jedis.hset(str, "online", String.valueOf(!stopping));
//            jedis.hset(str, "uptime", ManagementFactory.getRuntimeMXBean().getStartTime() + "");
//            // TODO Add memory usage and TPS.
//            Runtime runtime = Runtime.getRuntime();
//            jedis.hset(str, "memory", Math.round((runtime.totalMemory() - runtime.freeMemory()) / 1024l / 1024l) + "," + Math.round(runtime.totalMemory() / 1024 / 1024));
//
//            if(isLocked()) {
//                jedis.hset(str, "lock", lockRank.getName() + ";" + lockMessage);
//            } else {
//                if(jedis.hexists(str, "lock")) {
//                    jedis.hdel(str, "lock");
//                }
//            }
//
//            if(stopping) {
//                playerData.clear();
//                playerData.put("names", Lists.newArrayList());
//                playerData.put("vanished", Lists.newArrayList());
//                playerData.put("uuids", Lists.newArrayList());
//                playerData.put("details", new BasicDBList());
//            }
//
//            jedis.hset(str, "playerData", playerData.toString());
//
//            if(!extraFields.isEmpty()) {
//                jedis.hmset(str, extraFields);
//            }
//        }
//    }
//
//    public void importFrom(Jedis jedis) {
//        String str = "servers:id";
//
//        if(jedis.hexists(str, "displayName")) {
//            displayName = jedis.hget(str, "displayName");
//            slots = Integer.parseInt(jedis.hget(str, "slots"));
//            overflow = Integer.parseInt(jedis.hget(str, "overflow"));
//            onlinePlayers = Integer.parseInt(jedis.hget(str, "onlinePlayers"));
//            heartbeat = Long.parseLong(jedis.hget(str, "heartbeat"));
//            online = Boolean.parseBoolean(jedis.hget(str, "online"));
//            uptime = Long.parseLong(jedis.hget(str, "uptime"));
//
//            String lock = jedis.hget(str, "lock");
//            if(lock != null && !lock.isEmpty()) {
//                lockRank = Rank.getByName(lock.split(";")[0]);
//                lockMessage = lock.split(";")[1];
//            } else {
//                lockRank = Rank.MEMBER;
//                lockMessage = "";
//            }
//        }
//    }

    /**
     * Check if this server is 'full', does not
     * consider the overflow capacity of the server
     *
     * @return if the online players
     */
    public boolean isFull() {
        return this.onlinePlayers >= this.slots;
    }

    /**
     * If this server should be considered 'dead'
     *
     * @return if the server failed to send heartbeats within 5 seconds
     */
    public boolean isDead() {
        return (System.currentTimeMillis() / 1000) - heartbeat > 5;
    }

    /**
     * Check if the given rank can join this server in it's current state
     *
     * <p>List of checks performed:
     * <ul>
     *     <li>Is server locked?</li>
     *     <li>Is server full?</li>
     *     <li>Is helper, mod or admin?</li>
     *     <li>Is full and at least premium?</li>
     *     <li>Has server reached overflow capacity?</li>
     * </ul>
     *
     * @param rank rank to check
     * @return if the given rank can join the server
     */
    public boolean canJoin(Rank rank) {
        if(getLockRank() != null && getLockRank().ordinal() > rank.ordinal()) {
            return false;
        }

        // If server is full and they aren't staff
        if(isFull() && Rank.LOWEST_STAFF.ordinal() > rank.ordinal()) {
            if(Rank.LOWEST_DONOR.ordinal() > rank.ordinal()) { // If they are not donator - deny
                return false;
            }

            if(getOverflow() <= 0) { // If there is no overflow - deny
                return false;
            }

            if((getOnlinePlayers() + 1) > getOverflow()) { // If the overflow is full - deny
                return false;
            }
        }

        // Default to allow if staff
        return true;
    }

    /**
     * Check if this server is locked
     *
     * @return if it locked
     */
    public boolean isLocked() {
        return this.lockRank != null && this.lockRank != Rank.MEMBER;
    }

}
