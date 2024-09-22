package org.example.authserver.service;

import lombok.RequiredArgsConstructor;
import org.example.authserver.dto.AccountCreateRequest;
import org.example.authserver.interfaces.RoleRepository;
import org.example.authserver.interfaces.UserMapper;
import org.example.authserver.interfaces.UserRepository;
import org.springframework.stereotype.Service;

public interface UserService {
    void createAccount(AccountCreateRequest accountCreateRequest);
}
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService{

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void createAccount(AccountCreateRequest accountCreateRequest) {
        var role = roleRepository.findById(2).orElseThrow();
        var newAccountUser = userMapper.toEntity(accountCreateRequest);
        newAccountUser.setRole(role);
        userRepository.save(newAccountUser);
    }
}
