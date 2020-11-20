import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

@Suppress("UNUSED_VARIABLE")
buildscript {
	val kotlinVersion by project.extra { "1.4.10" } // also update plugins section on version bump
	val jooqVersion by project.extra { "3.14.3" }
	val dbtype by project.extra { "org.postgresql:postgresql:9.4.1211.jre7" } // do not upgrade without upgrading flyway to 6.0 or later
	val junitVersion by project.extra { "5.7.0" }

	repositories {
		jcenter()
	}

	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath(dbtype)
		classpath("nu.studer:gradle-jooq-plugin:4.2")
		classpath("gradle.plugin.com.palantir.gradle.docker:gradle-docker:0.22.1")
	}
}

group = "com.example.app"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

plugins {
	id("org.springframework.boot") version "2.3.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"

	kotlin("jvm") version "1.4.10" // can't use variables here
	kotlin("plugin.spring") version "1.3.72"

	id("org.flywaydb.flyway") version "5.2.4"
	id("nu.studer.jooq") version "4.2"

}

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
	jcenter()
}

// redeclaration necessary because buildscript is run before anything else
val kotlinVersion: String by project.extra
val jooqVersion: String by project.extra
val dbtype: String by project.extra
val junitVersion: String by project.extra

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
	implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("javax.xml.bind:jaxb-api:2.3.1")


	implementation("org.jooq:jooq:$jooqVersion")
	implementation("org.jooq:jooq-meta:$jooqVersion")
	implementation("org.jooq:jooq-codegen:$jooqVersion")
	implementation("org.flywaydb:flyway-core:5.2.4")


	// In order to access the type @GraphQLDescription graphql-kotlin-schema-generator from the common library
	implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:7.0.0")
	implementation("com.graphql-java-kickstart:playground-spring-boot-starter:5.10.0")

	implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.5")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.5")
	implementation("org.reflections:reflections:0.9.11")

	implementation("com.expediagroup:graphql-kotlin-schema-generator:3.6.7")

	runtimeOnly(dbtype)
	jooqRuntime(dbtype)

//	testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")

	//Needed in order to auto configure the testDatabase
//	testRuntimeOnly("com.h2database:h2")
}

configure<DependencyManagementExtension> {
	imports {
		mavenBom(SpringBootPlugin.BOM_COORDINATES)
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.SR8")
	}
}

sourceSets["main"].java.srcDir("build/generated-src/")
sourceSets["test"].java.srcDir("build/generated-src/")

apply(from = "jooq.gradle") // gradle-jooq-plugin doesn't support Kotlin DSL

flyway {
	driver = "org.postgresql.Driver"
	url = "jdbc:postgresql://localhost/app"
	user = "postgres"
	password = ""
}

// Datajetmanager is the name of the task inside jooq.gradle
val generateAppJooqSchemaSource by tasks.existing {
	val flywayMigrate by tasks.existing
	dependsOn(flywayMigrate)
}

tasks.withType<KotlinCompile<KotlinJvmOptions>> {
	kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
	dependsOn(generateAppJooqSchemaSource)
}
