package me.shinsunyoung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.AddUserRequest;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long save(AddUserRequest dto){
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("unexpected user"));
    }
}
