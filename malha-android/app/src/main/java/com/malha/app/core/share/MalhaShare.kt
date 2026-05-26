package com.malha.app.core.share

import android.content.Context
import android.content.Intent
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.Post
import com.malha.app.domain.model.Project

object MalhaShare {
    private const val BASE_URL = "https://malha.app"

    fun sharePost(context: Context, post: Post) {
        val status = if (post.status == "finished") "finished" else "working on"
        val patternLine = post.patternName?.let { "\nPattern: $it" }.orEmpty()
        val text = """
            I $status a handmade project in Malha.
            $patternLine

            ${post.description}

            Made with Malha - plan, stitch, finish.
            ${postLink(post.id)}
        """.trimIndent()
        shareText(context, "Share Malha post", text)
    }

    fun shareProject(context: Context, project: Project) {
        val text = """
            I am working on "${project.name}" in Malha.
            Progress: ${project.progressPercent}%

            Made with Malha - a calm home for knitting and crochet.
            ${projectTemplateLink(project.id)}
        """.trimIndent()
        shareText(context, "Share Malha project", text)
    }

    fun shareMaterial(context: Context, material: Material) {
        val text = """
            Material idea from my Malha stash:
            ${material.name}
            Quantity: ${material.quantity} ${material.unit}

            Tracked in Malha.
            ${materialLink(material.id)}
        """.trimIndent()
        shareText(context, "Share Malha material", text)
    }

    fun sharePattern(context: Context, pattern: Pattern) {
        val text = """
            Pattern saved in Malha:
            ${pattern.title}
            Steps: ${pattern.allSteps.size}

            Made easier with Malha.
            ${patternLink(pattern.id)}
        """.trimIndent()
        shareText(context, "Share Malha pattern", text)
    }

    fun postLink(postId: String): String = "$BASE_URL/post/$postId"
    fun projectTemplateLink(projectId: String): String = "$BASE_URL/project-template/$projectId"
    fun patternLink(patternId: String): String = "$BASE_URL/pattern/$patternId"
    fun materialLink(materialId: String): String = "$BASE_URL/material/$materialId"

    private fun shareText(context: Context, title: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}
