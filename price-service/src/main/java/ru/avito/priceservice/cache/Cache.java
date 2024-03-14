package ru.avito.priceservice.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Cache {
    private final Map<Long, List<Long>> categories = new HashMap<>();
    private final Map<Long, List<Long>> locations = new HashMap<>();

    public void addIdsForCategories(Long segmentKey, List<Long> microcategoriesId) {
        categories.put(segmentKey, microcategoriesId);
    }

    public void addIdsForLocations(Long segmentKey, List<Long> locationsId) {
        locations.put(segmentKey, locationsId);
    }

    public List<Long> categoryIds(Long segmentKey) {
        return categories.get(segmentKey);
    }

    public List<Long> locationsIds(Long segmentKey) {
        return locations.get(segmentKey);
    }
}
