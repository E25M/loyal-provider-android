package pet.loyal.provider.model

import android.text.style.ClickableSpan

data class Spannable(val start:Int, val end:Int, val clickableSpan:ClickableSpan, val replaceOldValue:String, val placeHolder: String)