package com.example.simplescanit.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplescanit.R
import com.example.simplescanit.databinding.FragmentSecondBinding
import com.example.simplescanit.ui.main.adapter.HeaderAdapter
import com.example.simplescanit.ui.main.adapter.ScannedItemsAdapter
import com.example.simplescanit.ui.main.model.HeaderItemModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecondFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentSecondBinding? = null
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }
    private val scannedItemsAdapter: ScannedItemsAdapter by lazy {
        ScannedItemsAdapter()
    }

    private val headerAdapter: HeaderAdapter by lazy {
        HeaderAdapter()
    }

    private val concatAdapter: ConcatAdapter by lazy {
        ConcatAdapter().apply {
            addAdapter(0, headerAdapter)
            addAdapter(1, scannedItemsAdapter)
        }
    }

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        val root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            initScannedItemsRv(scannedItemsRv)
            registerBtn.setOnClickListener {
                mainViewModel.writeIntoFile(sharedPreferences)
            }
        }
        headerAdapter.submitList(listOf(HeaderItemModel(
            itemName = getString(R.string.hint_item_name),
            quantityName = getString(R.string.quantity_text)
        )))

        mainViewModel.scannedItemsLiveData.observe(viewLifecycleOwner) {
            val itemsJson = Gson().toJson(it)
            with(sharedPreferences.edit()){
                putString(EXTRA_SCANNED_ITEMS, itemsJson)
                apply()
            }
            scannedItemsAdapter.submitList(null)
            scannedItemsAdapter.submitList(it)
            scannedItemsAdapter.notifyDataSetChanged()
        }
    }

    private fun initScannedItemsRv(rv: RecyclerView) {
        rv.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapter = concatAdapter
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }
}