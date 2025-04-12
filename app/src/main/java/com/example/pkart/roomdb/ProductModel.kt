package com.example.pkart.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    val productId: String, // Non-null by default in Kotlin
    @ColumnInfo(name = "ProductName")
    val productName: String? = null,
    @ColumnInfo(name = "ProductImage")
    val productImage: String? = null,
    @ColumnInfo(name = "ProductSp")
    val productSp: String? = null
)

