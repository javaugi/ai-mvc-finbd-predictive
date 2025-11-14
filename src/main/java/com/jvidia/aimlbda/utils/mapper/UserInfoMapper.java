package com.jvidia.aimlbda.utils.mapper;

import com.jvidia.aimlbda.dto.SignupRequest;
import com.jvidia.aimlbda.entity.UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    UserInfo singupRequestDtoToUserInfo(SignupRequest signupRequest);
    SignupRequest userInfoToSignupRequest(UserInfo userInfo);

}
