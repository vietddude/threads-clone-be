package viet.io.threadsbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompactUserDTO {
    private UUID id;
    private String username;
    private String image;
}