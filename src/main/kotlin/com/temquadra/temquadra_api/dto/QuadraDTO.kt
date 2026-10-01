package com.temquadra.temquadra_api.dto

import com.temquadra.temquadra_api.domain.Quadra
import java.util.UUID

data class CadastrarQuadraRequest(
    val nome: String,
    val descricao: String?,
    val endereco: String,
    val latitude: Double?,
    val longitude: Double?,
    val bairroRegiao: String?,
    val tipoPiso: String,
    val cobertura: String,
    val iluminacao: Boolean,
    val acessibilidade: Boolean
)

data class QuadraFotoResponse(
    val idQuadraImage: UUID,
    val urlMinio: String,
    val dataCriacao: String?
)

data class QuadraDetalhadaResponse(
    val idQuadra: UUID,
    val userId: UUID?,
    val nome: String,
    val descricao: String?,
    val endereco: String,
    val latitude: Double?,
    val longitude: Double?,
    val bairroRegiao: String?,
    val tipoPiso: String,
    val cobertura: String,
    val iluminacao: Boolean,
    val acessibilidade: Boolean,
    val ativa: Boolean,
    val fotos: List<QuadraFotoResponse>,
    val dataCriacao: String?
)

data class AtualizarQuadraRequest(
    val nome: String,
    val descricao: String?,
    val endereco: String,
    val latitude: Double?,
    val longitude: Double?,
    val bairroRegiao: String?,
    val tipoPiso: String,
    val cobertura: String,
    val iluminacao: Boolean,
    val acessibilidade: Boolean,
    val ativa: Boolean
)