package com.temquadra.temquadra_api.controller

import com.temquadra.temquadra_api.dto.CadastrarQuadraRequest
import com.temquadra.temquadra_api.dto.*
import com.temquadra.temquadra_api.service.QuadraService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/quadras")
class QuadraController(private val quadraService: QuadraService) {

    @GetMapping
    fun listarQuadras(): ResponseEntity<List<QuadraDetalhadaResponse>> {
        val quadras = quadraService.listarQuadras()
        return ResponseEntity.ok(quadras)
    }

    @GetMapping("/{id}")
    fun buscarQuadraPorId(@PathVariable id: UUID): ResponseEntity<QuadraDetalhadaResponse> {
        val quadra = quadraService.buscarQuadraPorId(id)
        return ResponseEntity.ok(quadra)
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun cadastrarQuadra(
        @RequestPart("dados") request: CadastrarQuadraRequest,
        @RequestPart("fotos", required = false) fotos: List<MultipartFile>?,
        @RequestHeader("X-User-Id", required = false) userIdHeader: String?
    ): ResponseEntity<QuadraDetalhadaResponse> {

        val userId = userIdHeader?.let { UUID.fromString(it) }
        val novaQuadra = quadraService.cadastrarQuadra(request, fotos, userId)

        return ResponseEntity.status(HttpStatus.CREATED).body(novaQuadra)
    }
}