package com.techtraining.userservice.mapper;

import com.techtraining.userservice.dto.request.UserRequest;
import com.techtraining.userservice.dto.response.UserResponse;
import com.techtraining.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserMapper {
    UserResponse toResponse(User user);
    
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRequest request);
    
    List<UserResponse> toResponseList(List<User> users);
}
