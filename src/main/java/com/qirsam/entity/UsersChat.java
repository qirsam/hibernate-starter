package com.qirsam.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_chat")
@EqualsAndHashCode(callSuper=false)
public class UsersChat extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;


    public void setUser(User user) {
        this.user = user;
        this.user.getUsersChats().add(this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chat.getUsersChats().add(this);
    }
}
