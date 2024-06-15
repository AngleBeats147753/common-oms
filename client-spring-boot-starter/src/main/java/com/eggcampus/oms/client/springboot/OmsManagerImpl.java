package com.eggcampus.oms.client.springboot;

import com.campus.util.springboot.application.EggCampusApplicationManager;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author 黄磊
 */
@Slf4j
public class OmsManagerImpl implements OmsManager {

    private final EggCampusApplicationManager applicationManager;
    private final OmsFeignManager omsFeignManager;
    private final ObjectMapper objectMapper;

    public OmsManagerImpl(OmsFeignManager omsFeignManager, EggCampusApplicationManager applicationManager, ObjectMapper objectMapper) {
        this.omsFeignManager = omsFeignManager;
        this.applicationManager = applicationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public UploadTokenDTO generateImageUploadToken(@NonNull String imageName) {
        Object data = omsFeignManager.generateUploadToken(new UploadTokenGenerationQuery(applicationManager.getApplication(), imageName)).getData();
        return objectMapper.convertValue(data, UploadTokenDTO.class);
    }

    @Override
    public void use(String urls) {
        if (urls == null) {
            return;
        }
        use(OmsUtil.convert2List(urls));
    }

    @Override
    public void use(Collection<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        omsFeignManager.use(new HashSet<>(urls));
        log.info("使用资源: {}", urls);
    }

    @Override
    public void delete(String urls) {
        if (urls == null) {
            return;
        }
        delete(OmsUtil.convert2List(urls));
    }

    @Override
    public void delete(Collection<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        omsFeignManager.delete(new HashSet<>(urls));
        log.info("删除资源: {}", urls);
    }

    @Override
    public void change(String oldUrls, String newUrls) {
        List<String> oldUrlsList = oldUrls == null ? List.of() : OmsUtil.convert2List(oldUrls);
        List<String> newUrlsList = newUrls == null ? List.of() : OmsUtil.convert2List(newUrls);
        change(oldUrlsList, newUrlsList);
    }

    @Override
    public void change(Collection<String> oldUrls, Collection<String> newUrls) {
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
