package viet.io.threadsbe.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import viet.io.threadsbe.utils.enums.PostPrivacy;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {

    @NotBlank(message = "Text cannot be blank")
    @Size(max = 500, message = "Text cannot exceed 500 characters")
    private String text;

    @Pattern(regexp = "^(https?://.*)?$", message = "Image URL must be a valid URL or empty")
    private String[] images;

    private PostPrivacy privacy = PostPrivacy.ANYONE;

    private UUID quoteId;

    private UUID postAuthor;
}
