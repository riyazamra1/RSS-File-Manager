package com.riyaz.rssfilemanager

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.IOException

enum class CollisionMode { SKIP, REPLACE, KEEP_BOTH }

data class OperationResult(val success: Boolean, val message: String)

fun copyOrMoveDocument(
    context: Context,
    source: DocumentFile,
    destination: DocumentFile,
    collision: CollisionMode,
    move: Boolean
): OperationResult {
    val sourceName = source.name ?: "Unnamed"
    val existing = destination.findFile(sourceName)

    if (existing != null && collision == CollisionMode.SKIP) {
        return OperationResult(true, "Skipped " + sourceName)
    }
    if (existing != null && collision == CollisionMode.REPLACE && !existing.delete()) {
        return OperationResult(false, "Cannot replace " + sourceName)
    }

    val targetName = if (existing != null && collision == CollisionMode.KEEP_BOTH) {
        uniqueName(destination, sourceName, source.isDirectory)
    } else sourceName

    return runCatching {
        if (source.isDirectory) {
            val target = destination.createDirectory(targetName)
                ?: throw IOException("Cannot create folder " + targetName)
            source.listFiles().forEach { child ->
                val result = copyOrMoveDocument(context, child, target, collision, false)
                if (!result.success) throw IOException(result.message)
            }
            if (move && !source.delete()) throw IOException("Copied but could not remove " + sourceName)
        } else {
            val target = destination.createFile(
                source.type ?: "application/octet-stream",
                targetName
            ) ?: throw IOException("Cannot create " + targetName)

            context.contentResolver.openInputStream(source.uri)?.use { input ->
                context.contentResolver.openOutputStream(target.uri, "w")?.use { output ->
                    input.copyTo(output)
                } ?: throw IOException("Cannot open destination")
            } ?: throw IOException("Cannot open source")

            if (move && !source.delete()) throw IOException("Copied but could not remove " + sourceName)
        }
        OperationResult(true, "Completed " + sourceName)
    }.getOrElse { error ->
        OperationResult(false, "Failed " + sourceName + ": " + (error.message ?: "unknown error"))
    }
}

private fun uniqueName(parent: DocumentFile, original: String, directory: Boolean): String {
    val dot = if (directory) -1 else original.lastIndexOf('.')
    val base = if (dot <= 0) original else original.substring(0, dot)
    val extension = if (dot <= 0) "" else original.substring(dot)
    var index = 1
    var candidate = base + " (" + index + ")" + extension
    while (parent.findFile(candidate) != null) {
        index++
        candidate = base + " (" + index + ")" + extension
    }
    return candidate
}
