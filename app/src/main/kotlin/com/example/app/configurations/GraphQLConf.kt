package com.example.app.configurations

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class GraphQlQuery

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class GraphQlMutation

@Configuration
class CustomGraphqlConfiguration {

    private val log = LoggerFactory.getLogger(CustomGraphqlConfiguration::class.java)

    @Bean
    fun schemaConfig() = SchemaGeneratorConfig(
            // Only graphql types (dto) as to by scanned.
            // Query and mutation are found using reflections
            listOf("com.example.app")
    )

    @Bean
    fun schema(schemaConfig: SchemaGeneratorConfig, context: ApplicationContext): GraphQLSchema {
        val queries = context.getBeansWithAnnotation(GraphQlQuery::class.java)
        val mutations = context.getBeansWithAnnotation(GraphQlMutation::class.java)
        val schema = toSchema(
                queries = queries.map { TopLevelObject(it.value) },
                mutations = mutations.map { TopLevelObject(it.value) },
                config = schemaConfig,
        )
        log.info(SchemaPrinter(SchemaPrinter.Options.defaultOptions().includeScalarTypes(true)
                .includeSchemaDefinition(true)).print(schema))
        return schema
    }
}

