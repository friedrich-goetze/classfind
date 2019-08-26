package de.mf.classfind

import kotlinx.coroutines.*
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.system.measureTimeMillis


fun analyzeDirectory(
    path: Path
) {

    val mainJob = Job()
    val scope = CoroutineScope(mainJob + Dispatchers.Default)
    val outputContext = newSingleThreadContext("output")

    Files.walkFileTree(path, object : FileVisitor<Path> {
        override fun postVisitDirectory(
            dir: Path?,
            exc: IOException?
        ): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(
            file: Path?,
            attrs: BasicFileAttributes?
        ): FileVisitResult {
            if (
                file?.fileName?.toString()?.endsWith(
                    ".jar",
                    ignoreCase = true
                ) == true
            ) {
                scope.launch {
                    val res = analyzeJar(file)
                    val txt = buildString {
                        for (cd in res) {
                            append(formatClassdata(cd))
                        }
                    }
                    launch(outputContext) {
                        print(txt)
                    }
                }
            } else if (file?.fileName?.toString()?.endsWith(".class") == true) {
                scope.launch {
                    val res = analyzeClass(path, Files.newInputStream(path))
                    val txt = formatClassdata(res)
                    launch(outputContext) {
                        print(txt)
                    }
                }
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(
            file: Path?,
            exc: IOException?
        ): FileVisitResult {
            if (exc != null) throw exc
            System.err.println("Visting $file failed")
            return FileVisitResult.TERMINATE
        }

        override fun preVisitDirectory(
            dir: Path?,
            attrs: BasicFileAttributes?
        ): FileVisitResult = FileVisitResult.CONTINUE

    })

    measureTimeMillis {
        runBlocking {
            mainJob.complete()
            mainJob.join()
        }
    }.also {
        println("$it ms")
    }
}