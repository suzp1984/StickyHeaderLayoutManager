package io.github.suzp1984.stickyheaderlayoutmanager.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.suzp1984.stickyheaderlayoutmanager.R

class StickyHeaderDecoration(val stickyHeaderProvider: IStickyHeaderProvider): RecyclerView.ItemDecoration() {

    private var stickyHeaderView: View? = null

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val firstCompletelyVisibleView = findFirstCompletelyVisibleView(parent) ?: return {
            Log.e("StickyHeader", "can not found first completely visible view")
        } ()

        val firstCompletelyVisibleAdapterPosition = parent.getChildAdapterPosition(firstCompletelyVisibleView)


        val headerPosition = stickyHeaderProvider.getStickyHeaderAdapterPositionAbove(firstCompletelyVisibleAdapterPosition) ?: return {
            Log.e("StickyHeader", "getStickyHeaderAbove AdapterPosition $firstCompletelyVisibleAdapterPosition; return null")
        } ()


        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getStickyHeaderHeight(parent.context), View.MeasureSpec.EXACTLY)

        val header = getStickyHeaderView(parent)

        stickyHeaderProvider.renderStickyHeaderView(header, headerPosition)

        header.measure(widthMeasureSpec, heightMeasureSpec)
        header.layout(0, 0, header.measuredWidth, header.measuredHeight)

        val headerOffset = getHeaderOffset(parent, header.measuredHeight)

        c.save()

        c.translate(0f, headerOffset.toFloat())
        header.draw(c)

        c.restore()
    }


    private fun findFirstCompletelyVisibleView(parent: RecyclerView): View? {
        return (0 until parent.childCount).firstOrNull {
            parent.getChildAt(it)?.let {
                it.top >= 0
            } ?: false
        }?.let {
            parent.getChildAt(it)
        }
    }

    private fun getHeaderOffset(parent: RecyclerView, stickyHeaderHeight: Int): Int {
        return (0 until parent.childCount).firstOrNull {
            val view = parent.getChildAt(it)
            view.top < stickyHeaderHeight && view.bottom > stickyHeaderHeight
        }?.let {
            parent.getChildAt(it)
        }?.let {
            Pair(it, parent.getChildAdapterPosition(it))
        }?.let {
            if ( stickyHeaderProvider.isStickyHeaderAdapterPosition(it.second)) {
                it.first.top - stickyHeaderHeight
            } else {
                null
            }
        } ?: 0
    }

    private fun getStickyHeaderHeight(context: Context): Int {
        return context.resources.getDimensionPixelSize(R.dimen.sticky_header_height)
    }

    private fun getStickyHeaderView(parent: RecyclerView): View {
        if (stickyHeaderView == null) {
            stickyHeaderView = LayoutInflater.from(parent.context).inflate(R.layout.simple_text_item, parent, false)

            stickyHeaderView?.background = ColorDrawable(ResourcesCompat.getColor(parent.resources, R.color.sticky_header_bg, null))
        }


        return stickyHeaderView!!
    }
}