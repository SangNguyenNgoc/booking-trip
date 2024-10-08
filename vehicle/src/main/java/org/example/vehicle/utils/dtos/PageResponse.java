package org.example.vehicle.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Integer totalPage;
    private Integer currentPage;
    private ListResponse<T> data;
}
