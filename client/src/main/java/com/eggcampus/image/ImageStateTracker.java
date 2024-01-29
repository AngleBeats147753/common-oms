package com.eggcampus.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageStateTracker {
    private List<String> newImages;
    private List<String> deletedImages;
    private List<String> stableImages;
}
