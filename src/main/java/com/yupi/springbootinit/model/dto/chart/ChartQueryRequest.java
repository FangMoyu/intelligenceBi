//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yupi.springbootinit.model.dto.chart;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private String name;
    private String goal;
    private String chartType;
    private Long userId;
    private static final long serialVersionUID = 1L;


}
