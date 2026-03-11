package org.springboot.pdv.security;


import org.springboot.pdv.dto.LoginDTO;
import org.springboot.pdv.entity.User;
import org.springboot.pdv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }
        return new UserPrincipal(user);
    }

    public void verifyUserCredentials(LoginDTO login) {
        UserDetails user = loadUserByUsername(login.getUsername());
        boolean passwordIsTheSame =  SecurityConfig.passwordEncoder()
                .matches(login.getPassword(), user.getPassword());

        if (!passwordIsTheSame) {
            throw new BadCredentialsException("Senha incorreta");
        }
    }
}
