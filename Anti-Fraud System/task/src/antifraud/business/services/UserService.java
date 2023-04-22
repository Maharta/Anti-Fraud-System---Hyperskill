package antifraud.business.services;

import antifraud.business.exception.InvalidRoleException;
import antifraud.business.exception.RoleConflictException;
import antifraud.business.exception.UserNotFoundException;
import antifraud.business.exception.UsernameTakenException;
import antifraud.business.model.entity.Role;
import antifraud.business.model.entity.User;
import antifraud.business.model.enums.RoleEnum;
import antifraud.business.model.enums.StatusOperation;
import antifraud.business.security.UserDetailsImpl;
import antifraud.persistence.RoleRepository;
import antifraud.persistence.UserRepository;
import antifraud.presentation.DTO.user.create.RegisterRequest;
import antifraud.presentation.DTO.user.read.UserResponse;
import antifraud.presentation.DTO.user.update.UpdateRoleRequest;
import antifraud.presentation.DTO.user.update.UpdateStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bcryptEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bcryptEncoder = encoder;
    }


    public List<UserResponse> getAllUsers() {
        Iterable<User> userIterable = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userIterable) {
            userResponseList.add(new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getRole().getName()));
        }
        return userResponseList;
    }


    public User registerNewUser(RegisterRequest registerRequest) throws UsernameTakenException {
        boolean isFirstUser = userRepository.count() == 0;
        if (isFirstUser) {
            return registerNewAdmin(registerRequest);
        }

        boolean isUsernameAvailable = checkUsernameAvailability(registerRequest.username().toLowerCase());
        if (!isUsernameAvailable) {
            throw new UsernameTakenException(registerRequest.username() + " is already registered!");
        }

        User user = new User(registerRequest.name(), registerRequest.username(), bcryptEncoder.encode(registerRequest.password()), new Role(RoleEnum.MERCHANT));
        user.setRole(preventRolesDuplicates(user.getRole()));
        userRepository.save(user);
        return user;
    }

    private User registerNewAdmin(RegisterRequest registerRequest) {
        User user = new User(registerRequest.name(), registerRequest.username(), bcryptEncoder.encode(registerRequest.password()), new Role(RoleEnum.ADMINISTRATOR));
        userRepository.save(user);
        return user;
    }

    public User updateUserRole(UpdateRoleRequest updateUserRoleRequest) {
        if (!updateUserRoleRequest.role().equals("SUPPORT") && !updateUserRoleRequest.role().equals("MERCHANT")) {
            throw new InvalidRoleException("Role must be SUPPORT or MERCHANT");
        }

        Optional<User> userToUpdate = userRepository.findByUsername(updateUserRoleRequest.username());
        if (userToUpdate.isEmpty()) {
            throw new UserNotFoundException("Can't find the user to be updated.");
        }

        User foundUser = userToUpdate.get();
        if (foundUser.getRole().getName().equals(updateUserRoleRequest.role())) {
            throw new RoleConflictException("This user already has " + updateUserRoleRequest.role() + " role.");
        }

        Role role = preventRolesDuplicates(new Role(RoleEnum.valueOf(updateUserRoleRequest.role())));
        foundUser.setRole(role);

        return userRepository.save(foundUser);
    }

    public User updateUserStatus(UpdateStatusRequest updateRequest) {
        Optional<User> userToUpdate = userRepository.findByUsername(updateRequest.username());
        if (userToUpdate.isEmpty()) {
            throw new UserNotFoundException("Can't find the user to be updated.");
        }

        User foundUser = userToUpdate.get();

        boolean isLockRequested = updateRequest.operation().equals(StatusOperation.LOCK);

        if (foundUser.getRole().getName().equals("ADMINISTRATOR") && isLockRequested) {
            throw new IllegalArgumentException("ADMINISTRATOR account can't be locked!");
        }

        foundUser.setLocked(isLockRequested);

        return userRepository.save(foundUser);
    }


    public User deleteUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Can't find the user to be deleted.");
        }

        User userToDelete = user.get();

        userRepository.delete(userToDelete);
        return userToDelete;
    }

    private Role preventRolesDuplicates(Role role) {
        Optional<Role> optionalRole = roleRepository.findByName(role.getName());

        return optionalRole.orElse(role);
    }

    private boolean checkUsernameAvailability(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.isEmpty();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username.toLowerCase());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        return new UserDetailsImpl(user.get());
    }


}
