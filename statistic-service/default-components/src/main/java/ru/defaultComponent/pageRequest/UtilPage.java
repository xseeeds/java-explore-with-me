package ru.defaultComponent.pageRequest;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class UtilPage {

    public PageRequest getPageSortAscByProperties(int from, int size, String properties) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, properties);
    }

    public PageRequest getPageSortDescByProperties(int from, int size, String properties) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, properties));
    }

    public PageRequest getPage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}
