package com.eggcampus.oms.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.util.springboot.mybatisplus.OffsetPageDto;
import com.campus.util.springboot.mybatisplus.OffsetPageQo;
import com.campus.util.springboot.mybatisplus.PageUtil;
import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.api.pojo.dto.OmsApplicationDto;
import com.eggcampus.oms.api.pojo.qo.GetApplicationQo;
import com.eggcampus.oms.server.manager.ApplicationManager;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 黄磊
 */
@Service
public class ApplicationReadService {
    @Resource
    private ApplicationManager applicationManager;

    public ReturnResult getApplication(GetApplicationQo qo) {
        ApplicationDo application = applicationManager.getByNameAndProfile(qo.getProjectName(), qo.getProfile());
        return ReturnResult.success(application == null ? null : new OmsApplicationDto(application));
    }

    public ReturnResult getApplications(OffsetPageQo pageQo) {
        QueryWrapper<ApplicationDo> wrapper = new QueryWrapper<ApplicationDo>()
                .orderByDesc(ApplicationDo.ID);
        OffsetPageDto<ApplicationDo> page = applicationManager.page(new OffsetPageDto<>(pageQo), wrapper);
        List<OmsApplicationDto> list = page.getRecords().stream().map(OmsApplicationDto::new).toList();
        return ReturnResult.success(PageUtil.changeRecord(page, list));
    }
}
