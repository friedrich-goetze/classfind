package de.mf.classfind

import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

var prefix = ""
var full = false

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printHelp()
        exitProcess(0)
    }

    val path = Paths.get(args.last()).let {
        try {
            it.toAbsolutePath()
        } catch (e: Exception) {
            it
        }
    }
    if (!Files.exists(path)) {
        println("Couldn't open FILE: $path")
        printHelp()
        exitProcess(1)
    }

    println(path)

    prefix = args.indexOf("-p")
        .takeIf { it != -1 && it != args.lastIndex - 2 } // -2 ignores path
        ?.let { args[it + 1] }
        ?: ""

    full = args.contains("-f")

    when {
        Files.isDirectory(path) -> analyzeDirectory(path)
        path.fileName.toString().toLowerCase().endsWith(".jar") -> analyzeJar(
            path
        ).also {
            for (cd in it) {
                print(formatClassdata(cd))
            }
        }
        path.fileName.toString().toLowerCase().endsWith(".class") -> analyzeClass(
            path,
            Files.newInputStream(path)
        ).also {
            print(formatClassdata(it))
        }
    }
}

fun printHelp() {
    println(
        """
        Lists information about class and jar files.
        
        usage: classfind [FILE]
            -p [PREFIX]
                Only shows classes whose fully qualified name starts with the
                given prefix.
                
            -f
                Prints full information of classes (methods, members...)
        
        description:
            
            If FILE is a directory, classfind will walk through it recursively
            and will print information on all jar and classfiles found.

            If FILE is a jar file, classfind will print all information of all
            contained classes.
            
            If FILE is a class file, classfind will print all information of
            that class.
    """.trimIndent()
    )
}