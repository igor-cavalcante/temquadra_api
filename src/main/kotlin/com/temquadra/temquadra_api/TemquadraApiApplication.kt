package com.temquadra.temquadra_api

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TemquadraApiApplication

fun main(args: Array<String>) {
	// Carrega o arquivo .env da raiz do projeto e injeta no System.setProperty
	val dotenv = Dotenv.configure().ignoreIfMissing().load()
	dotenv.entries().forEach { entry ->
		System.setProperty(entry.key, entry.value)
	}

	runApplication<TemquadraApiApplication>(*args)
}
