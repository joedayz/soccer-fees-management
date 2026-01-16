package com.soccerfees.ktor

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseFactory {
    fun createDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${env("DB_HOST", "localhost")}:${env("DB_PORT", "5432")}/${env("DB_NAME", "soccer_fees")}" 
            username = env("DB_USER", "postgres")
            password = env("DB_PASSWORD", "postgres")
            maximumPoolSize = env("DB_POOL", "8").toInt()
        }
        return HikariDataSource(config)
    }

    private fun env(key: String, defaultValue: String): String = System.getenv(key) ?: defaultValue
}
