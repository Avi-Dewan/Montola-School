package com.montola.school.auth.mapper;

import com.montola.school.auth.dto.AdminRegistrationRequest;
import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.dto.UserResponse;
import com.montola.school.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRegisterRequest request);

    User toEntity(AdminRegistrationRequest request);

    @Mapping(target = "hasProfilePicture", expression = "java(user.getProfilePhoto() != null)")
    UserResponse toResponse(User user);
}
