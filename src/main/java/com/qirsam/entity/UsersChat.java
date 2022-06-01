package com.qirsam.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_chat")
public class UsersChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;

    private Instant createdAt;

    private String createdBy;

    public void setUser(User user) {
        this.user = user;
        this.user.getUsersChats().add(this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chat.getUsersChats().add(this);
    }
}
