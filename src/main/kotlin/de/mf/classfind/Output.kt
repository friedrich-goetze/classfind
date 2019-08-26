package de.mf.classfind

import java.nio.file.Paths

private val workingDir = Paths.get(".").toAbsolutePath()

fun formatClassdata(cd: ClassData) = buildString {
    val lines = mutableListOf<String>()

    if (cd.error != null) {
        lines += "ERROR ${cd.error.message}"
    } else {
        lines += "CLASS ${cd.className}"
        if (cd.superClass.isNotEmpty()) {
            lines += "  SUPER ${cd.superClass}"
        }
        cd.interfaces.forEach { iface ->
            lines += "  IMPL $iface"
        }
    }

    for (l in lines) {
        if (cd.path.startsWith(workingDir)) {
            append(workingDir.relativize(cd.path))
        } else {
            append(cd.path)
        }
        append(": ")
        append(l)
        append('\n')
    }
}