package pet.loyal.provider.util

import android.graphics.drawable.Drawable
import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.carbs.android.avatarimageview.library.AvatarImageView
import com.squareup.picasso.Picasso
import pet.loyal.provider.R

object AvatarImageViewBindingUtil {

    @JvmStatic
    @BindingAdapter("app:setImage")
    fun setImage(view: AvatarImageView, url: String?) {
        Picasso.get().load(url).placeholder(R.drawable.img_pet_sample).into(view)
    }

    @JvmStatic
    @BindingAdapter("app:setPetImage")
    fun setPetImage(view: AvatarImageView, url: String?) {
        Picasso.get().load(Constants.url_cloudinary_pet_profile + url).placeholder(R.drawable.img_pet_sample).into(view)
    }

    @JvmStatic
    @BindingAdapter("app:setFacilityImage")
    fun setFacilityImage(view: ImageView, url: String?) {
        Picasso.get().load(url).placeholder(R.drawable.img_pet_sample).into(view)
    }

    @JvmStatic
    @BindingAdapter("app:setDrawable")
    fun setDrawable(view: TextView, drawable: Drawable?) {
        view.background = drawable
    }

    @JvmStatic
    @BindingAdapter("app:setSrcDrawable")
    fun setSrcDrawable(view: ImageView, drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }
}