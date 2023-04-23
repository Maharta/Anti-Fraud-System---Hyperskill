package antifraud.presentation.controller;

import antifraud.business.exception.InvalidRoleException;
import antifraud.business.exception.UserNotFoundException;
import antifraud.business.model.entity.User;
import antifraud.business.services.UserService;
import antifraud.presentation.DTO.StatusResponseDTO;
import antifraud.presentation.DTO.error.ErrorResponseDTO;
import antifraud.presentation.DTO.user.UserRequestDTO;
import antifraud.presentation.DTO.user.UserResponseDTO;
import antifraud.presentation.DTO.user.delete.DeleteResponseDTO;
import antifraud.presentation.DTO.user.update.UpdateRoleRequestDTO;
import antifraud.presentation.DTO.user.update.UpdateStatusRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO registerRequest) {
        User user = userService.registerNewUser(registerRequest);
        UserResponseDTO registerResponse = new UserResponseDTO(user.getId(), user.getName(), user.getUsername(), user.getRole().getName());
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<UserResponseDTO> changeUserRole(@RequestBody @Valid UpdateRoleRequestDTO updateUserRoleRequest) {
        User updatedUser = userService.updateUserRole(updateUserRoleRequest);
        UserResponseDTO userResponse = new UserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getUsername(), updatedUser.getRole().getName());

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<StatusResponseDTO> changeUserLockedStatus(@RequestBody @Valid UpdateStatusRequestDTO updateRequest) {
        User updatedUser = userService.updateUserStatus(updateRequest);
        String lockStatus = updatedUser.isLocked() ? "locked" : "unlocked";
        StatusResponseDTO updateStatusResponse = new StatusResponseDTO("User %s %s!".formatted(updatedUser.getUsername(), lockStatus));
        return new ResponseEntity<>(updateStatusResponse, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<DeleteResponseDTO> deleteUserByUsername(@PathVariable String username) {
        User deletedUser = userService.deleteUserByUsername(username);
        return new ResponseEntity<>(new DeleteResponseDTO(deletedUser.getUsername(), "Deleted successfully!"), HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRole(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
