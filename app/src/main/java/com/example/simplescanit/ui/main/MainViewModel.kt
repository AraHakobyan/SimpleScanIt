package com.example.simplescanit.ui.main

import android.content.SharedPreferences
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplescanit.ui.main.model.DbItemModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.*
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
    val registeredLiveData = MutableLiveData<Boolean>().apply { value = true }

    fun loadScanInItemsFromFile(sharedPreferences: SharedPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val letDirectory = File(dir, "scanin.dat")
            val fileInputStream = FileInputStream(letDirectory)
            val bufferReader = fileInputStream.bufferedReader()
            val inputAsString = bufferReader.use {
                it.readText()
            }
            val items: List<String> = (inputAsString).split(
                Pattern.compile(
                    ";;;\r\n"
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
            try {
                bufferReader.close()
                fileInputStream.close()
            } catch (ex: Exception) {
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val scannedItemsCash = sharedPreferences.getString(EXTRA_SCANNED_ITEMS, null)
            val gson = Gson()
            val arrayTutorialType = object : TypeToken<Array<DbItemModel>>() {}.type

            if (scannedItemsCash != null) {
                val cashItems: Array<DbItemModel> =
                    gson.fromJson(scannedItemsCash, arrayTutorialType)
                scannedItems.clear()
                scannedItems.addAll(cashItems)
                scannedItemsLiveData.postValue(scannedItems)
            }
        }
    }

    fun writeIntoFile(sharedPreferences: SharedPreferences) {
        if (scannedItems.isNullOrEmpty()) return

        var scanedItemsString = ""
        scannedItems.forEach {
            scanedItemsString += "P;${it.barcode};${it.quantity}\r\n"
        }
        insertTextIntoGivenFile(
            "scanout_${Calendar.getInstance().timeInMillis}.dat",
            scanedItemsString
        )
        with(sharedPreferences.edit()) {
            putString(EXTRA_SCANNED_ITEMS, "")
            apply()
        }
        registeredLiveData.value = true
        scannedItems.clear()
        scannedItemsLiveData.postValue(scannedItems)
        setQuantity(0)
        currentBarcode = ""
    }

    private fun insertTextIntoGivenFile(sFileName: String?, sBody: String?) {
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!root.exists()) {
            root.mkdirs()
        }
        File(root, sFileName).printWriter().use { out -> out.println(sBody) }
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
        scannedItems.find { it.barcode == currentBarcode }?.quantity = quantityAtomic.get()
        scannedItemsLiveData.postValue(scannedItems)
    }

    fun isBarcodeAlreadyScanned(barcode: String): Boolean =
        scannedItems.find { it.barcode == barcode } != null
}

const val EXTRA_SCANNED_ITEMS = "EXTRA_SCANNED_ITEMS"