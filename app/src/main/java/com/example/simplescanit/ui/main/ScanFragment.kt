package com.example.simplescanit.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root = binding.root

        mainViewModel.allItemsLiveData.observe(viewLifecycleOwner, {

        })

        mainViewModel.registeredLiveData.observe(viewLifecycleOwner){
            binding.infoGroup.isVisible = !it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            with(mainViewModel){
                searchWithTextBtn.setOnClickListener{
                    val barcode = barcodeEdT.text.toString()
                    if(barcode.isEmpty()) return@setOnClickListener
                    val item = findItemWithBarcode(barcode)
                    barcodeEdT.setText("")
                    if (isBarcodeAlreadyScanned(barcode)){
                        val quantity = scannedItems.find { it.barcode == barcode }?.quantity
                        if (quantity != null){
                            setQuantity(quantity + 1)
                        } else {
                            setQuantity(1)
                        }

                    } else {
                        scannedItems.add(item)
                        scannedItemsLiveData.postValue(scannedItems)
                        setQuantity(1)
                    }
                    counterLayout.scanQtyTv.text = quantityAtomic.get().toString()
                    showInfo(item)
                }
                barcodeEdT.addTextChangedListener(
                    beforeTextChanged = { text: CharSequence?, start: Int, count: Int, after: Int ->
                        val t = text?.toString() ?: ""
                        beforeBarcodeChangedText = t
                    }
                )
                barcodeEdT.addTextChangedListener {
                    if (it?.contains("\n") == true) {
                        val barcode = it.toString().replace("\n", "")
                        val isBarcodeTheSame = doesContainsTwoBarcode(
                            newText = barcode,
                            oldText = beforeBarcodeChangedText
                        )
                        if (isBarcodeTheSame.first) {
                            barcodeEdT.setText(isBarcodeTheSame.second)
                            barcodeEdT.setSelection(isBarcodeTheSame.second.length)
                            barcodeEdT.requestFocus()
                            counterLayout.qtyPlusBtn.callOnClick()
                        } else {
                            barcodeEdT.setText(barcode)
                            barcodeEdT.setSelection(barcode.length)
                            barcodeEdT.requestFocus()
                            searchWithTextBtn.callOnClick()
                        }
                    }
                }
                counterLayout.apply {
                    qtyMinusBtn.setOnClickListener {
                        scanQtyTv.text =
                            subtractQuantity()
                                .toString()

                    }
                    qtyPlusBtn.setOnClickListener {
                        scanQtyTv.text =
                            addQuantity().toString()
                    }
                }
            }
        }
    }


    private fun doesContainsTwoBarcode(newText: String, oldText: String): Pair<Boolean, String> {
        val addedText = newText.replaceFirst(oldText, "")
        return Pair(addedText.trim() == oldText.trim(), oldText)
    }

    private fun showInfo(data: DbItemModel){
        binding.apply {
            with(data){
                mainViewModel.registeredLiveData.value = false
                productNameValue.text = name
                productPriceValue.text = price
                counterLayout.mnacord.text = remCount
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