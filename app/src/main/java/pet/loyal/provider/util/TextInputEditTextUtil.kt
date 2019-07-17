package pet.loyal.provider.util

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object TextInputEditTextUtil {

        @JvmStatic
        @BindingAdapter("app:errorText")
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.error = errorMessage
        }

        @JvmStatic
        @BindingAdapter("app:setDrawable")
        fun setDrawableEnd(view: TextInputEditText, drawable: Drawable?) {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
}