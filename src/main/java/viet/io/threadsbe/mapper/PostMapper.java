package viet.io.threadsbe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import viet.io.threadsbe.dto.NestedPostDTO;
import viet.io.threadsbe.dto.PostDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    NestedPostDTO toNestedPostDTO(PostDTO postDTO);
}
