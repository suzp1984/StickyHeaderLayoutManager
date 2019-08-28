package io.github.suzp1984.stickyheaderlayoutmanager.widget

import android.graphics.Rect
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HomeMadeLinearLayoutManager: RecyclerView.LayoutManager() {
    private val logTag = "LayoutManager"

    private val topViewRect: Rect = Rect(0, 0, 0, 0)
    private val bottomViewRect: Rect = Rect(0, 0, 0, 0)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (recycler == null || state == null) {
            return
        }

        detachAndScrapAttachedViews(recycler)
        topViewRect.set(0, 0, 0, 0)
        bottomViewRect.set(0, 0, 0, 0)

        val anchorAdapterPosition = getAnchorItemAdapterPosition(state)
        fillFromAnchor(anchorAdapterPosition, recycler, state)
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (recycler == null || state == null) {
            return 0
        }

        if (childCount == 0 || dy == 0) {
            return 0
        }

        // limit the max |dy| within the range of height of recyclerView
        val maxDy = if (dy < 0) {
            Math.max((1 - height + paddingBottom + paddingTop), dy)
        } else {
            Math.min(height - paddingBottom - paddingTop - 1, dy)
        }

        if (maxDy < 0 &&
            getFirstVisibleAdapterPosition() == 0 &&
            getLayoutTop() >= 0) {
            Log.d(logTag, "the first visible item was the first adapter view, so it can't scroll forward.")

            if (getLayoutTop() > 0) {
                offsetChildrens(-getLayoutTop())

                fillGap(Direction.END, recycler)
            }

            return 0
        } else if (maxDy > 0 &&
            getFirstVisibleAdapterPosition() + childCount == state.itemCount &&
            getLayoutBottom() <= height - paddingBottom) {
            Log.d(logTag, "the last visible item was the last adapter view, so it can't scroll backward.")

            if (getLayoutBottom() < height - paddingBottom && childCount < state.itemCount) {
                offsetChildrens(height - paddingBottom)
                fillGap(Direction.START, recycler)
            }

            return  0
        }

        offsetChildrens(-maxDy)

        val direction = if (maxDy < 0) {
            Direction.END
        } else {
            Direction.START
        }

        recycleChildrenOutOfBounds(direction, recycler)

        fillGap(direction.revert(), recycler)

        val correction = when (direction.revert()) {
            Direction.END -> {
                if (getLastVisibleAdapterPosition() + 1 == state.itemCount &&
                    bottomViewRect.bottom < height - paddingBottom &&
                    childCount < state.itemCount) {
                    height - paddingBottom - bottomViewRect.bottom
                } else {
                    0
                }
            }
            Direction.START -> {
                if (getFirstVisibleAdapterPosition() == 0 &&
                    topViewRect.top > paddingTop) {
                    paddingTop - topViewRect.top
                } else {
                    0
                }
            }
        }

        offsetChildrens(correction)

        if (correction != 0) {
            fillGap(direction, recycler)
        }

        return maxDy + correction
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return 0
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }

    private fun getAnchorItemAdapterPosition(state: RecyclerView.State): Int {
        return findFirstValidChildAdapterPosition(state)
    }

    private fun findFirstValidChildAdapterPosition(state: RecyclerView.State): Int {
        return if (childCount == 0) {
            0
        } else {
            (0 until childCount).firstOrNull {
                getChildAt(it)?.let { child ->
                    getPosition(child)
                }?.let { adapterPosition ->
                    adapterPosition >= 0 && adapterPosition < state.itemCount
                } ?: false
            }?.let {
                getChildAt(it)
            }?.let {
                getPosition(it)
            } ?: 0
        }
    }

    private fun fillFromAnchor(adapterPosition: Int,
                               recycler: RecyclerView.Recycler,
                               state: RecyclerView.State) {
        if (state.itemCount <= 0) {
            return
        }

        makeAndAddView(adapterPosition, Direction.END, recycler)

        fillBefore(adapterPosition - 1, recycler)

        fillAfter(adapterPosition + 1, recycler)
    }

    private fun fillBefore(adapterPosition: Int, recycler: RecyclerView.Recycler) {
        var position = adapterPosition

        while (canAddMoreViews(direction = Direction.START) && position >= 0) {
            makeAndAddView(position, Direction.START, recycler)

            position -= 1
        }
    }

    private fun fillAfter(adapterPosition: Int, recycler: RecyclerView.Recycler) {
        var position = adapterPosition

        while (canAddMoreViews(direction = Direction.END) && position < itemCount) {
            makeAndAddView(position, Direction.END, recycler)

            position += 1
        }
    }

    private fun makeAndAddView(adapterPosition: Int, direction: Direction, recycler: RecyclerView.Recycler): View {
        val child = recycler.getViewForPosition(adapterPosition)
        val isItemRemoved = (child.layoutParams as? RecyclerView.LayoutParams)?.isItemRemoved ?: true
        (child.layoutParams as? RecyclerView.LayoutParams)
        getItemViewType(child)

        if (!isItemRemoved) {
            addView(child, if (direction == Direction.END) -1 else 0)
        }

        setupChild(child, direction)

        return child
    }

    private fun setupChild(child: View, direction: Direction) {
        measureChild(child)
        layoutChild(child, direction)
    }

    private fun measureChild(child: View) {
        measureChildWithMargins(child, 0, 0)
    }

    private fun layoutChild(child: View, direction: Direction) {
        val layoutParams = child.layoutParams as? RecyclerView.LayoutParams
        val adapterPosition = layoutParams?.viewAdapterPosition

        val viewType = getItemViewType(child)

        val top = if (direction == Direction.END) {
            bottomViewRect.bottom
        } else {
            topViewRect.top - getDecoratedMeasuredHeight(child)
        }

        val bottom = top + getDecoratedMeasuredHeight(child)

        layoutDecoratedWithMargins(child, 0, top, width, bottom)

//        Log.e(logTag, "layoutChild: viewType = $viewType; adapterPosition = $adapterPosition; (l, t, r, b) = (${child.left}, ${child.top}, ${child.right}, ${child.bottom})")

        when (direction) {
            Direction.START -> {
                topViewRect.set(0, top, width, bottom)
            }
            Direction.END -> {
                bottomViewRect.set(0, top, width, bottom)
            }
        }
    }

    private fun canAddMoreViews(direction: Direction): Boolean {
        return when (direction) {
            Direction.END -> {
                val lastItemBottom = bottomViewRect.bottom
                lastItemBottom <= height
            }
            Direction.START -> {
                val firstItemTop = topViewRect.top
                firstItemTop >= 0
            }
        }
    }

    private fun getFirstVisibleAdapterPosition(): Int {
        return getChildAt(0)?.let {
            getPosition(it)
        } ?: 0
    }

    private fun getLastVisibleAdapterPosition(): Int {
        return getChildAt(childCount - 1)?.let {
            getPosition(it)
        } ?: 0
    }

    private fun getLayoutTop(): Int {
        return topViewRect.top
    }

    private fun getLayoutBottom(): Int {
        return bottomViewRect.bottom
    }

    private fun recycleChildrenOutOfBounds(direction: Direction, recycler: RecyclerView.Recycler) {
        when (direction) {
            Direction.END -> {
                (0 until childCount).reversed().firstOrNull {
                    val ret = (getChildAt(it)?.top ?: Int.MAX_VALUE) <= height
                    ret
                }?.also {
                    if (it + 1 < (childCount - 1)) {
                        (it + 1 until childCount).reversed().mapNotNull { index ->
                            getChildAt(index)
                        }.forEach { child ->
                            removeAndRecycleView(child, recycler)
                        }

                        getChildAt(childCount - 1)?.also { view ->
                            bottomViewRect.set(view.left, view.top, view.right, view.bottom)
                        }
                    }
                }
            }
            Direction.START -> {
                (0 until childCount).firstOrNull {
                    val ret = (getChildAt(it)?.bottom ?: Int.MIN_VALUE) >= 0
                    ret
                }?.also {
                    if (it - 1 >= 0) {
                        (0 until it).mapNotNull { index ->
                            getChildAt(index)
                        }.forEach { child ->
                            removeAndRecycleView(child, recycler)
                        }

                        getChildAt(0)?.also { view ->
                            topViewRect.set(view.left, view.top, view.right, view.bottom)
                        }
                    }
                }
            }
        }
    }

    private fun fillGap(direction: Direction, recycler: RecyclerView.Recycler) {
        if (!canAddMoreViews(direction)) {
            Log.d(logTag, "fillGap: can not add more views for direction = ${direction.name}")

            return
        }

        when (direction) {
            Direction.END -> {
                getChildAt(childCount - 1)?.let {
                    getPosition(it)
                }?.also {
                    fillAfter(it + 1, recycler)
                }
            }
            Direction.START -> {
                getChildAt(0)?.let {
                    getPosition(it)
                }?.also {
                    fillBefore(it - 1, recycler)
                }
            }
        }
    }

    private fun offsetChildrens(dy: Int): Int {

        topViewRect.offset(0, dy)
        bottomViewRect.offset(0, dy)

        offsetChildrenVertical(dy)

        return dy
    }

    enum class Direction {
        START,
        END
    }

    private fun Direction.revert(): Direction {
        return when (this) {
            Direction.START -> Direction.END
            Direction.END -> Direction.START
        }
    }
}