package com.yulin.manage.impl;

import com.yulin.manage.AccountManage;
import com.yulin.mapper.AccountMapper;
import com.yulin.model.AccountDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */
@Component
@Slf4j
public class AccountManageImpl implements AccountManage {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public int insert(AccountDO accountDO) {
        return accountMapper.insert(accountDO);
    }
}
