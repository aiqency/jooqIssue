package com.example.app

import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppApplication

fun main(args: Array<String>) {
	runApplication<AppApplication>(*args)
}

val reflect = commonReflections("com.example.app")

fun commonReflections(pkg: String) =
		ConfigurationBuilder().run {
			addUrls(ClasspathHelper.forPackage(pkg))
			filterInputsBy(
					FilterBuilder().include(FilterBuilder.prefix(pkg))
			)
			setScanners(
					SubTypesScanner(),
					TypeAnnotationsScanner(),
					MethodAnnotationsScanner()
			)
			Reflections(this)
		}
