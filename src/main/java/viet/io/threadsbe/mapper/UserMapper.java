package viet.io.threadsbe.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import viet.io.threadsbe.dto.CompactUserDTO;
import viet.io.threadsbe.dto.ProfileDTO;
import viet.io.threadsbe.dto.UserDTO;
import viet.io.threadsbe.dto.auth.UserInfo;
import viet.io.threadsbe.dto.response.Profile;
import viet.io.threadsbe.dto.response.SearchUserResponse;
import viet.io.threadsbe.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "name", target = "fullname")
    @Mapping(source = "picture", target = "image")
    @Mapping(target = "privacy", constant = "PUBLIC")
    @Mapping(target = "username", expression = "java(userInfo.getEmail().substring(0, userInfo.getEmail().indexOf('@')))")
    User userInfoToUser(UserInfo userInfo);

    UserDTO userToUserDTO(User user);

    CompactUserDTO userToCompactUserDTO(User user);
}
