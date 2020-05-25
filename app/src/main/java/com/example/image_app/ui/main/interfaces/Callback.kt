package com.example.image_app.ui.main.interfaces

import com.example.image_app.databinding.ItemImageBinding
import com.example.image_app.ui.data.MImage

interface Callback {
    fun openSelectedImage(
        item: MImage,
        binding: ItemImageBinding
    )

}
