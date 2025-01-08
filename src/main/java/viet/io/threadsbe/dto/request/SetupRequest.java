package viet.io.threadsbe.dto.request;

import lombok.Data;
import viet.io.threadsbe.utils.enums.Privacy;

@Data
public class SetupRequest {
    private String bio;
    private String link;
    private Privacy privacy;
}
