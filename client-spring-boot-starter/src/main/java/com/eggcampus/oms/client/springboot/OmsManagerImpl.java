package com.eggcampus.oms.client.springboot;

import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.dto.OmsApplicationDto;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDto;
import com.eggcampus.oms.api.pojo.qo.DeleteObjectQo;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQo;
import com.eggcampus.oms.api.pojo.qo.UseObjectQo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author 黄磊
 */
@Slf4j
public class OmsManagerImpl implements OmsManager {

    private final OmsApplicationDto application;
    private final OmsFeignManager omsFeignManager;
    private final ObjectMapper objectMapper;

    public OmsManagerImpl(OmsFeignManager omsFeignManager, OmsApplicationDto application, ObjectMapper objectMapper) {
        this.omsFeignManager = omsFeignManager;
        this.application = application;
        this.objectMapper = objectMapper;
    }

    @Override
    public UploadTokenDto generateImageUploadToken(@NonNull String imageName) {
        Object data = omsFeignManager.generateUploadToken(new UploadTokenGenerationQo(application.getApplicationId(), imageName)).getData();
        return objectMapper.convertValue(data, UploadTokenDto.class);
    }

    @Override
    public void use(@Nullable String urls) {
        if (urls == null) {
            return;
        }
        use(OmsUtil.convert2List(urls));
    }

    @Override
    public void use(@Nullable Collection<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        UseObjectQo qo = new UseObjectQo(application.getApplicationId(), urls);
        omsFeignManager.use(qo);
        log.info("使用资源: {}", urls);
    }

    @Override
    public void delete(@Nullable String urls) {
        if (urls == null) {
            return;
        }
        delete(OmsUtil.convert2List(urls));
    }

    @Override
    public void delete(@Nullable Collection<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        DeleteObjectQo qo = new DeleteObjectQo(application.getApplicationId(), urls);
        omsFeignManager.delete(qo);
        log.info("删除资源: {}", urls);
    }

    @Override
    public void change(@Nullable String oldUrls, @Nullable String newUrls) {
        List<String> oldUrlsList = oldUrls == null ? List.of() : OmsUtil.convert2List(oldUrls);
        List<String> newUrlsList = newUrls == null ? List.of() : OmsUtil.convert2List(newUrls);
        change(oldUrlsList, newUrlsList);
    }

    @Override
    public void change(@Nullable Collection<String> oldUrls, @Nullable Collection<String> newUrls) {
        if (oldUrls == null) {
            oldUrls = List.of();
        }
        if (newUrls == null) {
            newUrls = List.of();
        }
        ObjectStateTracker tracker = OmsUtil.compare(oldUrls, newUrls);
        this.use(tracker.getNewObjects());
        this.delete(tracker.getDeletedObjects());
    }
}
