package io.github.suzp1984.stickyheaderlayoutmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        custom_layout_manager.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_customLayoutFragment)
        }

        custom_recycler_view_single_lane.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_stickyHeaderFragment)
        }


        custom_decoration.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_decorationFragment)
        }

        activity?.title = "Main"
    }

}