package com.example.domain.myEnum;

/**
 * 支付状态
 */
public enum PayStatus {
    /**
     * 等待支付中
     */
    WAITING(0, "等待支付中"),
    /**
     * 已取消
     */
    CANCEL(1, "已取消"),
    /**
     * 已支付
     */
    PAYED(2, "已支付"),
    /**
     * 已超时回滚（未支付，并且商品已恢复）
     */
    TIMEOUT(3, "已超时回滚"),
    /**
     * 不支持的模式（仅用在测试中）
     */
    NOT_SUPPORT(4, "不支持的模式");

    private final int code;
    private final String desc;

    PayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PayStatus parse(String code) {
        PayStatus result = null;
        int iCode = Integer.valueOf(code).intValue();
        for (PayStatus payStatus : PayStatus.values()) {
            if (iCode == payStatus.getCode()) {
                result = payStatus;
                break;
            }
        }
        if (result == null) {
            throw new RuntimeException("支付状态参数异常");
        }
        return result;
    }
}
