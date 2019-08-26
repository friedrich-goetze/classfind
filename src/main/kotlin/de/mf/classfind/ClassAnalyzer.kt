package de.mf.classfind

import org.objectweb.asm.*
import java.io.InputStream
import java.nio.file.Path

data class ClassData(
    val path: Path,
    val error: Exception?,
    val className: String,
    val superClass: String,
    val interfaces: List<String>
) {
    companion object {
        fun error(path: Path, error: Exception) = ClassData(
            path,
            error,
            "",
            "",
            emptyList()
        )
    }
}

fun analyzeClass(
    path: Path,
    bytes: InputStream
): ClassData = try {
    val cr = ClassReader(bytes)
    ClassData(
        path,
        null,
        cr.className,
        cr.superName,
        cr.interfaces.toList()
    )
} catch (e: Exception) {
    ClassData.error(path, e)
}
