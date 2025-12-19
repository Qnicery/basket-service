package org.example.basket.config

import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CustomErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String?, response: Response): Exception {
        if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
            return ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен истек или недействителен")
        }
        return ErrorDecoder.Default().decode(methodKey, response)
    }
}