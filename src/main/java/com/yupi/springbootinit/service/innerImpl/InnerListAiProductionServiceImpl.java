package com.yupi.springbootinit.service.innerImpl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fang.fangapicommon.model.dto.ChartQueryRequest;
import com.fang.fangapicommon.model.entity.Chart;
import com.fang.fangapicommon.model.entity.User;
import com.fang.fangapicommon.service.InnerListAiProductionService;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;

/**
 * 展示我生成的图表，直接用内部方法返回
 */
@DubboService
public class InnerListAiProductionServiceImpl implements InnerListAiProductionService {
    @Resource
    private UserService userService;

    @Resource
    private ChartService chartService;

    /**
     *
     * @param userId
     * @param chartQueryRequest
     * @return
     */
    @Override
    public Page<Chart> listMyChartByPage(Long userId, ChartQueryRequest chartQueryRequest) {
            if (chartQueryRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        // 之前调用时，就会经过
        User loginUser = userService.getById(userId);
        chartQueryRequest.setUserId(loginUser.getId());
            long current = chartQueryRequest.getCurrent();
            long size = chartQueryRequest.getPageSize();
            // 限制爬虫
            ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
            Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                    chartService.getQueryWrapper(chartQueryRequest));
            // 如果没查到，就直接抛出异常
            ThrowUtils.throwIf(chartPage == null, ErrorCode.NOT_FOUND_ERROR);
            return chartPage;
    }
}
