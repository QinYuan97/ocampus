package com.ocampus.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static  String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";
    private static String BIZ_FOLLOW = "FOLLOW";


    public static  String getLikeKey(int entityId, int entityType){
        return BIZ_LIKE + SPLIT + String.valueOf(entityId) + SPLIT + String.valueOf(entityType);
    }

    public static  String getDisLikeKey(int entityId, int entityType){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityId) + SPLIT + String.valueOf(entityType);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENT;
    }

    public static String getFollowKey(int userId){
        return BIZ_FOLLOW + SPLIT + String.valueOf(userId);
    }

}
