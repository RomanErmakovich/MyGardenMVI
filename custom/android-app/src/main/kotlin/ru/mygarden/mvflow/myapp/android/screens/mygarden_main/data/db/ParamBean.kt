package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "parambean")
data class ParamBean (
    @PrimaryKey
    @ColumnInfo(index = true)
    var name_: String,
    @ColumnInfo(name = "value_") var value_: String?
)
