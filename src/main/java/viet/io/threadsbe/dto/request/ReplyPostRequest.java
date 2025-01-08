package viet.io.threadsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import viet.io.threadsbe.utils.enums.PostPrivacy;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReplyPostRequest {
    private String text;
    private String[] images;
    private PostPrivacy privacy;
    private UUID postId;
    private UUID postAuthor;
}
