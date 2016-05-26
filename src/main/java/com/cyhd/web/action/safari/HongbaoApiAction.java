package com.cyhd.web.action.safari;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.Pair;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDraw;
import com.cyhd.service.impl.HongbaoActivityService;
import com.cyhd.service.impl.UserDrawService;
import com.cyhd.service.impl.UserDrawService.DrawNum;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.vo.UserDrawLogVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;

/**
 * Created by hy on 1/29/16.
 */
@Controller
@RequestMapping("/ios/api/hongbao")
public class HongbaoApiAction extends BaseAction{

    @Resource
    HongbaoActivityService hongbaoService;

    String prefix = "/safari/api/";

    @Resource
    private UserDrawService userDrawService;
    
    /**
     * 获取系统和用户的红包数量
     * @param request
     * @return
     */
    @RequestMapping(value = "/number", method = RequestMethod.GET, produces="text/json; charset=UTF-8")
    @ResponseBody
    public String getSystemHongbaoNum(HttpServletRequest request) throws Exception{
        User user = getUser(request);
        int sysNum = 0;//hongbaoService.getSystemHongbaoNum();
        int userNum = hongbaoService.getUserHongbaoNum(user.getId());

        Map<String, Object> results = new HashMap<>();
        results.put("sysNum", sysNum);
        results.put("userNum", userNum);
       // results.put("next_wave", hongbaoService.getNextHongbaoTime());


        return toJSONResult(results);
    }

    /**
     * 抢系统红包
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/system", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String grabSytemHongbao(HttpServletRequest request) throws Exception{
        User user = getUser(request);
        Pair<Integer, Integer> amountPair = null;//hongbaoService.grabSystemHongbao(user.getId());

        Map<String, Object> results = null;
        if (amountPair != null){
            results = new HashMap<>();
            results.put("amount", amountPair.second);
            results.put("type", amountPair.first);
        }
        return toJSONResult(results);
    }


    /**
     * 抢专属红包
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/mine", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String grabUserHongbao(HttpServletRequest request) throws Exception{
        User user = getUser(request);
        Pair<Integer, Integer> amountPair = hongbaoService.grabUserHongbao(user.getId());

        Map<String, Object> results = null;
        if (amountPair != null){
            results = new HashMap<>();
            results.put("amount", amountPair.second);
            results.put("type", amountPair.first);
        }
        return toJSONResult(results);
    }

    /**
     * 所有用户的红包中奖记录
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", produces="text/json; charset=UTF-8")
    public ModelAndView getLatestHongbao(HttpServletRequest request) throws Exception {
        List<UserDrawLogVo> drawLogVoList = hongbaoService.getLatestHongbaoList();

        ModelAndView mv = new ModelAndView(prefix + "hongbao_list.json.ftl");
        fillStatus(mv);
        mv.addObject("hongbaoList", drawLogVoList);
        return mv;
    }
}
