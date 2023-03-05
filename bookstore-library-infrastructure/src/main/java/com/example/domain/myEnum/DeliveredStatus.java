package com.example.domain.myEnum;

/**
 * 待交付商品的状态
 **/
public enum DeliveredStatus {

    /**
     * 出库调减库存
     */
    DECREASE,

    /**
     * 入库调增库存
     */
    INCREASE,

    /**
     * 待出库冻结库存
     */
    FROZEN,

    /**
     * 取消出库解冻库存
     */
    THAWED;

}
