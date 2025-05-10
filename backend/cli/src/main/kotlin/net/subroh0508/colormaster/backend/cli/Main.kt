package net.subroh0508.colormaster.backend.cli

import kotlinx.coroutines.runBlocking
import net.subroh0508.colormaster.backend.cli.commands.FetchIdolColorsCommand
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("ColorMaster CLI")

    if (args.isEmpty()) {
        printUsage()
        exitProcess(1)
    }

    when (args[0]) {
        "fetch-colors" -> runBlocking { FetchIdolColorsCommand().execute() }
        "help" -> printUsage()
        else -> {
            println("Unknown command: ${args[0]}")
            printUsage()
            exitProcess(1)
        }
    }
}

fun printUsage() {
    println("""
        Usage: colormaster-cli <command> [options]

        Commands:
          fetch-colors [--lang=<language>] [--output=<file>]
                       Fetch idol member colors from im@sparql
          help         Show this help message

        Options for fetch-colors:
          --lang=<language>     Language code (default: ja)
          --output=<file>       Output file path (default: stdout)
          --format=<format>     Output format: csv, json, or yaml (default: csv)
    """.trimIndent())
}
