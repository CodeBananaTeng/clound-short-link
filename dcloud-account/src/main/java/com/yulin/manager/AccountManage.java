package com.yulin.manager;

import com.yulin.model.AccountDO;

import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/15
 * @Description:
 */
public interface AccountManage {

    int insert(AccountDO accountDO);

    List<AccountDO> findByPhone(String phone);
}
