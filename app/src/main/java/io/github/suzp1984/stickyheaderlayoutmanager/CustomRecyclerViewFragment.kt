package io.github.suzp1984.stickyheaderlayoutmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.suzp1984.stickyheaderlayoutmanager.adapter.AlphabetAdapter
import kotlinx.android.synthetic.main.fragment_custom_recycler.*

class CustomRecyclerViewFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        recycler_view.layoutManager = linearLayoutManager
        recycler_view.adapter = AlphabetAdapter()

        activity?.title = "custom recycler view"
    }
}