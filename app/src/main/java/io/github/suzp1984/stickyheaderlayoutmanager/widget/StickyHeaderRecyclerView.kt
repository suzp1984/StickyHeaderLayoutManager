package io.github.suzp1984.stickyheaderlayoutmanager.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.suzp1984.stickyheaderlayoutmanager.R
import io.github.suzp1984.stickyheaderlayoutmanager.adapter.AlphabetAdapter
import kotlin.math.max
import kotlin.math.min

class StickyHeaderRecyclerView @JvmOverloads constructor(
    context: Context,
    val attributeSet: AttributeSet? = null,
    val defautStyle: Int = 0): ViewGroup(context, attributeSet, defautStyle) {

    private val recyclerView: RecyclerView
    private val textView: TextView

    private var linearLayoutManager: LinearLayoutManager? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.sticky_header_recycler_view, this, true)

        recyclerView = findViewById(R.id.recycler)
        textView = findViewById(R.id.sticky_header)
        textView.textSize = context.resources.getDimension(R.dimen.view_header_size)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = linearLayoutManager ?: return {
                    Log.e("list", "linear layout manager is nil.")
                } ()

                val adapter = adapter as? AlphabetAdapter ?: return {
                    Log.e("list", "adaper is nil.")
                } ()

                val firstCompletelyVisibleHeaderAdapterPosition =
                    layoutManager.findFirstCompletelyVisibleItemPosition(AlphabetAdapter.HEADER_POSITION)

                val firstCompletelyVisibleHeaderView = firstCompletelyVisibleHeaderAdapterPosition?.let {
                    layoutManager.findViewByPosition(it)
                }

                if (firstCompletelyVisibleHeaderView == null) {
                    ViewCompat.offsetTopAndBottom(textView, -textView.top)
                } else if (firstCompletelyVisibleHeaderView.top < textView.height) {
                    ViewCompat.offsetTopAndBottom(textView, firstCompletelyVisibleHeaderView.top - textView.height - textView.top)
                } else {
                    ViewCompat.offsetTopAndBottom(textView, -textView.top)
                }

                (firstCompletelyVisibleHeaderAdapterPosition ?: layoutManager.findLastCompletelyVisibleItemPosition()).let {
                    adapter.previousHeaderPosition(it)
                }?.let {
                    adapter.getTitle(it)
                }?.also {
                    textView.text = it
                } ?: {
                    textView.text = adapter.getTitle(0)
                } ()

            }
        })

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        measureChildWithMargins(recyclerView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(textView, widthMeasureSpec, 0, heightMeasureSpec, 0)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val selfMeasuredWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(widthSize, max(recyclerView.measuredWidth, textView.measuredWidth))
            }
            MeasureSpec.UNSPECIFIED -> {
                max(recyclerView.measuredWidth, textView.measuredWidth)
            }
            else -> {
                max(recyclerView.measuredWidth, textView.measuredWidth)
            }
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val selfMeasuredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(heightSize, max(recyclerView.measuredHeight, textView.measuredHeight))
            }
            MeasureSpec.UNSPECIFIED -> {
                max(recyclerView.measuredHeight, textView.measuredHeight)
            }
            else -> {
                max(recyclerView.measuredHeight, textView.measuredHeight)
            }
        }

        setMeasuredDimension(selfMeasuredWidth, selfMeasuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val recyclerLeft = measuredWidth / 2 - recyclerView.measuredWidth / 2
        val recyclerTop = measuredHeight / 2 - recyclerView.measuredHeight / 2
        recyclerView.layout(recyclerLeft,
            recyclerTop,
            recyclerLeft + recyclerView.measuredWidth,
            recyclerTop + recyclerView.measuredHeight)

        val textLeft = measuredWidth / 2 - textView.measuredWidth / 2
        val textTop = t

        textView.layout(textLeft, textTop,
            textLeft + textView.measuredWidth, textTop + textView.measuredHeight)
    }

    var layoutManager: RecyclerView.LayoutManager?
        get() = recyclerView.layoutManager
        set(value) {
            if (value is LinearLayoutManager) {
                linearLayoutManager = value
            }

            recyclerView.layoutManager = value
        }

    var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
        get() = recyclerView.adapter
        set(value) {
            recyclerView.adapter = value
        }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams()
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams : ViewGroup.MarginLayoutParams {

        constructor() : super(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(source: ViewGroup.MarginLayoutParams) : super(source)

        constructor(source: LayoutParams) : super(source)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    }
}

private fun LinearLayoutManager.findFirstCompletelyVisibleItemPosition(viewType: Int): Int? {
    val start = findFirstCompletelyVisibleItemPosition()
    val end = findLastCompletelyVisibleItemPosition()

    return (start .. end).firstOrNull { adapterPosition ->
        findViewByPosition(adapterPosition)?.let {
            getItemViewType(it) == viewType
        } ?: false
    }
}

private fun LinearLayoutManager.findFirstVisibleItemPosition(viewType: Int): Int? {
    val start = findFirstVisibleItemPosition()
    val end = findLastVisibleItemPosition()

    return (start .. end).firstOrNull {
        findViewByPosition(it)?.let {
            getItemViewType(it) == viewType
        } ?: false
    }
}

