package com.yupi.springbootinit.controller;
import java.util.Date;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.bizmq.SaveDataMessageProducer;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Copywriting;
import com.yupi.springbootinit.model.enums.AiModelIdEnum;
import com.yupi.springbootinit.model.vo.CopyWritingResponse;
import com.yupi.springbootinit.service.CopywritingService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/copyWriting")
public class CopyWritingController {
    @Resource
    private AiManager aiManager;

    @Resource
    private CopywritingService copywritingService;

    @Resource
    private SaveDataMessageProducer saveDataMessageProducer;
    /**
     * ai 自动生成文案
     * @param userInput
     * @return
     */
    @PostMapping("/gen")
    public CopyWritingResponse genCopyWritingContext(String userInput, Long userId) {
        if(StrUtil.isBlank(userInput)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        }
        if(userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        String result = aiManager.doChat(AiModelIdEnum.COPY_WRITING_ID.getModelId(), userInput);
        if(StrUtil.isBlank(result)) {

            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "ai 生成错误");
        }
        // 保存到数据库
        Copywriting copywriting = new Copywriting();
        copywriting.setUserId(userId);
        copywriting.setGenCopyWriting(result);
        copywriting.setUserInput(userInput);
        boolean save = copywritingService.save(copywriting);
        copywriting.setGenCopyWriting(result);
        // 数据库保存失败了，但是不影响结果，做个日志记录后返回，可以通过保存到消息队列，然后让消息队列放回
        if(!save) {
            // 将 CopyWriting 转成 Json 后发送到消息
            String copyWritingJson = JSONUtil.toJsonStr(copywriting);
            if(StrUtil.isBlank(copyWritingJson)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成 JSon 失败");
            }
            // 让消息队列发送消息
            saveDataMessageProducer.SendUserInputSaveAgain(copyWritingJson);
            log.info("数据库保存异常，未能正确保存数据");
        }
        CopyWritingResponse copyWritingResponse = new CopyWritingResponse();
        copyWritingResponse.setCopyWritingId(copywriting.getId());
        copyWritingResponse.setGenCopyWriting(result);
        return copyWritingResponse;
    }
}
