package com.example.simplescanit.ui.main

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplescanit.ui.main.model.DbItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern


class MainViewModel : ViewModel() {

    val allItemsLiveData = MutableLiveData<MutableList<DbItemModel>>()
    val allItems = mutableListOf<DbItemModel>()
    val scannedItemsLiveData = MutableLiveData<MutableList<DbItemModel>>()
    val scannedItems =  mutableListOf<DbItemModel>()


    fun loadScanInItemsFromFile() {
        viewModelScope.launch(Dispatchers.IO) {
//            val dir: File =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//            val letDirectory = File(dir, "ScanIt")
//            val inputAsString = FileInputStream(letDirectory).bufferedReader().use { it.readText() }
            val items: List<String> = ("P;00001;Միս;435.00;1.00;;;\n" +
                    "P;04211AM2021;Սեղան Մեծ;0.00;1.00;;;\n" +
                    "P;0022588929;Ստեղնաշար + Մկնիկ Apple Անլար;10000.00;1.00;;;\n" +
                    "P;0022588956;Մոդուլյատոր AUX //HK008\\\\!@#\$%^&*()|+_)(;3000.00;1.00;;;\n" +
                    "P;0022588966;Ականջակալ iPhone Original;9000.00;1.00;;;").split(
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val letDirectory = File(dir, "ScanIt")
        letDirectory.mkdirs()
        val file = File(letDirectory, "scanout.txt")
        file.appendText("record goes here")


    }

    fun findItemWithBarcode(barcode: String): DbItemModel {
       val existingItem =  allItems.findLast {
            it.barcode == barcode
        }
        val scannedItem = if (existingItem == null){
            val newItem = DbItemModel(barcode = barcode)
            allItems.add(newItem)
            allItemsLiveData.postValue(allItems)
            newItem
        } else {
            existingItem
        }
        scannedItems.add(scannedItem)
        scannedItemsLiveData.postValue(scannedItems)
        return scannedItem
    }
}