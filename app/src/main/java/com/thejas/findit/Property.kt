package com.thejas.findit

data class Property(
    val name: String,
    val address: String,
    val contactPerson: String,
    val contactNumber: String,
    val price: Double,
    val type: String,
    val ownerId: String // To associate the property with the owner
) {
    // Default constructor for Firebase
    constructor() : this("", "", "", "", 0.0, "", "")
}

