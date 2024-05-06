package com.eggcampus.oms.client.springboot;

import com.campus.util.springboot.application.EggCampusApplicationManager;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;
import com.eggcampus.oms.api.pojo.qo.UsageQuery;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @author 黄磊
 */
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
    public UploadTokenDTO generateUploadToken(String imageName) {
        Object data = omsFeignManager.generateUploadToken(new UploadTokenGenerationQuery(applicationManager.getApplication(), imageName)).getData();
        return objectMapper.convertValue(data, UploadTokenDTO.class);
    }

    @Override
    public void use(UsageQuery query) {
        omsFeignManager.use(List.of(query));
    }

    @Override
    public void use(List<UsageQuery> queries) {
        omsFeignManager.use(queries);
    }

    @Override
    public void delete(String url) {
        omsFeignManager.delete(List.of(url));
    }

    @Override
    public void delete(List<String> urls) {
        omsFeignManager.delete(urls);
    }

    @Override
    public void change(String oldUrls, String newUrls) {
        change(OmsUtil.convert2List(oldUrls), OmsUtil.convert2List(newUrls));
    }

    @Override
    public void change(List<String> oldUrls, List<String> newUrls) {
        ObjectStateTracker tracker = OmsUtil.compare(oldUrls, newUrls);
        this.use(tracker.getNewObjects().stream().map(url -> new UsageQuery(url, false)).toList());
        this.delete(tracker.getDeletedObjects());
    }
}
