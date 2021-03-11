package com.yinrj.emos.wx.init;

import cn.hutool.core.util.StrUtil;
import com.yinrj.emos.wx.constant.SystemContant;
import com.yinrj.emos.wx.db.dao.SysConfigDao;
import com.yinrj.emos.wx.db.entity.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

/**
* @description 
* @author yinrongjie
* @date 2021/3/11
* @version 1.0
*/
@Component
@Slf4j
public class InitConfig {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private SystemContant systemContant;

    @PostConstruct
    public void init() {
        log.info("init()");
        List<SysConfig> list = sysConfigDao.selectAllConfig();
        list.forEach(one -> {
            String key = one.getParamKey();
            // 转成驼峰
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = systemContant.getClass().getDeclaredField(key);
                field.set(systemContant, value);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        });
    }
}
