package com.qirsam.listener;

import com.qirsam.entity.Chat;
import com.qirsam.entity.UserChat;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

public class UserChatListener {

    @PostPersist
    public void postPersist(UserChat userChat){
        var chat = userChat.getChat();
        chat.setCount(chat.getCount() + 1);
    }

    @PostRemove
    public void postRemove(UserChat userChat){
        var chat = userChat.getChat();
        chat.setCount(chat.getCount() - 1);
    }
}
