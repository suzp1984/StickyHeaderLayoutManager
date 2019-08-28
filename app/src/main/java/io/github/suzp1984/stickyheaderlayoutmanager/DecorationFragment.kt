package io.github.suzp1984.stickyheaderlayoutmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.suzp1984.stickyheaderlayoutmanager.adapter.AlphabetAdapter
import io.github.suzp1984.stickyheaderlayoutmanager.adapter.AlphabetWithStickyHeaderAdapter
import io.github.suzp1984.stickyheaderlayoutmanager.widget.StickyHeaderDecoration
import kotlinx.android.synthetic.main.fragment_decoration.*

class DecorationFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_decoration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AlphabetWithStickyHeaderAdapter()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(requireContext())

        recycler_view.addItemDecoration(StickyHeaderDecoration(adapter))
    }
}