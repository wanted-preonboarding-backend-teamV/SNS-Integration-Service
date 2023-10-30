package com.wanted.teamV.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResDto<PostResDto> {

    private List<PostResDto> content;
    private Pageable pageable;
    private boolean last;
    private long totalPages;
    private long totalElements;
    private long size;
    private long number;
    private Sort sort;
    private long numberOfElements;
    private boolean first;
    private boolean empty;

    @Getter
    @Setter
    public static class Pageable {
        private Sort sort;
        private long offset;
        private long pageSize;
        private long pageNumber;
        private boolean paged;
        private boolean unpaged;
    }

    @Getter
    @Setter
    public static class Sort {
        private boolean sorted;
        private boolean unsorted;
        private boolean empty;
    }

}
