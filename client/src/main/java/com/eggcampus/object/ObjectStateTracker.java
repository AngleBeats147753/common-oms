package com.eggcampus.object;

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
public class ObjectStateTracker {
    private List<String> newObjects;
    private List<String> deletedObjects;
    private List<String> stableObjects;
}
