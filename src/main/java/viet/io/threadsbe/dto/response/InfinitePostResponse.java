package viet.io.threadsbe.dto.response;

import viet.io.threadsbe.dto.PostDTO;

import java.util.List;

public class InfinitePostResponse {
    private List<PostDTO> posts;
    private int page;
    private int limit;
    private boolean hasMore;
}
