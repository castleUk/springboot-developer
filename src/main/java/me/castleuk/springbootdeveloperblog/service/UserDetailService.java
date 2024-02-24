package me.castleuk.springbootdeveloperblog.service;

import lombok.RequiredArgsConstructor;
import me.castleuk.springbootdeveloperblog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 이름(Email)으로 사용자의 정보를 가져오는 메서드 =  필수로 구현해야 함
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
