package com.sotti.product.service

import com.sotti.product.configuration.R2Properties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Service
class ImageUploadService(
    private val s3Client: S3Client,
    private val r2Properties: R2Properties,
) {
    companion object {
        private val ALLOWED_CONTENT_TYPES =
            setOf(
                "image/jpeg",
                "image/png",
                "image/webp",
                "image/gif",
            )
        private const val MAX_FILE_SIZE = 5L * 1024 * 1024
    }

    fun upload(
        file: MultipartFile,
        folder: String,
    ): String {
        validate(file)

        val extension = extractExtension(file.originalFilename)
        val key = "$folder/${UUID.randomUUID()}.$extension"

        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(r2Properties.bucket)
                .key(key)
                .contentType(file.contentType)
                .build()

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.inputStream, file.size))

        val baseUrl = r2Properties.publicBaseUrl.trimEnd('/')
        return "$baseUrl/$key"
    }

    private fun validate(file: MultipartFile) {
        require(!file.isEmpty) { "파일이 비어있습니다." }
        require(file.size <= MAX_FILE_SIZE) { "파일 크기는 5MB를 초과할 수 없습니다." }
        require(file.contentType in ALLOWED_CONTENT_TYPES) {
            "허용되지 않는 파일 형식입니다. JPEG, PNG, WebP, GIF만 가능합니다."
        }
    }

    private fun extractExtension(filename: String?): String {
        val ext = filename?.substringAfterLast('.', "")?.lowercase() ?: ""
        require(ext.isNotBlank()) { "파일 확장자를 확인할 수 없습니다." }
        return ext
    }
}
