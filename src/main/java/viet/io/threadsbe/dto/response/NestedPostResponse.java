package viet.io.threadsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.dto.NestedPostDTO;
import viet.io.threadsbe.dto.PostDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class NestedPostResponse {
    private NestedPostDTO postInfo;
    private List<PostDTO> parentPosts;
}
