package io.github.suzp1984.stickyheaderlayoutmanager.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderLayoutManager: RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}