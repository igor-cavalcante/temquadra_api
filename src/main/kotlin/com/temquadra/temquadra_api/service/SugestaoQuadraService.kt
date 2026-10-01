package com.temquadra.temquadra_api.service

import com.temquadra.temquadra_api.domain.CoberturaEnum
import com.temquadra.temquadra_api.domain.Quadra
import com.temquadra.temquadra_api.domain.QuadraImage
import com.temquadra.temquadra_api.domain.QuadraSugestao
import com.temquadra.temquadra_api.domain.StatusSugestaoEnum
import com.temquadra.temquadra_api.domain.SugestaoQuadraImage
import com.temquadra.temquadra_api.domain.TipoPisoEnum
import com.temquadra.temquadra_api.dto.AprovarSugestaoRequest
import com.temquadra.temquadra_api.dto.AtualizarQuadraRequest
import com.temquadra.temquadra_api.dto.CadastrarQuadraRequest
import com.temquadra.temquadra_api.dto.QuadraDetalhadaResponse
import com.temquadra.temquadra_api.dto.QuadraFotoResponse
import com.temquadra.temquadra_api.dto.QuadraSugestaoDetalhadaResponse
import com.temquadra.temquadra_api.dto.SugerirQuadraRequest
import com.temquadra.temquadra_api.repository.QuadraRepository
import com.temquadra.temquadra_api.repository.SugestaoQuadraRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class SugestaoQuadraService (
    private val quadraRepository: QuadraRepository,
    private val quadraSugestaoRepository: SugestaoQuadraRepository,
    private val storageService: StorageService
    ) {

        @Transactional(readOnly = true)
        fun listarQuadrasSugestoes(): List<QuadraSugestaoDetalhadaResponse> {
            return quadraSugestaoRepository.findAllComImagens().map { mapearSugestaoParaResponse(it) }
        }

        @Transactional
        fun criarSugestao(
            request: SugerirQuadraRequest,
            fotos: List<MultipartFile>?
        ): QuadraSugestao {
            val sugestao = QuadraSugestao(
                nomeSolicitante = request.nomeSolicitante,
                telefoneSolicitante = request.telefoneSolicitante,
                nome = request.nome,
                descricao = request.descricao,
                endereco = request.endereco,
                latitude = request.latitude,
                longitude = request.longitude,
                bairroRegiao = request.bairroRegiao,
                tipoPiso = TipoPisoEnum.valueOf(request.tipoPiso.uppercase()),
                cobertura = CoberturaEnum.valueOf(request.cobertura.uppercase()),
                iluminacao = request.iluminacao,
                acessibilidade = request.acessibilidade,
                status = StatusSugestaoEnum.PENDENTE
            )

            fotos?.filter { !it.isEmpty }?.forEach { foto ->
                val urlMinio = storageService.uploadFoto(foto)
                sugestao.imagens.add(SugestaoQuadraImage(sugestaoQuadra = sugestao, urlMinio = urlMinio))
            }

            return quadraSugestaoRepository.save(sugestao)
        }

        @Transactional
        fun atualizarQuadra(idQuadra: UUID, request: AtualizarQuadraRequest): QuadraDetalhadaResponse {
            val quadra = quadraRepository.findByIdComImagens(idQuadra)
                .orElseThrow { NoSuchElementException("Quadra não encontrada com o ID: $idQuadra") }

            // Atualização via reflexão nos dados existentes
            val quadraAtualizada = Quadra(
                idQuadra = quadra.idQuadra,
                userId = quadra.userId,
                nome = request.nome,
                descricao = request.descricao,
                endereco = request.endereco,
                latitude = request.latitude,
                longitude = request.longitude,
                bairroRegiao = request.bairroRegiao,
                tipoPiso = TipoPisoEnum.valueOf(request.tipoPiso.uppercase()),
                cobertura = CoberturaEnum.valueOf(request.cobertura.uppercase()),
                iluminacao = request.iluminacao,
                acessibilidade = request.acessibilidade,
                ativa = request.ativa,
                imagens = quadra.imagens
            )

            val salva = quadraRepository.save(quadraAtualizada)
            return mapearParaResponse(salva)
        }

    @Transactional
    fun aprovarSugestao(idSugestao: UUID, adminUserId: UUID?, request: AprovarSugestaoRequest): QuadraDetalhadaResponse {
        val sugestao = quadraSugestaoRepository.findByIdComImagens(idSugestao)
            .orElseThrow { NoSuchElementException("Sugestão não encontrada com o ID: $idSugestao") }

        if (sugestao.status == StatusSugestaoEnum.APROVADO) {
            throw IllegalStateException("Esta sugestão já foi aprovada anteriormente.")
        }

        // 1. Atualiza o status e notas da sugestão existente
        sugestao.status = StatusSugestaoEnum.APROVADO
        sugestao.observacaoAdmin = request.observacaoAdmin
        quadraSugestaoRepository.save(sugestao)

        // 2. Transfere os dados criando uma nova Quadra oficial no banco
        val novaQuadra = Quadra(
            userId = adminUserId,
            nome = sugestao.nome,
            descricao = sugestao.descricao,
            endereco = sugestao.endereco,
            latitude = sugestao.latitude,
            longitude = sugestao.longitude,
            bairroRegiao = sugestao.bairroRegiao,
            tipoPiso = sugestao.tipoPiso,
            cobertura = sugestao.cobertura,
            iluminacao = sugestao.iluminacao,
            acessibilidade = sugestao.acessibilidade,
            ativa = true
        )

        // 3. Copia todas as referências de fotos da sugestão para a nova quadra oficial
        sugestao.imagens.forEach { imgSugestao ->
            novaQuadra.imagens.add(
                QuadraImage(
                    quadra = novaQuadra,
                    urlMinio = imgSugestao.urlMinio
                )
            )
        }

        val quadraSalva = quadraRepository.save(novaQuadra)
        return mapearParaResponse(quadraSalva)
    }

    private fun parseTipoPiso(valor: String): TipoPisoEnum {
        return try {
            TipoPisoEnum.valueOf(valor.trim().uppercase())
        } catch (e: Exception) {
            TipoPisoEnum.CIMENTO
        }
    }

    private fun parseCobertura(valor: String): CoberturaEnum {
        return try {
            CoberturaEnum.valueOf(valor.trim().uppercase())
        } catch (e: Exception) {
            CoberturaEnum.DESCOBERTA
        }
    }

    private fun mapearParaResponse(quadra: Quadra): QuadraDetalhadaResponse {
        return QuadraDetalhadaResponse(
            idQuadra = quadra.idQuadra!!,
            userId = quadra.userId,
            nome = quadra.nome,
            descricao = quadra.descricao,
            endereco = quadra.endereco,
            latitude = quadra.latitude,
            longitude = quadra.longitude,
            bairroRegiao = quadra.bairroRegiao,
            tipoPiso = quadra.tipoPiso.name,
            cobertura = quadra.cobertura.name,
            iluminacao = quadra.iluminacao,
            acessibilidade = quadra.acessibilidade,
            ativa = quadra.ativa,
            fotos = quadra.imagens.map { img ->
                QuadraFotoResponse(
                    idQuadraImage = img.idQuadraImage!!,
                    urlMinio = img.urlMinio,
                    dataCriacao = img.dataCriacao?.toString()
                )
            },
            dataCriacao = quadra.dataCriacao?.toString()
        )
    }

    private fun mapearSugestaoParaResponse(sugestao: QuadraSugestao): QuadraSugestaoDetalhadaResponse {
        return QuadraSugestaoDetalhadaResponse(
            idSugestaoQuadra = sugestao.idSugestaoQuadra!!,
            nome = sugestao.nome,
            nomeSolicitante = sugestao.nomeSolicitante,
            telefoneSolicitante = sugestao.telefoneSolicitante,
            descricao = sugestao.descricao,
            endereco = sugestao.endereco,
            latitude = sugestao.latitude,
            longitude = sugestao.longitude,
            bairroRegiao = sugestao.bairroRegiao,
            tipoPiso = sugestao.tipoPiso.name,
            cobertura = sugestao.cobertura.name,
            iluminacao = sugestao.iluminacao,
            acessibilidade = sugestao.acessibilidade,
            status = sugestao.status.name,
            observacaoAdmin = sugestao.observacaoAdmin,
            fotos = sugestao.imagens.map { img ->
                QuadraFotoResponse(
                    idQuadraImage = img.idSugestaoQuadraImage!!,
                    urlMinio = img.urlMinio,
                    dataCriacao = img.dataCriacao?.toString()
                )
            },
            dataCriacao = sugestao.dataCriacao?.toString()
        )
    }
    }
