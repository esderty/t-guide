package t.lab.guide.mapper

import t.lab.guide.dto.auth.TokenPairResponse
import t.lab.guide.security.TokenPair

fun TokenPair.toTokenPairResponse() =
    TokenPairResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
