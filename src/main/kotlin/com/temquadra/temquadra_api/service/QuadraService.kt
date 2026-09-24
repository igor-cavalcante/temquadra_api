package com.temquadra.temquadra_api.service



import com.temquadra.temquadra_api.domain.*
import com.temquadra.temquadra_api.dto.CadastrarQuadraRequest
import com.temquadra.temquadra_api.dto.*
import com.temquadra.temquadra_api.repository.QuadraRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
@Service
class QuadraService(
    private val quadraRepository: QuadraRepository,
    private val storageService: StorageService
) {

    @Transactional(readOnly = true)
    fun listarQuadras(): List<QuadraDetalhadaResponse> {
        return quadraRepository.findAllComImagens().map { quadra ->
            QuadraDetalhadaResponse(
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
    }

    @Transactional(readOnly = true)
    fun buscarQuadraPorId(idQuadra: UUID): QuadraDetalhadaResponse {
        val quadra = quadraRepository.findByIdComImagens(idQuadra)
            .orElseThrow { NoSuchElementException("Quadra não encontrada com o ID: $idQuadra") }

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

    @Transactional
    fun cadastrarQuadra(
        request: CadastrarQuadraRequest,
        fotos: List<MultipartFile>?,
        userId: UUID?
    ): QuadraDetalhadaResponse {

        val novaQuadra = Quadra(
            userId = userId,
            nome = request.nome,
            descricao = request.descricao,
            endereco = request.endereco,
            latitude = request.latitude,
            longitude = request.longitude,
            bairroRegiao = request.bairroRegiao,
            tipoPiso = TipoPisoEnum.valueOf(request.tipoPiso.uppercase()),
            cobertura = CoberturaEnum.valueOf(request.cobertura.uppercase()),
            iluminacao = request.iluminacao,
            acessibilidade = request.acessibilidade
        )

        fotos?.filter { !it.isEmpty }?.forEach { foto ->
            val urlMinio = storageService.uploadFoto(foto)
            novaQuadra.imagens.add(QuadraImage(quadra = novaQuadra, urlMinio = urlMinio))
        }

        val quadraSalva = quadraRepository.save(novaQuadra)

        return QuadraDetalhadaResponse(
            idQuadra = quadraSalva.idQuadra!!,
            userId = quadraSalva.userId,
            nome = quadraSalva.nome,
            descricao = quadraSalva.descricao,
            endereco = quadraSalva.endereco,
            latitude = quadraSalva.latitude,
            longitude = quadraSalva.longitude,
            bairroRegiao = quadraSalva.bairroRegiao,
            tipoPiso = quadraSalva.tipoPiso.name,
            cobertura = quadraSalva.cobertura.name,
            iluminacao = quadraSalva.iluminacao,
            acessibilidade = quadraSalva.acessibilidade,
            ativa = quadraSalva.ativa,
            fotos = quadraSalva.imagens.map { img ->
                QuadraFotoResponse(
                    idQuadraImage = img.idQuadraImage!!,
                    urlMinio = img.urlMinio,
                    dataCriacao = img.dataCriacao?.toString()
                )
            },
            dataCriacao = quadraSalva.dataCriacao?.toString()
        )
    }
}