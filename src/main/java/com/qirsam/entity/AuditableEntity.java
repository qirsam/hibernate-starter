package com.qirsam.entity;

import com.qirsam.listener.AuditListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditListener.class)
public abstract class AuditableEntity<T extends Serializable> implements BaseEntity<T>{

    private Instant createdAt;
    private String createdBy;

    private Instant updateAt;
    private String updateBy;

}
