package com.ocampus.service;

import com.ocampus.util.JedisAdapter;
import com.ocampus.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int followedId){
        String followKey = RedisKeyUtil.getFollowKey(userId);
        if(jedisAdapter.sadd(followKey, String.valueOf(followedId)) > 0){
            return true;
        }
        return false;
    }

    public Set<Integer> getFollow(int userId){
        String followkey = RedisKeyUtil.getFollowKey(userId);
        Set<String> res = jedisAdapter.smembers(String.valueOf(followkey));
        Set<Integer> set = new HashSet<>();
        for(String s : res){
            set.add(Integer.valueOf(s));
        }
        return set;
    }

}
