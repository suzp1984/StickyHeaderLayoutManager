package io.github.suzp1984.stickyheaderlayoutmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.suzp1984.stickyheaderlayoutmanager.R

class AlphabetAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: Map<String, List<String>> = mapOf(
        "AA" to listOf("as", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "an"),
        "BB" to listOf("bs", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "bn"),
        "CC" to listOf("cs", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "cn"),
        "DD" to listOf("ds", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "dn"),
        "EE" to listOf("es", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "en"),
        "FF" to listOf("fs", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "fn")
    )

    private val flatedValues: List<String> by lazy {

        items.map {
            (it.value.reversed() + it.key).reversed()
        }.flatten()
    }

    private val headersPositions: List<Int> by lazy {
        val vc = items.keys.map {
            items[it]?.size ?: 0
        }

        vc.mapIndexed { index, i ->
            if (i == 0) {
                0
            } else {
                vc.subList(0, index).sum() + index
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_POSITION -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_text_item, parent, false)
                HeaderViewHolder(view)
            }
            CONTENT_POSITON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_text_item, parent, false)
                ContentViewHolder(view)
            }
            else -> {
                throw IllegalStateException("unknown viewType: $viewType")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.keys.map {
            items[it]?.size ?: 0
        }.sum() + items.keys.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderAt(position)) {
            HEADER_POSITION
        } else {
            CONTENT_POSITON
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                holder.bindTitle(flatedValues[position] + position)
            }
            is HeaderViewHolder -> {
                holder.bindTitle(flatedValues[position] + position)
            }
        }
    }

    private fun isHeaderAt(adapterPosition: Int): Boolean {
        return adapterPosition in headersPositions
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTitle(title: String) {
            itemView.findViewById<TextView>(R.id.simple_title)?.text = title
        }
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<TextView>(R.id.simple_title)?.apply {
                textSize = itemView.context.resources.getDimension(R.dimen.view_header_size)
                setTextColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            }
        }

        fun bindTitle(title: String) {
            itemView.findViewById<TextView>(R.id.simple_title)?.text = title
        }
    }

    companion object {
        const val HEADER_POSITION = 0
        const val CONTENT_POSITON = 1
    }
}