package com.example.dacs3.controller


import com.example.dacs3.model.OrderReview

class OrderReviewController {
    fun getOrderReviews(userId: String): List<OrderReview> {
        // Giả lập dữ liệu cho ví dụ
        val reviews = listOf(
            OrderReview(1, "Sản phẩm A", 5, "2025-04-01", "Sản phẩm rất tốt!", "https://example.com/image_a.jpg"),
            OrderReview(2, "Sản phẩm B", 4, "2025-03-15", "Hài lòng với chất lượng.", "https://example.com/image_b.jpg")
        )

        // Kiểm tra dữ liệu
        println("Đánh giá trả về: $reviews")

        return reviews
    }
}