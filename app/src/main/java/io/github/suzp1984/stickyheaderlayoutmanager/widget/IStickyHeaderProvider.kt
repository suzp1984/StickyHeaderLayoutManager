package io.github.suzp1984.stickyheaderlayoutmanager.widget

import android.view.View

interface IStickyHeaderProvider {

    fun isStickyHeaderAdapterPosition(position: Int): Boolean

    fun hasStickyHeaderAboveAdapterPosition(position: Int): Boolean

    fun getStickyHeaderAdapterPositionAbove(position: Int): Int?

    fun renderStickyHeaderView(view: View, position: Int)
}