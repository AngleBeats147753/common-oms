package com.eggcampus.oms.server.config;

import com.eggcampus.oms.server.service.QiniuyunObjectService;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author 黄磊
 */
public class QiniuyunSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                QiniuyunObjectService.class.getName(),
        };
    }
}
