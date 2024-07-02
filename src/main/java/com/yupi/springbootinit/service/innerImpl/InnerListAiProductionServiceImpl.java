//package com.yupi.springbootinit.service.innerImpl;
//
//
//import cn.hutool.json.JSONUtil;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.fang.fangapicommon.model.dto.ChartQueryRequest;
//import com.fang.fangapicommon.model.entity.Chart;
//import com.fang.fangapicommon.model.entity.User;
//import com.fang.fangapicommon.model.vo.ChartVO;
//import com.fang.fangapicommon.service.InnerListAiProductionService;
//import com.google.common.reflect.TypeToken;
//import com.google.gson.Gson;
//import com.yupi.springbootinit.common.ErrorCode;
//import com.yupi.springbootinit.exception.BusinessException;
//import com.yupi.springbootinit.exception.ThrowUtils;
//import com.yupi.springbootinit.service.ChartService;
//import com.yupi.springbootinit.service.UserService;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * 展示我生成的图表，直接用内部方法返回
// */
//@DubboService
//public class InnerListAiProductionServiceImpl implements InnerListAiProductionService {
//    @Resource
//    private UserService userService;
//
//    @Resource
//    private ChartService chartService;
//
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//    /**
//     *
//     * @param userId
//     * @param chartQueryRequest
//     * @return
//     */
//    @Override
//    public Page<ChartVO> listMyChartByPage(Long userId, ChartQueryRequest chartQueryRequest) {
//            if (chartQueryRequest == null) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR);
//            }
//        // 之前调用时，就会经过
//        User loginUser = userService.getById(userId);
//
//        chartQueryRequest.setUserId(loginUser.getId());
//        // 加缓存
//        String chartJson = stringRedisTemplate.opsForValue().get("MyChart:"+ loginUser.getId()+ ":" + JSONUtil.toJsonStr(chartQueryRequest));
//        Gson gson = new Gson();
//        //
//        if(!StringUtils.isBlank(chartJson)) {
//            Page<ChartVO> chartPage = gson.fromJson(chartJson, new TypeToken<Page<ChartVO>>() {
//            }.getType());
//            return chartPage;
//        }
//
//        long current = chartQueryRequest.getCurrent();
//            long size = chartQueryRequest.getPageSize();
//            // 限制爬虫
//            ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//            Page<Chart> chartPage = chartService.page(new Page<>(current, size),
//                    chartService.getQueryWrapper(chartQueryRequest));
//            // 如果没查到，就直接抛出异常
//        List<Chart> records = chartPage.getRecords();
//        // 获取 VO 对象
//        List<ChartVO> chartVOList = records.stream()
//                .map(chart -> {
//                    ChartVO chartVO = new ChartVO();
//                    BeanUtils.copyProperties(chart, chartVO);
//                    return chartVO;
//                })
//                .collect(Collectors.toList());
//        ThrowUtils.throwIf(chartPage == null, ErrorCode.NOT_FOUND_ERROR);
//        // 将 VO 对象保存到新的 Page 中
//        Page<ChartVO> chartVOPage = new Page<>();
//        // 通过 bean 转换
//        BeanUtils.copyProperties(chartPage, chartVOPage);
//        // 其他都一致，就修改一下记录即可
//        chartVOPage.setRecords(chartVOList);
//        ThrowUtils.throwIf(chartVOPage == null, ErrorCode.NOT_FOUND_ERROR);
//            stringRedisTemplate.opsForValue().set("MyChart:"+ loginUser.getId()+ ":" + JSONUtil.toJsonStr(chartQueryRequest), JSONUtil.toJsonStr(chartVOPage), 1, TimeUnit.DAYS);
//            return chartVOPage;
//    }
//}
