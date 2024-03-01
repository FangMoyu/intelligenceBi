package com.yupi.springbootinit.bizmq;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.fang.fangapicommon.model.entity.Chart;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.enums.AiModelIdEnum;
import com.yupi.springbootinit.model.enums.ChartStatusEnum;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
// 使用@Slf4j注解生成日志记录器
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    // @SneakThrows 注解简化异常处理，
    // 底层实际上是给需要抛出检查型异常的方法加上了 try-catch，使用这个注解可以让代码更加简洁
    @SneakyThrows
    // rabbitMQ 消息监听注解，可以用来接收消息
    // 设置队列名称和消息确认模式为手动确认
    @RabbitListener(queues = {"bi_queue"}, ackMode = "MANUAL")
    // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。
    // 通过使用@Header(AmqpHeaders.DELIVERY_TAG)注解,
    // 可以从消息头中提取出该投递标签,并将其赋值给long deliveryTag参数。
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StrUtil.isBlank(message) || !StrUtil.isNumeric(message)) {
            // 如果消息为空或者不是数字，就直接拒绝消息，同时不重试消息
            channel.basicNack(deliveryTag, false, false);
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            // 如果图表为空，拒绝消息并抛出业务异常
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        // 设置运行中的状态
        updateChart.setStatus(ChartStatusEnum.RUNNING.getStatus());
        updateChart.setExecMessage(ChartStatusEnum.RUNNING.getExecMessage());
        // 判断状态是否保存成功
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            handleChartUpdateError(updateChart.getId(), "更新图表执行中状态失败");
            return;
        }
        // 调用 Ai 接口,得到返回结果
        String result = aiManager.doChat(AiModelIdEnum.BI_MODEL_ID.getModelId(), buildUserInput(chart));
        // 将返回结果进行分割赋值
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            handleChartUpdateError(updateChart.getId(), "AI 生成错误");
            return;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        updateChart.setGenChart(genChart);
        updateChart.setGenResult(genResult);
        // 更新图表生成成功的状态
        updateChart.setStatus(ChartStatusEnum.SUCCEED.getStatus());
        updateChart.setExecMessage(ChartStatusEnum.SUCCEED.getExecMessage());
        boolean updateResult = chartService.updateById(updateChart);
        if (!updateResult) {
            handleChartUpdateError(updateChart.getId(), "更新图表成功状态失败");
            return;
        }
        channel.basicAck(deliveryTag, false);
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus(ChartStatusEnum.FAIL.getStatus());
        chart.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(chart);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

    /**
     * 生成 userInput 用户输入
     * @param chart 图表，用来获取用户生成图表的一些数据
     * @return 返回 userInput
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        // 设计 message，将用户发送的信息向 ai 提问
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求:").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据:").append("\n");
        ThrowUtils.throwIf(StringUtils.isBlank(csvData), ErrorCode.SYSTEM_ERROR, "数据压缩失败");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}