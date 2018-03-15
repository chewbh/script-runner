package io.boonlogic.sql.scriptrunner

import org.apache.ibatis.jdbc.ScriptRunner
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.stereotype.Component
import java.io.File
import javax.sql.DataSource

@SpringBootApplication
class ScriptRunnerApp

fun main(args: Array<String>) {
    SpringApplicationBuilder(ScriptRunnerApp::class.java)
        .bannerMode(Banner.Mode.OFF).run(*args)
}

@Component
class runner(private val dataSource: DataSource): CommandLineRunner {

    override fun run(vararg args: String?) {

        if(args.size == 0) {
            println("""
                Usage: ./script-runner.jar [SQL-FILE]
                ---------------------------------------------------------------------------------------------------
                This uility run a given SQL file to completion. It accept 1 and only 1 argument (path to SQL file)

                ----------------------------
                | Connection configuration |
                ----------------------------
                Set the following in application.properties file that resides in the same directory as this app

                spring.datasource.url = jdbc:oracle:thin:@//[HOST][:PORT]/SERVICE
                spring.datasource.username = [USER]
                spring.datasource.password = [PASSWORD]
                ---------------------------------------------------------------------------------------------------
                """.trimIndent())
            return
        }

        args[0]?.let {
            val scriptfile = File(it)
            if(scriptfile.exists()) {
                val runner = ScriptRunner(dataSource.connection)
                runner.setAutoCommit(false)
                scriptfile.bufferedReader().use {
                    runner.runScript(scriptfile.bufferedReader())
                }
                runner.closeConnection()
            } else {
                println("Cannot process. Invalid file specified : $it")
            }
        }
    }
}
