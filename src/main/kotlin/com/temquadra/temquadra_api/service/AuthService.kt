package com.temquadra.temquadra_api.service

import com.temquadra.temquadra_api.domain.*
import com.temquadra.temquadra_api.dto.LoginRequest
import com.temquadra.temquadra_api.dto.LoginResponse
import com.temquadra.temquadra_api.dto.RegisterRequest
import com.temquadra.temquadra_api.repository.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) {

    @Transactional
    fun registrar(request: RegisterRequest): LoginResponse {
        if (usuarioRepository.findByEmail(request.email).isPresent) {
            throw IllegalArgumentException("E-mail já cadastrado!")
        }

        val novoUsuario = Usuario(
            name = request.name,
            email = request.email,
            passwords = passwordEncoder.encode(request.passwords!!), // Criptografa a senha com BCrypt real
            role = RoleEnum.valueOf(request.role.uppercase())
        )

        val usuarioSalvo = usuarioRepository.save(novoUsuario)
        val token = tokenService.gerarToken(usuarioSalvo)

        return LoginResponse(
            token = token,
            userId = usuarioSalvo.idUsuario!!,
            name = usuarioSalvo.name,
            role = usuarioSalvo.role.name
        )
    }


    fun login(request: LoginRequest): LoginResponse {
        val usuario = usuarioRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("Credenciais inválidas.") }

        if (!passwordEncoder.matches(request.passwords, usuario.passwords)) {
            throw IllegalArgumentException("Credenciais inválidas.")
        }

        val token = tokenService.gerarToken(usuario)

        return LoginResponse(
            token = token,
            userId = usuario.idUsuario!!,
            name = usuario.name,
            role = usuario.role.name
        )
    }
}