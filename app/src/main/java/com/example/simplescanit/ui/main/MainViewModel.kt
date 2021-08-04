package com.example.simplescanit.ui.main

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplescanit.ui.main.model.DbItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern


class MainViewModel : ViewModel() {

    val allItemsLiveData = MutableLiveData<MutableList<DbItemModel>>()
    val allItems = mutableListOf<DbItemModel>()
    val scannedItemsLiveData = MutableLiveData<MutableList<DbItemModel>>()
    val scannedItems = mutableListOf<DbItemModel>()
    val quantityAtomic = AtomicInteger(0)
    var beforeBarcodeChangedText: String = ""
    var currentBarcode: String = ""

    fun loadScanInItemsFromFile() {
        viewModelScope.launch(Dispatchers.IO) {
            val dir: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val letDirectory = File(dir, "scanin.txt")
            val inputAsString = FileInputStream(letDirectory).bufferedReader().use { it.readText() }
            val items: List<String> = (inputAsString).split(
                Pattern.compile(
                    ";;;\n"
                )
            )
            items.forEach {
                val item = it.split(';')
                allItems.add(
                    DbItemModel(
                        p = item[0],
                        barcode = item[1],
                        name = item[2],
                        price = item[3],
                        remCount = item[4]
                    )
                )
            }
            allItemsLiveData.postValue(allItems)
        }
    }

    fun writeIntoFile() {
        val dir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, "scanout.txt")
        file.mkdir()
        var scanedItemsString = ""
        scannedItems.forEach {
            scanedItemsString += "P;${it.barcode};${it.quantity}\n"
        }
        file.appendText(scanedItemsString)
        scannedItems.clear()
        scannedItemsLiveData.postValue(scannedItems)
    }

    fun findItemWithBarcode(barcode: String): DbItemModel {
        currentBarcode = barcode
        val existingItem = allItems.findLast {
            it.barcode == barcode
        }
        val scannedItem = if (existingItem == null) {
            val newItem = DbItemModel(barcode = barcode, quantity = 0)
            allItems.add(newItem)
            allItemsLiveData.postValue(allItems)
            newItem
        } else {
            existingItem
        }
        return scannedItem
    }


    fun addQuantity(): Int {
        quantityAtomic.incrementAndGet()
        scannedItems.find { it.barcode == currentBarcode }?.quantity = quantityAtomic.get()
        scannedItemsLiveData.postValue(scannedItems)
        return quantityAtomic.get()
    }

    fun subtractQuantity(): Int {
        if (quantityAtomic.get() <= 0) {
            quantityAtomic.set(0)
        } else {
            quantityAtomic.decrementAndGet()
        }
        scannedItems.find { it.barcode == currentBarcode }?.quantity = quantityAtomic.get()
        scannedItemsLiveData.postValue(scannedItems)
        return quantityAtomic.get()
    }

    fun setQuantity(qty: Int?) {
        quantityAtomic.set(qty ?: 0)
    }

    fun isBarcodeAlreadyScanned(barcode: String): Boolean = scannedItems.find { it.barcode == barcode } != null
}