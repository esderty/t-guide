package t.lab.guide.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty

@Configuration
class JdbcNamingConfig {
    @Bean
    fun namingStrategy(): NamingStrategy = SnakeCaseNamingStrategy

    private object SnakeCaseNamingStrategy : NamingStrategy {
        private val camelBoundary = Regex("([a-z\\d])([A-Z])")

        override fun getTableName(type: Class<*>): String = type.simpleName.toSnakeCase()

        override fun getColumnName(property: RelationalPersistentProperty): String = property.name.toSnakeCase()

        private fun String.toSnakeCase(): String = replace(camelBoundary, "$1_$2").lowercase()
    }
}
