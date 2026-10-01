package com.temquadra.temquadra_api.repository

import com.temquadra.temquadra_api.domain.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UsuarioRepository : JpaRepository<Usuario, UUID> {
    fun findByEmail(email: String): Optional<Usuario>
}