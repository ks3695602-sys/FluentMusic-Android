/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune

import android.app.Application
import leakcanary.AppWatcher
import leakcanary.LeakCanary

internal object LeakCanaryVariant {
  @JvmStatic
  fun initialize(@Suppress("UNUSED_PARAMETER") application: Application) {
    AppWatcher.config = AppWatcher.config.copy(enabled = false)
    LeakCanary.config = LeakCanary.config.copy(dumpHeap = false)
  }

  @JvmStatic
  fun setEnabled(
    @Suppress("UNUSED_PARAMETER") context: android.content.Context,
    @Suppress("UNUSED_PARAMETER") enabled: Boolean,
  ) {
    // Disabled in FluentMusic builds to prevent notification popups.
  }
}
