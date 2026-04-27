package t.lab.guide.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import t.lab.guide.mapper.toAppUserDetails
import t.lab.guide.repository.UserRepository

@Service
class AppUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository
            .findLoginViewByUsername(username)
            ?.toAppUserDetails()
            ?: throw UsernameNotFoundException("User with username '$username' not found")
}
