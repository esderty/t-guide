package t.lab.guide

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GuideApplication

fun main(args: Array<String>) {
    runApplication<GuideApplication>(*args)
}
