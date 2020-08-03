package com.thai.tiktokcrawler.tiktok.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name = "created_at")
    private long createdAt;
    @Column(name = "updated_at")
    private long updatedAt;

    @Version
    private long version = 0;

    @PrePersist
    private void prePersist() {
        createdAt = new Date().getTime();
        updatedAt = new Date().getTime();
        this.version = 0;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = new Date().getTime();
    }

}
