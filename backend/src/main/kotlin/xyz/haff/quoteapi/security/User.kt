package xyz.haff.quoteapi.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(
    val id: String,
    private val authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getUsername(): String = id

    override fun getPassword(): String? = null
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}