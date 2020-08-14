/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thai.tiktokcrawler.tiktok.repository;

import com.thai.tiktokcrawler.tiktok.entity.TransactionHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Admin
 */
@Repository
public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, String> {
    List<TransactionHistory> findAllByTypeAndCreatedTimeTxBetween(String type, Date time1, Date time2);
    TransactionHistory findFirstByOrderByCreatedAtDesc();
}
