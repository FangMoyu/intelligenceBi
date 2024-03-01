package com.yupi.springbootinit.controller;

import cn.hutool.core.util.ObjectUtil;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.LoveStory;
import com.yupi.springbootinit.model.vo.LoveStoryVO;
import com.yupi.springbootinit.service.LoveStoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/love/story")
public class LoveStoryController {

    @Resource
    private LoveStoryService loveStoryService;

    @PostMapping("/random/gen")
    @Transactional(rollbackFor = Exception.class)
    public LoveStoryVO getRandomLoveStory() {
        long count = loveStoryService.count();
        if(count <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据");
        }
        // 随机生成一个查询索引
        int randomIndex = (int) (Math.random() * count);
        // 查询数据
        LoveStory loveStory = loveStoryService.getById(randomIndex);
        // 若果查询不到，则循环调用本方法，直到查询到数据,最多循环 200 次
        int countInvoke = 0;
        while(ObjectUtil.isNull(loveStory) && countInvoke <= 200) {
            randomIndex = (int) (Math.random() * count);
            loveStory = loveStoryService.getById(randomIndex);
            countInvoke++;
        }
        // 两百次循环仍然没有数据就直接返回
        if(ObjectUtil.isNull(loveStory)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"没有数据");
        }
        // 查询后需要经过脱敏再返回结果
        LoveStory safetyLoveStory = getSafetyLoveStory(loveStory);
        LoveStoryVO loveStoryVO = new LoveStoryVO();
        BeanUtils.copyProperties(safetyLoveStory, loveStoryVO);
        return loveStoryVO;
    }

    /**
     * 数据脱敏
     * @param loveStory
     * @return
     */
    public LoveStory getSafetyLoveStory(LoveStory loveStory) {
        LoveStory newLoveStory = new LoveStory();
        newLoveStory.setId(loveStory.getId());
        newLoveStory.setContent(loveStory.getContent());
        return newLoveStory;
    }
}
