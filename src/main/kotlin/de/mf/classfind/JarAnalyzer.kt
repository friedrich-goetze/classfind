package de.mf.classfind

import java.lang.Exception
import java.nio.file.Path
import java.util.zip.ZipFile

fun analyzeJar(path: Path): List<ClassData> = try {
    val r = mutableListOf<ClassData>()

    ZipFile(path.toFile()).use { zip ->
        for (zipEntry in zip.entries().asSequence()) {
            val zePath = path.resolve(zipEntry.name)
            if (!zePath.fileName.toString().endsWith(".class"))
                continue

            val cd = analyzeClass(zePath, zip.getInputStream(zipEntry))
            if (cd.className.startsWith(prefix))
                r += cd
        }
    }

    r
} catch (e: Exception) {
    listOf(ClassData.error(path, e))
}