package com.temquadra.temquadra_api.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class StorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}") private val bucketName: String,
    @Value("\${minio.endpoint}") private val endpoint: String
) {

    fun uploadFoto(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast(".", "jpg") ?: "jpg"
        val fileName = "${UUID.randomUUID()}.$extension"

        file.inputStream.use { inputStream ->
            minioClient.putObject(
                PutObjectArgs.builder()
                    .`bucket`(bucketName)
                    .`object`(fileName)
                    .stream(inputStream, file.size, -1)
                    .contentType(file.contentType ?: "image/jpeg")
                    .build()
            )
        }

        return "$endpoint/$bucketName/$fileName"
    }
}