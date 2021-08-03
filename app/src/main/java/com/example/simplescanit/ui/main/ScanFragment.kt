package com.example.simplescanit.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplescanit.databinding.FragmentScanBinding
import com.example.simplescanit.ui.main.model.DbItemModel

/**
 * A placeholder fragment containing a simple view.
 */
class ScanFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentScanBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java).apply {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root = binding.root



        mainViewModel.allItemsLiveData.observe(viewLifecycleOwner, {

        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            searchBtn.setOnClickListener{
                val item = mainViewModel.findItemWithBarcode(barcodeEditText.text.toString())
                showInfo(item)
            }
        }
    }

    private fun showInfo(data: DbItemModel){
        binding.apply {
            with(data){
                itemNameTv.text = name ?: "dfsf"
                itemPriceTv.text = price ?: "dsfasf"
            }
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
        fun newInstance(): ScanFragment {
            return ScanFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}