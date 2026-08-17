package com.gabriel.SpringEcom.mappers;

import com.gabriel.SpringEcom.dto.UserDTO.UserProfileResponseDTO;
import com.gabriel.SpringEcom.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "externalId", target = "id")
    UserProfileResponseDTO mapToProfileResponse(User user);

    List<UserProfileResponseDTO> toProfileResponseList(List<User> users);


}


