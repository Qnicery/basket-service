package org.example.basket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["org.example.basket.client"])
class BasketApplication

fun main(args: Array<String>) {
    runApplication<BasketApplication>(*args)
}
