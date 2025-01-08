package viet.io.threadsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;    // The list of data
    private int currentPage;        // Current page
    private int pageSize;       // Page size
    private long totalItems;      // Total number of elements
}