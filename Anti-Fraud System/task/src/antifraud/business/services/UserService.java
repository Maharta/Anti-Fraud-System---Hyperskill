package antifraud.business.services;

import antifraud.business.exceptions.UserNotFoundException;
import antifraud.business.exceptions.UsernameTakenException;
import antifraud.business.model.Role;
import antifraud.business.model.User;
import antifraud.business.security.UserDetailsImpl;
import antifraud.persistence.RoleRepository;
import antifraud.persistence.UserRepository;
import antifraud.presentation.DTO.user.RegisterRequest;
import antifraud.presentation.DTO.user.UserResponse;
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

    public User registerNewUser(RegisterRequest registerRequest) throws UsernameTakenException {

        boolean isUsernameAvailable = checkUsernameAvailability(registerRequest.username().toLowerCase());
        if (!isUsernameAvailable) {
            throw new UsernameTakenException(registerRequest.username() + " is already registered!");
        }
        User user = new User(registerRequest.name(), registerRequest.username(), bcryptEncoder.encode(registerRequest.password()));
        preventRolesDuplicates(user.getRolesAndAuthorities());
        userRepository.save(user);
        return user;
    }


    public List<UserResponse> getAllUsers() {
        Iterable<User> userIterable = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userIterable) {
                userResponseList.add(new UserResponse(user.getId(), user.getName(), user.getUsername()));
        }
        return userResponseList;
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

    private void preventRolesDuplicates(List<Role> rolesAndAuthorities) {
        for (int i = 0; i < rolesAndAuthorities.size(); i++) {
            Role current = rolesAndAuthorities.get(i);
            Role role = roleRepository.findByName(current.getName());
            if (role != null) {
                rolesAndAuthorities.set(i, role);
            }
        }
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
