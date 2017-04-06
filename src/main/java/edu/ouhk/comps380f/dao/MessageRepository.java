package edu.ouhk.comps380f.dao;

import edu.ouhk.comps380f.model.Message;
import edu.ouhk.comps380f.model.MessageReply;
import edu.ouhk.comps380f.model.MessageUser;
import java.util.List;

public interface MessageRepository {
    public void create(Message entry, MessageUser user);
    public List<Message> findAllTopic(String category);
    public Message findById(int id);
    public void update(Message entry);
    
    //Reply
    public List<MessageReply> findAllReply(int id);
    public void reply(int id, MessageReply entry, MessageUser user);
}
