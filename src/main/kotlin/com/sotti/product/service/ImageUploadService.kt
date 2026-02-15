package com.sotti.product.service

import com.luciad.imageio.webp.WebPWriteParam
import com.sotti.product.configuration.R2Properties
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream

@Service
@ConditionalOnBean(S3Client::class)
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
        private const val WEBP_QUALITY = 0.8f
    }

    fun upload(
        file: MultipartFile,
        folder: String,
        width: Int? = null,
        height: Int? = null,
    ): String {
        validate(file)

        val originalImage =
            ImageIO.read(file.inputStream)
                ?: throw IllegalArgumentException("이미지를 읽을 수 없습니다.")

        val resizedImage =
            if (width != null && height != null) {
                resizeImage(originalImage, width, height)
            } else {
                toRgb(originalImage)
            }

        val webpBytes = convertToWebP(resizedImage)
        val key = "$folder/${UUID.randomUUID()}.webp"

        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(r2Properties.bucket)
                .key(key)
                .contentType("image/webp")
                .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(webpBytes))

        val baseUrl = r2Properties.publicBaseUrl.trimEnd('/')
        return "$baseUrl/$key"
    }

    private fun resizeImage(
        original: BufferedImage,
        targetWidth: Int,
        targetHeight: Int,
    ): BufferedImage {
        val origW = original.width
        val origH = original.height

        // 원본이 이미 목표 크기 이하이면 RGB 변환만
        if (origW <= targetWidth && origH <= targetHeight) {
            return toRgb(original)
        }

        // 비율 유지: targetWidth x targetHeight 안에 맞추기
        val scale = minOf(targetWidth.toDouble() / origW, targetHeight.toDouble() / origH)
        val newW = (origW * scale).toInt().coerceAtLeast(1)
        val newH = (origH * scale).toInt().coerceAtLeast(1)

        val resized = BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB)
        val g2d = resized.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, newW, newH)
        g2d.drawImage(original, 0, 0, newW, newH, null)
        g2d.dispose()
        return resized
    }

    private fun toRgb(image: BufferedImage): BufferedImage {
        if (image.type == BufferedImage.TYPE_INT_RGB) return image
        val rgb = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val g2d = rgb.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, image.width, image.height)
        g2d.drawImage(image, 0, 0, null)
        g2d.dispose()
        return rgb
    }

    private fun convertToWebP(image: BufferedImage): ByteArray {
        val output = ByteArrayOutputStream()
        val writer = ImageIO.getImageWritersByMIMEType("image/webp").next()
        val writeParam = WebPWriteParam(writer.locale)
        writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
        writeParam.compressionType = writeParam.compressionTypes[WebPWriteParam.LOSSY_COMPRESSION]
        writeParam.compressionQuality = WEBP_QUALITY

        MemoryCacheImageOutputStream(output).use { imageOutput ->
            writer.output = imageOutput
            writer.write(null, IIOImage(image, null, null), writeParam)
        }
        writer.dispose()

        return output.toByteArray()
    }

    private fun validate(file: MultipartFile) {
        require(!file.isEmpty) { "파일이 비어있습니다." }
        require(file.size <= MAX_FILE_SIZE) { "파일 크기는 5MB를 초과할 수 없습니다." }
        require(file.contentType in ALLOWED_CONTENT_TYPES) {
            "허용되지 않는 파일 형식입니다. JPEG, PNG, WebP, GIF만 가능합니다."
        }
    }
}
