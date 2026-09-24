package com.temquadra.temquadra_api.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import java.time.OffsetDateTime
import java.util.UUID

enum class RoleEnum { ADMIN, USUARIO, LIDER }
enum class TipoPisoEnum { CIMENTO, EMBORRACHADO, AREIA, GRAMADO, GRAMA_SINTETICA, OUTRO }
enum class CoberturaEnum { COBERTA, PARCIALMENTE_COBERTA, DESCOBERTA }

@Entity
@Table(name = "usuarios")
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_usuario")
    val idUsuario: UUID? = null,

    val name: String,

    @Column(unique = true)
    val email: String,

    val passwords: String?,

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val role: RoleEnum = RoleEnum.USUARIO,

    @Column(name = "data_criacao", insertable = false, updatable = false)
    val dataCriacao: OffsetDateTime? = null
)
@Entity
@Table(name = "quadras")
class Quadra(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_quadra")
    val idQuadra: UUID? = null,

    @Column(name = "user_id")
    val userId: UUID? = null,

    val nome: String,
    val descricao: String?,
    val endereco: String,
    val latitude: Double?,
    val longitude: Double?,

    @Column(name = "bairro_regiao")
    val bairroRegiao: String?,

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class) // <-- Mapeia o tipo_piso_enum do Postgres
    @Column(name = "tipo_piso")
    val tipoPiso: TipoPisoEnum = TipoPisoEnum.CIMENTO,

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class) // <-- Mapeia o cobertura_enum do Postgres
    val cobertura: CoberturaEnum = CoberturaEnum.DESCOBERTA,

    val iluminacao: Boolean = false,
    val acessibilidade: Boolean = false,
    val ativa: Boolean = true,

    @OneToMany(mappedBy = "quadra", cascade = [CascadeType.ALL], orphanRemoval = true)
    val imagens: MutableList<QuadraImage> = mutableListOf(),

    @Column(name = "data_criacao", insertable = false, updatable = false)
    val dataCriacao: OffsetDateTime? = null,

    @Column(name = "data_atualizacao", insertable = false, updatable = false)
    val dataAtualizacao: OffsetDateTime? = null
)

@Entity
@Table(name = "quadra_image")
class QuadraImage(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_quadra_image")
    val idQuadraImage: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quadra_id")
    val quadra: Quadra,

    @Column(name = "url_minio")
    val urlMinio: String,

    @Column(name = "data_criacao", insertable = false, updatable = false)
    val dataCriacao: OffsetDateTime? = null
)