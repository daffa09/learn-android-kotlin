package com.dicoding.learn

import android.content.Context

object FileHelper {

    fun writeToFile(fileModel: FileModel, context: Context) {
        context.openFileOutput(fileModel.filename, Context.MODE_PRIVATE).use {
            it.write(fileModel.data?.toByteArray())
        }
    }

    fun readFromFile(context: Context, fileName: String): FileModel {
        val fileModel = FileModel()
        fileModel.filename = fileName
        fileModel.data  = context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.fold("") {some, text ->
                "$some$text\n"
            }
        }

        return fileModel
    }
}