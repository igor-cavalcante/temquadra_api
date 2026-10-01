package com.temquadra.temquadra_api.repository

import com.temquadra.temquadra_api.domain.Quadra
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface QuadraRepository : JpaRepository<Quadra, UUID> {

    @Query("SELECT DISTINCT q FROM Quadra q LEFT JOIN FETCH q.imagens WHERE q.ativa = true ORDER BY q.dataCriacao DESC")
    fun findAllComImagens(): List<Quadra>

    @Query("SELECT q FROM Quadra q LEFT JOIN FETCH q.imagens WHERE q.idQuadra = :idQuadra AND q.ativa = true")
    fun findByIdComImagens(idQuadra: UUID): Optional<Quadra>
}