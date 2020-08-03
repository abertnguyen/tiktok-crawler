package com.thai.tiktokcrawler.tiktok.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "setting")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @Id
    @Column(name = "key")
    private String key;
    @Column(name = "value")
    private String value;

    private long getValueAsLong() {
        return Long.valueOf(value);
    }
}
