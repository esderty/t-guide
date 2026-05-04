package t.lab.guide.service

import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Template
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t.lab.guide.enums.MediaType
import t.lab.guide.properties.S3Properties
import java.util.UUID

@Service
class S3Service(
    private val s3Template: S3Template,
    private val s3Properties: S3Properties,
) {
    fun uploadObject(file: MultipartFile, pointId: Long, type: MediaType): String {
        val ext =
            file.originalFilename
                ?.substringAfterLast('.', "")
                ?.lowercase()
                ?.takeIf { it.isNotBlank() }
                ?: "webp"

        val key = buildKey(pointId, type, ext)

        val objectMetadata =
            ObjectMetadata
                .builder()
                .contentType(file.contentType ?: "application/octet-stream")
                .contentLength(file.size)
                .cacheControl("public, max-age=31536000, immutable")
                .build()

        s3Template.upload(s3Properties.bucket, key, file.inputStream, objectMetadata)

        return key
    }

    fun deleteObject(key: String) {
        s3Template.deleteObject(s3Properties.bucket, key)
    }

    fun getS3Domain(): String = s3Properties.publicBaseUrl

    fun publicUrl(objectKey: String): String =
        "${s3Properties.publicBaseUrl}/$objectKey"

    private fun buildKey(pointId: Long, type: MediaType, ext: String): String {
        val folder =
            when (type) {
                MediaType.PHOTO -> "photos"
                MediaType.VIDEO -> "videos"
                MediaType.AUDIO -> "audios"
            }
        return "points/$pointId/$folder/${UUID.randomUUID()}.$ext"
    }
}
