package com.cyhd.service.impl;

import com.cyhd.service.dao.db.mapper.ArticleComercialMapper;
import com.cyhd.service.dao.db.mapper.ArticleWeixinAccountMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.ArticleComercial;
import com.cyhd.service.dao.po.ArticleWeixinAccount;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by hy on 9/29/15.
 */
@Service
public class WeixinArticleService {

    private static final int CACHE_TTL = 300 * 1000; //5分钟
    @Resource
    ArticleComercialMapper comercialMapper;

    @Resource
    ArticleWeixinAccountMapper weixinAccountMapper;

    
    CacheLRULiveAccessDaoImpl<ArticleComercial> commercialCache = new CacheLRULiveAccessDaoImpl<>(CACHE_TTL, 50);
    CacheLRULiveAccessDaoImpl<ArticleWeixinAccount> weixinAccountCache = new CacheLRULiveAccessDaoImpl<>(CACHE_TTL, 50);


    /**
     * 根据id获取文章的广告
     * @param id
     * @return
     */
    public ArticleComercial getCommercial(int id){
        ArticleComercial comercial = commercialCache.get("" + id);

        if (comercial == null){
            comercial = comercialMapper.getComercial(id);

            if (comercial != null){
                commercialCache.set("" + id, comercial);
            }
        }

        return comercial;
    }

    /**
     * 根据id获取文章的微信账户信息
     * @param id
     * @return
     */
    public ArticleWeixinAccount getWeixinAccount(int id){
        ArticleWeixinAccount weixinAccount = weixinAccountCache.get("" + id);

        if (weixinAccount == null){
            weixinAccount = weixinAccountMapper.getWeixinAccount(id);

            if (weixinAccount != null){
                weixinAccountCache.set("" + id, weixinAccount);
            }
        }

        return weixinAccount;
    }
}
