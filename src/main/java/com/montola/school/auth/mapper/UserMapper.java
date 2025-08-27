package com.montola.school.auth.mapper;

import com.montola.school.auth.dto.UserRegisterRequest;
import com.montola.school.auth.dto.UserResponse;
import com.montola.school.auth.model.User;
import org.mapstruct.Mapper;

/**
 * @author avidewan
 * @date 8/27/25
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRegisterRequest request);

    UserResponse toResponse(User user);
}
