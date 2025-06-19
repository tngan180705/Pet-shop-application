package com.example.dacs3.controller

import com.example.dacs3.model.AboutMe

class AboutMeController {
    fun getAboutMe(): AboutMe {
        return AboutMe(
            address = "119 Phạm Như Xương, P.Hòa Khánh Nam, Q.Liên Chiểu, TP. Đà Nẵng",
            phone = "0123 456 789",
            facebookLink = "https://www.facebook.com/profile.php?id=100038108673575",
            purpose = "Chúng tôi cung cấp các sản phẩm chất lượng nhất với dịch vụ tốt nhất."
        )
    }
}