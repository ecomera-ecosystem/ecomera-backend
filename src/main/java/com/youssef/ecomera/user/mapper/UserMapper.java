package com.youssef.ecomera.user.mapper;

import com.youssef.ecomera.common.mapper.BaseMapper;
import com.youssef.ecomera.common.mapper.BaseMappingConfig;
import com.youssef.ecomera.user.dto.UserDto;
import com.youssef.ecomera.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMappingConfig.class)
public interface UserMapper extends BaseMapper<User, UserDto> {

    @Override
    default User toEntity(UserDto userDto) {
        throw new UnsupportedOperationException("User creation goes through AuthenticationService");
    }
}
