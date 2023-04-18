package antifraud.presentation.controller;

import antifraud.business.exceptions.UserNotFoundException;
import antifraud.business.exceptions.UsernameTakenException;
import antifraud.business.model.User;
import antifraud.business.services.UserService;
import antifraud.presentation.DTO.user.DeleteResponse;
import antifraud.presentation.DTO.user.RegisterRequest;
import antifraud.presentation.DTO.user.UserResponse;
import antifraud.presentation.DTO.error.ErrorResponse;
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

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest userRequest) {
        User user = userService.registerNewUser(userRequest);
        UserResponse registerResponse = new UserResponse(user.getId(), user.getName(), user.getUsername());
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<DeleteResponse> deleteUserByUsername(@PathVariable String username) {
        User deletedUser = userService.deleteUserByUsername(username);
        return new ResponseEntity<>(new DeleteResponse(deletedUser.getUsername(), "Deleted successfully!"), HttpStatus.OK);
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotAvailable(UsernameTakenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


}
