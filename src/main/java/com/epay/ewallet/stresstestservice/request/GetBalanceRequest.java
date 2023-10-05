package com.epay.ewallet.stresstestservice.request;

import lombok.Data;

@Data
public class GetBalanceRequest {
    private String userId;
    private String lmspcode;
    private String modulecode;
    private String trxid;
    private String sign;
}
