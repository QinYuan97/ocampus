package com.ocampus.dao;


import com.ocampus.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {

    String TABLE_NAME = " message ";

    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id";

    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId}, #{toId},#{content},#{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from", TABLE_NAME, " where from_id=#{userId}" +
            " or to_id=#{userId} order by id desc) tt group by conversation_id order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read = 0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, " set has_read=#{status} where id=#{id} "})
    void updateRead(@Param("id") int id, @Param("status") int status);
}
