package topg.bimber_user_service.service;

import topg.bimber_user_service.dto.UserAndAdminUpdateDto;
import topg.bimber_user_service.dto.UserCreatedDto;
import topg.bimber_user_service.dto.UserRequestDto;
import topg.bimber_user_service.dto.UserResponseDto;

import java.math.BigDecimal;

public interface IUserService {
    UserCreatedDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(String userId);
    UserResponseDto editUserById(UserAndAdminUpdateDto userAndAdminUpdateDto, String userId);
    String deleteUserById(String userId);
    String fundAccount(String userId, BigDecimal amount);
}
