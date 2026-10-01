package com.temquadra.temquadra_api.controller

import com.temquadra.temquadra_api.dto.AprovarSugestaoRequest
import com.temquadra.temquadra_api.dto.QuadraDetalhadaResponse
import com.temquadra.temquadra_api.dto.QuadraSugestaoDetalhadaResponse
import com.temquadra.temquadra_api.dto.SugerirQuadraRequest
import com.temquadra.temquadra_api.service.QuadraService
import com.temquadra.temquadra_api.service.SugestaoQuadraService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/quadras/sugestoes")
class SugestaoQuadraController(private val sugestaoQuadraService: SugestaoQuadraService) {

    @GetMapping
    fun listarSugestoes(): ResponseEntity<List<QuadraSugestaoDetalhadaResponse>> {
        val sugestoes = sugestaoQuadraService.listarQuadrasSugestoes()
        return ResponseEntity.ok(sugestoes)
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun sugerirQuadra(
        @RequestPart("dados") request: SugerirQuadraRequest,
        @RequestPart("fotos", required = false) fotos: List<MultipartFile>?
    ): ResponseEntity<Any> {
        val sugestaoSalva = sugestaoQuadraService.criarSugestao(request, fotos)
        return ResponseEntity.status(HttpStatus.CREATED).body(sugestaoSalva)
    }

    @PostMapping("/{idSugestao}/aprovar")
    fun aprovarSugestao(
        @PathVariable idSugestao: UUID,
        @RequestHeader("X-User-Id", required = false) adminUserIdHeader: String?,
        @RequestBody(required = false) request: AprovarSugestaoRequest?
    ): ResponseEntity<QuadraDetalhadaResponse> {
        val adminUserId = adminUserIdHeader?.let { UUID.fromString(it) }
        val novaQuadra = sugestaoQuadraService.aprovarSugestao(
            idSugestao = idSugestao,
            adminUserId = adminUserId,
            request = request ?: AprovarSugestaoRequest()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(novaQuadra)
    }
}