package com.epay.ewallet.stresstestservice.service;

import com.epay.ewallet.stresstestservice.constant.EcodeConstant;
import com.epay.ewallet.stresstestservice.request.GetBalanceRequest;
import com.epay.ewallet.stresstestservice.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StressTestService {
    private static final Logger logger = LogManager.getLogger(StressTestService.class);
    private static final Gson gson = new Gson();

    @Value("${URL_GET_BALANCE_SERVICE}")
    private String url;

    public CommonResponse<Object> testStress(GetBalanceRequest request, String requestId, String token, Map<String, String> header) {

        String balanceServiceUrl = url + "/balance/getBalance";
        String notificationServiceUrl = url + "/notification/sendNotification";

        String jsonObject = gson.toJson(request);
        logger.info("{} | getBalance | jsonGetBalanceRequest={} | urlBalanceService={}", requestId, jsonObject, balanceServiceUrl);

        // call balance service
        CommonResponse<Object> response = getConnectionToService(jsonObject, balanceServiceUrl, requestId, token, header);

        // call notification service

        logger.info("{} | getBalance | jsonGetBalanceRequest={} | urlBalanceService={} | response={}", requestId, jsonObject, balanceServiceUrl, response);
        return response;
    }

    private CommonResponse<Object> getConnectionToService(String jsonObject, String endPoint, String requestId,
                                                            String token, Map<String, String> header) {

        String device = header.get("device");
        String language = header.get("language");
        CommonResponse<Object> res = new CommonResponse<>();
        logger.info("{} | Connection Balance Service start | jsonObject={} | endPoint={}  ", requestId, jsonObject,
                endPoint);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpPost request = new HttpPost(endPoint);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonObject, "UTF-8"));
            request.addHeader("Authorization", "Bearer " + token);
            request.addHeader("requestId", requestId);
            request.addHeader("device", device);
            request.addHeader("language", language);
            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entityApi = response.getEntity();
            String rs = EntityUtils.toString(entityApi, "UTF-8");
            if (status == HttpStatus.SC_OK) {

                res = objectMapper.readValue(rs, CommonResponse.class);

            } else {
                logger.info("{} | Connection Balance Service | status={} | body={} ", requestId, status, rs);
                res.setData(EcodeConstant.PENDING);
                res.setEcode(EcodeConstant.PENDING);
            }
        } catch (Exception e) {
            logger.fatal("{} | Connection Balance Service Service | error={}", requestId, e);
            res.setEcode(EcodeConstant.EXCEPTION);
            res.setMessage(e.getMessage());
        }
        return res;
    }

}
