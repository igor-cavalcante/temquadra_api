package com.temquadra.temquadra_api.dto

import java.util.UUID

data class SugerirQuadraRequest(
    val nomeSolicitante: String? = null,
    val telefoneSolicitante: String? = null,
    val nome: String,
    val descricao: String? = null,
    val endereco: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val bairroRegiao: String? = null,
    val tipoPiso: String,
    val cobertura: String,
    val iluminacao: Boolean = false,
    val acessibilidade: Boolean = false
)

data class QuadraSugestaoFotoResponse(
    val idQuadraImage: UUID,
    val urlMinio: String,
    val dataCriacao: String?
)

// --- DTO para Aprovação de Sugestão pelo Admin ---
data class AprovarSugestaoRequest(
    val observacaoAdmin: String? = null
)

data class QuadraSugestaoDetalhadaResponse(
    val idSugestaoQuadra: UUID,
    val nome: String,
    val nomeSolicitante: String?,
    val telefoneSolicitante: String?,
    val descricao: String?,
    val endereco: String,
    val latitude: Double?,
    val longitude: Double?,
    val bairroRegiao: String?,
    val tipoPiso: String,
    val cobertura: String,
    val iluminacao: Boolean,
    val acessibilidade: Boolean,
    val status: String,
    val observacaoAdmin: String?,
    val fotos: List<QuadraFotoResponse>,
    val dataCriacao: String?
)