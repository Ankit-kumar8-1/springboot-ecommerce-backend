package com.ankitsaahariya.Util;


import com.ankitsaahariya.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public class PaginationUtil {

    private PaginationUtil(){

    }

    public static Pageable createPageRequest(int page, int size, String sortBy){
        return PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,sortBy));
    }

    public static Pageable createPageRequest(int page, int size){
        return PageRequest.of(page,size);
    }

    public static <T,R> PageResponse<R> toPageResponse(Page<T> page, Function<T,R> mapper){
        List<R> content = page.stream().map(mapper).toList();

        return new PageResponse<>(content,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages());
    }

    public static <R> PageResponse<R> toPageResponse(Page<?> page,List<R>  mapperContent ){
        return new PageResponse<>(mapperContent,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages());
    }
}