package com.example.dacs3.controller

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.dacs3.model.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartController(private val context: Context, private val phone: String) {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    private var _totalPrice by mutableStateOf(0.0)
    val totalPrice: Double by this::_totalPrice

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    private val cartKey = "cart_items_$phone"

    init {
        loadCartItems()
    }
// hien thi cart
    private fun loadCartItems() {
        val json = sharedPreferences.getString(cartKey, null)
        if (json != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            val savedItems = gson.fromJson<List<CartItem>>(json, type) ?: emptyList()
            _cartItems.addAll(savedItems)
            updateTotalPrice()
            println("Loaded cart items for phone $phone: $_cartItems")
        } else {
            println("No cart items found for phone $phone")
        }
    }
// doc du lieu
    private fun saveCartItems() {
        val json = gson.toJson(cartItems)
        sharedPreferences.edit().putString(cartKey, json).apply()
        println("Saved cart items for phone $phone: $json")
    }

    fun addItem(item: CartItem) {
        val existingItem = _cartItems.find { it.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
            println("Updated existing item for phone $phone: $existingItem")
        } else {
            _cartItems.add(item)
            println("Added new item for phone $phone: $item")
        }
        updateTotalPrice()
        saveCartItems()
    }

    fun updateQuantity(item: CartItem, quantity: Int) {
        if (quantity > 0) {
            item.quantity = quantity
            println("Updated quantity for item ${item.id} to $quantity for phone $phone")
            updateTotalPrice()
            saveCartItems()
        }
    }

    fun removeItem(item: CartItem) {
        println("Removing item for phone $phone: $item")
        _cartItems.remove(item)
        updateTotalPrice()
        saveCartItems()
    }

    fun clearCart() {
        _cartItems.clear()
        updateTotalPrice()
        saveCartItems()
        println("Cleared cart for phone $phone")
    }

    private fun updateTotalPrice() {
        _totalPrice = _cartItems.sumOf { it.price * it.quantity }
        println("Updated total price for phone $phone: $_totalPrice")
    }
}