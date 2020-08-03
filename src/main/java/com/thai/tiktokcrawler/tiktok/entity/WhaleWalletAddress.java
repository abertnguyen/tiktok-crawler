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
@Table(name = "whale_wallet_address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WhaleWalletAddress {
    @Column(name = "address")
    @Id
    private String address;
    @Column(name = "name")
    private String name;
}
