package com.example.listacompras.db


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CompraDao {

    @Query("SELECT * FROM compras ORDER BY comprado ASC")
    fun getAll(): List<Compra>

    @Insert
    fun insert(compra: Compra): Long

    @Update
    fun update(compra: Compra)

    @Delete
    fun delete(compra: Compra)
}