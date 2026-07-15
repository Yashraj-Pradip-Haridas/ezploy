package org.haridas.ezploy.common.dto.response;

public record PaginationResponse (
    int currentPage,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    int numberOfElements,
    boolean hasNext,
    boolean hasPrevious
){}
