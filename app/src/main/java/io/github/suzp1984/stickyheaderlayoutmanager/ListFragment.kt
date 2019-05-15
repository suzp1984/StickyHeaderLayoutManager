package io.github.suzp1984.stickyheaderlayoutmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.suzp1984.stickyheaderlayoutmanager.adapter.AlphabetAdapter
import io.github.suzp1984.stickyheaderlayoutmanager.widget.HomeMadeLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = HomeMadeLinearLayoutManager()
        recycler_view.adapter = AlphabetAdapter()
    }
}
