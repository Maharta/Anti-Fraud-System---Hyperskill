package antifraud.presentation.controller;

import antifraud.business.exception.InvalidRoleException;
import antifraud.business.exception.RoleConflictException;
import antifraud.business.exception.UserNotFoundException;
import antifraud.business.exception.UsernameTakenException;
import antifraud.business.model.entity.User;
import antifraud.business.services.UserService;
import antifraud.presentation.DTO.error.ErrorResponse;
import antifraud.presentation.DTO.user.create.RegisterRequest;
import antifraud.presentation.DTO.user.delete.DeleteResponse;
import antifraud.presentation.DTO.user.read.UserResponse;
import antifraud.presentation.DTO.user.update.UpdateRoleRequest;
import antifraud.presentation.DTO.user.update.UpdateStatusRequest;
import antifraud.presentation.DTO.user.update.UpdateStatusResponse;
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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        User user = userService.registerNewUser(registerRequest);
        UserResponse registerResponse = new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole().getName());
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<UserResponse> changeUserRole(@RequestBody @Valid UpdateRoleRequest updateUserRoleRequest) {
        User updatedUser = userService.updateUserRole(updateUserRoleRequest);
        UserResponse userResponse = new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getUsername(), updatedUser.getRole().getName());

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<UpdateStatusResponse> changeUserLockedStatus(@RequestBody @Valid UpdateStatusRequest updateRequest) {
        User updatedUser = userService.updateUserStatus(updateRequest);
        String lockStatus = updatedUser.isLocked() ? "locked" : "unlocked";
        UpdateStatusResponse updateStatusResponse = new UpdateStatusResponse("User %s %s!".formatted(updatedUser.getUsername(), lockStatus));
        return new ResponseEntity<>(updateStatusResponse, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<DeleteResponse> deleteUserByUsername(@PathVariable String username) {
        User deletedUser = userService.deleteUserByUsername(username);
        return new ResponseEntity<>(new DeleteResponse(deletedUser.getUsername(), "Deleted successfully!"), HttpStatus.OK);
    }

    @ExceptionHandler({UsernameTakenException.class, RoleConflictException.class})
    public ResponseEntity<ErrorResponse> handleUsernameNotAvailable(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidRoleException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRole(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
