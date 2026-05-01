package t.lab.guide.dto.admin.point.command

import t.lab.guide.enums.MediaType

data class AdminUploadPointMediaCommand(
    val type: MediaType,
    val sortOrder: Int? = null,
)
