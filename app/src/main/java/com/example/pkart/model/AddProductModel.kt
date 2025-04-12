package com.example.pkart.model

data class AddProductModel(
    var productName: String? = "",
    var productDescription: String? = "",
    var productCoverImg: String? = "",
    var productCategory: String? = "",
    var productId: String? = "",
    var productMrp: String? = "",
    var productSp: String? = "",

    var productImages: ArrayList<String> = arrayListOf()  // Default empty list
) {
    // Firestore requires a no-argument constructor
    constructor() : this("", "", "", "", "", "", "", arrayListOf())
}
