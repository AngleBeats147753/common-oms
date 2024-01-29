package com.eggcampus.image;

import cn.hutool.http.*;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eggcampus.image.enums.CheckStatus;
import com.eggcampus.image.pojo.UploadTokenDTO;
import com.eggcampus.util.application.ApplicationDTO;
import com.eggcampus.util.exception.EggCampusException;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import lombok.NonNull;

import java.util.List;

/**
 * @author 黄磊
 */
public class ImageClient {
    public static final String GENERATE_UPLOAD_TOKEN_URL = "/image/upload-token";
    public static final String USE_IMAGE_URL = "/image/use";
    public static final String MODIFY_CHECK_STATUS_URL = "/image/status";
    public static final String DELETE_IMAGE_URL = "/image";

    private final String baseUrl;
    private final Boolean needCheck;

    public ImageClient(String host) {
        this(host, false);
    }

    public ImageClient(String host, Boolean needCheck) {
        this("http://", host, needCheck);
    }

    public ImageClient(@NonNull String protocol, @NonNull String host, Boolean needCheck) {
        this.baseUrl = protocol + host;
        this.needCheck = needCheck;
    }

    public UploadTokenDTO generateUploadToken(String applicationName, String applicationProfile, String imageName) {
        ApplicationDTO application = new ApplicationDTO(applicationName, applicationProfile);
        return this.generateUploadToken(application, imageName);
    }

    public void useOrDeleteImage(List<String> newServiceImages, List<String> oldServiceImages) {
        this.useOrDeleteImage(newServiceImages, oldServiceImages, this.needCheck);
    }

    public void useOrDeleteImage(List<String> newServiceImages, List<String> oldServiceImages, Boolean needCheck) {
        ImageStateTracker imageStateTracker = ImageUtil.compare(oldServiceImages, newServiceImages);
        for (String newImage : imageStateTracker.getNewImages()) {
            this.useImage(newImage, needCheck);
        }
        for (String deletedImage : imageStateTracker.getDeletedImages()) {
            this.deleteImage(deletedImage);
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

    public void useImage(String imageURL, boolean needCheck) {
        JSONObject body = new JSONObject();
        body.set("imageURL", imageURL);
        body.set("needCheck", needCheck);

        HttpRequest request = HttpUtil.createRequest(Method.PUT, this.baseUrl + USE_IMAGE_URL).body(body.toString());
        executeRequest(request);
    }

    public void modifyImageCheckStatus(String imageURL, CheckStatus checkStatus) {
        JSONObject body = new JSONObject();
        body.set("imageURL", imageURL);
        body.set("checkStatus", checkStatus);

        HttpRequest request = HttpUtil.createRequest(Method.PUT, this.baseUrl + MODIFY_CHECK_STATUS_URL)
                .body(body.toString());
        executeRequest(request);
    }

    public void deleteImage(String imageURL) {
        HttpRequest request = HttpUtil.createRequest(Method.DELETE, this.baseUrl + DELETE_IMAGE_URL)
                .form("imageURL", imageURL);
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