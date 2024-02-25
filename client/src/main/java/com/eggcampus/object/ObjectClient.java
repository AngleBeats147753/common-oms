package com.eggcampus.object;

import cn.hutool.http.*;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.util.exception.EggCampusException;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import com.eggcampus.util.spring.application.ApplicationDTO;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄磊
 */
@Slf4j
public class ObjectClient {
    public static final String GENERATE_UPLOAD_TOKEN_URL = "/image/upload-token";
    public static final String USE_IMAGE_URL = "/object/use";

    public static final String USE_IMAGE_LIST_URL = "/object/useList";
    public static final String MODIFY_CHECK_STATUS_URL = "/object/status";
    public static final String DELETE_IMAGE_URL = "/object";
    public static final String DELETE_IMAG_LIST_URL = "/image/removeList";

    @Getter
    private final String baseUrl;

    private final Boolean needCheck;

    public ObjectClient(String host) {
        this(host, false);
    }

    public ObjectClient(String host, Boolean needCheck) {
        this("http://", host, needCheck);
    }

    public ObjectClient(@NonNull String protocol, @NonNull String host, Boolean needCheck) {
        this.baseUrl = protocol + host;
        this.needCheck = needCheck;
    }

    public UploadTokenDTO generateUploadToken(String applicationName, String applicationProfile, String imageName) {
        ApplicationDTO application = new ApplicationDTO(applicationName, applicationProfile);
        return this.generateUploadToken(application, imageName);
    }

    public void useOrDeleteObject(List<String> newServiceImages, List<String> oldServiceImages) {
        this.useOrDeleteObject(newServiceImages, oldServiceImages, this.needCheck);
    }

    public void useOrDeleteObject(List<String> newServiceImages, List<String> oldServiceImages, Boolean needCheck) {
        ObjectStateTracker objectStateTracker = ObjectUtil.compare(oldServiceImages, newServiceImages);
        for (String newImage : objectStateTracker.getNewObjects()) {
            this.useObject(newImage, needCheck);
        }
        for (String deletedImage : objectStateTracker.getDeletedObjects()) {
            this.deleteObject(deletedImage);
        }
    }

    public UploadTokenDTO generateUploadToken(ApplicationDTO application, String imageName) {
        JSONObject body = new JSONObject();
        body.set("application", JSONUtil.parseObj(application));
        body.set("imageName", imageName);

        HttpRequest request = HttpUtil.createPost(this.baseUrl + GENERATE_UPLOAD_TOKEN_URL).body(body.toString());
        JSONObject json = (JSONObject) executeRequest(request);
        return JSONUtil.toBean(json, UploadTokenDTO.class);
    }

    public void useObject(String imageURL, boolean needCheck) {
        JSONObject body = new JSONObject();
        body.set("imageURL", imageURL);
        body.set("needCheck", needCheck);

        HttpRequest request = HttpUtil.createRequest(Method.PUT, this.baseUrl + USE_IMAGE_URL).body(body.toString());
        executeRequest(request);
    }

    public void useObjectList(List<String> objectList, boolean needCheck) {
        JSONObject body = new JSONObject();
        List<UsageQO> usageQoList = new ArrayList<>(objectList.size());
        for (String s : objectList) {
            UsageQO usageQO = new UsageQO();
            usageQO.setImageURL(s);
            usageQO.setNeedCheck(needCheck);
        }
        body.set("usageQoList", usageQoList);
        HttpRequest request = HttpUtil.createRequest(Method.PUT, this.baseUrl + USE_IMAGE_LIST_URL).body(body.toString());
        executeRequest(request);
    }

    public void modifyObjectCheckStatus(String objectUrl, String checkStatus) {
        JSONObject body = new JSONObject();
        body.set("imageURL", objectUrl);
        body.set("checkStatus", checkStatus);

        HttpRequest request = HttpUtil.createRequest(Method.PUT, this.baseUrl + MODIFY_CHECK_STATUS_URL)
                .body(body.toString());
        executeRequest(request);
    }

    public void deleteObject(String objectUrl) {
        HttpRequest request = HttpUtil.createRequest(Method.DELETE, this.baseUrl + DELETE_IMAGE_URL)
                .form("objectUrl", objectUrl);
        executeRequest(request);
    }

    public void deleteObjectList(List<String> imageUrlList) {
        List<DeleteQO> deleteQoList = new ArrayList<>(imageUrlList.size());
        for (String s : imageUrlList) {
            DeleteQO deleteQO = new DeleteQO();
            deleteQO.setImageURL(s);
            deleteQoList.add(deleteQO);
        }
        HttpRequest request = HttpUtil.createRequest(Method.DELETE, this.baseUrl + DELETE_IMAG_LIST_URL)
                .form("deleteQoList", deleteQoList);
        executeRequest(request);
    }

    private JSON executeRequest(HttpRequest request) {
        try (HttpResponse response = request.execute()) {
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new EggCampusException(AliErrorCode.SERVICE_ERROR_C0001,
                        "图像服务器处理请求失败",
                        "图像服务器处理请求失败，url为<%s>，方法为<%s>，http状态为<%s>，请求体为<%s>".formatted(request.getUrl(), request.getMethod(), response.getStatus(), response.body()));
            }
            ReturnResult returnResult = JSONUtil.toBean(response.body(), ReturnResult.class);
            if (!AliErrorCode.SUCCESS.equals(returnResult.getStatus())) {
                throw new EggCampusException(AliErrorCode.SERVICE_ERROR_C0001,
                        "图像服务器处理请求失败",
                        "图像服务器处理请求失败，url为<%s>，方法为<%s>，服务状态为<%s>，错误信息为<%s>".formatted(request.getUrl(), request.getMethod(), returnResult.getStatus(), returnResult.getMessage()));
            }
            return (JSON) returnResult.getData();
        } catch (EggCampusException e) {
            throw e;
        } catch (Exception e) {
            throw new EggCampusException(AliErrorCode.SERVICE_ERROR_C0001, "发送请求失败，错误信息为：" + e.getMessage(), e);
        }
    }
}
