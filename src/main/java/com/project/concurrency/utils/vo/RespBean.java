package com.project.concurrency.utils.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*公共返回对象*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object object;

    /**
     * 成功的返回结果 200
     * @return
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 有obj返回  200
     * @param object
     * @return
     */
    public static RespBean success(Object object){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), object);
    }
    /**
     * 失败的返回结果, 不止有500
     */
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }

    /**
     *
     * @param respBeanEnum
     * @param object
     * @return
     */
    public static RespBean error(RespBeanEnum respBeanEnum, Object object){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), object);
    }
}
