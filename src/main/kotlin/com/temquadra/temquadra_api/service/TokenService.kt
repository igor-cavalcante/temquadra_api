package com.temquadra.temquadra_api.service

import com.temquadra.temquadra_api.domain.Usuario
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TokenService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {

    fun gerarToken(usuario: Usuario): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val agora = Date()
        val dataExpiracao = Date(agora.time + expiration)

        return Jwts.builder()
            .setSubject(usuario.idUsuario.toString())
            .claim("email", usuario.email)
            .claim("role", usuario.role.name)
            .setIssuedAt(agora)
            .setExpiration(dataExpiracao)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}