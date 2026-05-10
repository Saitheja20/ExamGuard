package com.saitheja.examguard.report

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.saitheja.examguard.data.DailyStudyEntity
import java.io.File
import kotlin.math.max

class PdfReportGenerator {
    fun generateWeeklyReport(targetFile: File, entries: List<DailyStudyEntity>) {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }
        val barPaint = Paint().apply { color = Color.parseColor("#1565C0") }

        canvas.drawText("ExamGuard Weekly Focus Report", 40f, 50f, titlePaint)
        canvas.drawText("Today / Yesterday / This Week", 40f, 80f, bodyPaint)

        val sorted = entries.sortedBy { it.date }.takeLast(7)
        val maxMinutes = max(1, sorted.maxOfOrNull { it.whitelistedMinutes.toInt() } ?: 1)
        val chartBottom = 720f
        val barWidth = 55f
        sorted.forEachIndexed { index, entry ->
            val x = 50f + (index * 75)
            val barHeight = (entry.whitelistedMinutes.toFloat() / maxMinutes.toFloat()) * 380f
            canvas.drawRect(x, chartBottom - barHeight, x + barWidth, chartBottom, barPaint)
            canvas.drawText(entry.date.takeLast(5), x, chartBottom + 20f, bodyPaint)
        }

        doc.finishPage(page)
        targetFile.outputStream().use { out -> doc.writeTo(out) }
        doc.close()
    }
}
