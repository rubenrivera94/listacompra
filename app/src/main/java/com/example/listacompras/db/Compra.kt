package com.example.listacompras.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "nombre") var nombre: String?,
    @ColumnInfo(name = "comprado") var comprado: Boolean = false // Valor predeterminado
)