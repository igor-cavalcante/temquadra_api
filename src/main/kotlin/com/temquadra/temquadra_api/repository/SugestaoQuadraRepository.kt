package com.temquadra.temquadra_api.repository

import com.temquadra.temquadra_api.domain.QuadraSugestao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface SugestaoQuadraRepository : JpaRepository<QuadraSugestao, UUID> {

    @Query("SELECT DISTINCT q FROM QuadraSugestao q LEFT JOIN FETCH q.imagens ORDER BY q.dataCriacao DESC")
    fun findAllComImagens(): List<QuadraSugestao>

    @Query("SELECT s FROM QuadraSugestao s LEFT JOIN FETCH s.imagens WHERE s.idSugestaoQuadra = :idSugestao")
    fun findByIdComImagens(@Param("idSugestao") idSugestao: UUID): Optional<QuadraSugestao>
}