package topg.bimber_user_service.service;

import topg.bimber_user_service.dto.UserAndAdminUpdateDto;
import topg.bimber_user_service.dto.UserCreatedDto;
import topg.bimber_user_service.dto.UserRequestDto;
import topg.bimber_user_service.dto.UserResponseDto;

public interface IAdminService {
    UserCreatedDto createAdmin(UserRequestDto userRequestDto);
    UserResponseDto getAdminById(String adminId);
    UserResponseDto editAdminById(UserAndAdminUpdateDto adminUpdateRequestDto, String adminId);
    String deleteAdminById(String adminId);
}
