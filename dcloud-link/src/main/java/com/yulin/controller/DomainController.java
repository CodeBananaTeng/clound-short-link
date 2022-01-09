package com.yulin.controller;

import com.yulin.service.DomainService;
import com.yulin.utils.JsonData;
import com.yulin.vo.DomainVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther:LinuxTYL
 * @Date:2022/1/2
 * @Description:
 */
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {

    @Autowired
    private DomainService domainService;

    /**
     * 列举全部可用域名列表
     * @return
     */
    @GetMapping("list")
    public JsonData listAll(){
        List<DomainVO> list = domainService.listAll();
        return JsonData.buildSuccess(list);
    }

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @GetMapping("test")
    public JsonData test(@RequestParam(name = "code")String code,@RequestParam(name = "account_no")String accountNo){
        //key1是短链码，ARGV[1]是accountNo,ARGV[2]是过期时间
        String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
        " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
        " else return 0; end;";
        Long result = redisTemplate.execute(new
                DefaultRedisScript<>(script, Long.class), Arrays.asList(code), accountNo,100);
        return JsonData.buildSuccess(result);
    }

}
