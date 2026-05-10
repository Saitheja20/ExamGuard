package com.saitheja.examguard.ui

import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saitheja.examguard.focus.AppInfo
import com.saitheja.examguard.focus.WhitelistRecyclerAdapter

@Composable
fun FocusAppPickerView(
    context: Context,
    selectedPackages: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {
    val packages = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { context.packageManager.getLaunchIntentForPackage(it.packageName) != null }
        .sortedBy { context.packageManager.getApplicationLabel(it).toString() }
    val appItems = packages.map {
        AppInfo(
            packageName = it.packageName,
            appLabel = context.packageManager.getApplicationLabel(it).toString(),
            selected = selectedPackages.contains(it.packageName)
        )
    }

    AndroidView(
        factory = { ctx ->
            RecyclerView(ctx).apply {
                layoutManager = LinearLayoutManager(ctx)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        },
        update = {
            val adapter = (it.adapter as? WhitelistRecyclerAdapter) ?: WhitelistRecyclerAdapter(
                appItems
            ) { packageName, selected ->
                val mutable = selectedPackages.toMutableSet()
                if (selected) mutable.add(packageName) else mutable.remove(packageName)
                onSelectionChanged(mutable)
            }.also(it::setAdapter)
            adapter.update(appItems)
        }
    )
}
