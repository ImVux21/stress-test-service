package com.epay.ewallet.stresstestservice.controller;

import com.epay.ewallet.stresstestservice.authen.JwtTokenUtil;
import com.epay.ewallet.stresstestservice.constant.EcodeConstant;
import com.epay.ewallet.stresstestservice.constant.LogCategory;
import com.epay.ewallet.stresstestservice.model.Ecode;
import com.epay.ewallet.stresstestservice.request.GetBalanceRequest;
import com.epay.ewallet.stresstestservice.response.CommonResponse;
import com.epay.ewallet.stresstestservice.service.StressTestService;
import com.epay.ewallet.stresstestservice.utility.CodeService;
import com.epay.ewallet.stresstestservice.utility.DecodeDataUtil;
import com.epay.ewallet.stresstestservice.utility.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stressTest")
public class StressTestController {
    private static final Logger logger = LogManager.getLogger(StressTestController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private DecodeDataUtil decodeData;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CodeService codeService;

    @Autowired
    private StressTestService stressTestService;

    @PostMapping("/testStress")
    @ResponseBody
    public CommonResponse<Object> testStress(@RequestHeader Map<String, String> header,
                                                       @RequestBody(required = false) JsonNode requestBody,
                                                       @RequestParam(required = false, defaultValue = "true") boolean encrypted) {

        CommonResponse<Object> response = new CommonResponse<>();
        String logCategory = LogCategory.LOG_STRESS_TEST;
        String requestId = header.get("requestid");
        String language = header.get("language");
        String deviceId = Utils.getDeviceIdFromHeader(header);

        try {
            GetBalanceRequest request = decodeData.getRequest(requestId, logCategory, requestBody,
                    GetBalanceRequest.class, encrypted, deviceId);

            String bearerToken = header.get("authorization");
            String token = jwtTokenUtil.getTokenFromBearerToken(bearerToken);
            String onlineUserphone = jwtTokenUtil.getUsernameFromToken(token);
            long onlineUserId = Long.parseLong(jwtTokenUtil.getAllClaimsFromToken(token).get("userId", String.class));

            logger.info("{} | {} | Header={}", logCategory, requestId, gson.toJson(header));
            logger.info("{} | {} | Request={}", logCategory, requestId, gson.toJson(request));
            logger.info("{} | {} | UserInfo: phone={} | userId={}", logCategory, requestId, onlineUserphone, onlineUserId);

            response = stressTestService.testStress(request, requestId, token, header);
            return response;
        } catch (Exception e) {
            logger.fatal("{} | {} | Exception | error={}", logCategory, requestId, e.getMessage(), e);

            response.setEcode(EcodeConstant.EXCEPTION);
            return response;
        } finally {
            if (response.getMessage() == null || response.getMessage().isEmpty()) {
                // Set ecode message, p_ecode, p_message
                Ecode ecode = codeService.getEcode(response.getEcode(), language);
                response.setMessage(ecode.getMessage());
                response.setP_ecode(ecode.getP_ecode());
                response.setP_message(ecode.getP_message());
            }

            if (encrypted == true) {
                logger.info("{} | {} | Create raw response done | rawResponse={}", logCategory, requestId,
                        gson.toJson(response));

                String encryptedData = decodeData.encrypt(requestId, logCategory, deviceId, response.getData());
                response.setData(encryptedData);
            }
        }
    }
}
