package com.eggcampus.image.server.config;

import com.eggcampus.image.server.service.QiniuyunImageService;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author 黄磊
 */
public class QiniuyunSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                QiniuyunImageService.class.getName(),
        };
    }
}
