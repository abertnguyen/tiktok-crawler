package com.thai.tiktokcrawler.tiktok.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistory extends BaseEntity {
    @Column(name = "eth_amount")
    private Double ethAmount;
    @Column(name = "ampl_amount")
    private Double amplAmount;
    @Column(name = "usd_amount")
    private Double usdAmount;
    @Column(name = "type")
    private String type; //bought, sold
    @Column(name = "from_address")
    private String fromAddress;
    @Column(name = "to_address")
    private String toAddress;
    @Column(name = "tx_id")
    private String txId;
}
