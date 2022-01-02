package com.yulin.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yulin.manager.AccountManage;
import com.yulin.mapper.AccountMapper;
import com.yulin.model.AccountDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public List<AccountDO> findByPhone(String phone) {
        List<AccountDO> accountDOList = accountMapper.
                selectList(new QueryWrapper<AccountDO>().eq("phone", phone));
        return accountDOList;
    }
}
