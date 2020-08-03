/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thai.tiktokcrawler.tiktok.repository;

import com.thai.tiktokcrawler.tiktok.entity.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Admin
 */
@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {
    Setting findFirstByKey(String key);
}
