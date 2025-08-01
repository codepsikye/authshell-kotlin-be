package io.cpk.be

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@SpringBootApplication
@EnableSpringDataWebSupport
class KotlinbeApplication

fun main(args: Array<String>) {
    runApplication<KotlinbeApplication>(*args)
}
