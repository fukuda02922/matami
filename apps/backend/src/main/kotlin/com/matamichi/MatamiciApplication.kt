package com.matamichi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MatamiciApplication

fun main(args: Array<String>) {
    runApplication<MatamiciApplication>(*args)
}
