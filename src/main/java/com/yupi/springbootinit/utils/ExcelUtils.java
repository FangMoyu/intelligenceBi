package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yupi.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 读取Excel文件工具类
 */

@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
        // 同步读，不用写监听器
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误",e);
        }

        if(CollectionUtil.isEmpty(list)){
            return "";
        }
        // 读取表头(第一行)
        LinkedHashMap<Integer, String> headerMap =(LinkedHashMap<Integer, String>) list.get(0);
        // 过滤出其中不为空的内容
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.join(headerList,",")).append("\n");
        // 将真实数据过滤空值后输出。
        for(int i = 1; i < list.size(); i++){
            Map<Integer, String> dataMap = list.get(i);
            List<String> dataList = dataMap.values()
                    .stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }

        return stringBuilder.toString();
    }


}