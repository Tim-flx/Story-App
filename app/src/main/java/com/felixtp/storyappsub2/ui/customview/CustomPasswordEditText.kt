package com.felixtp.storyappsub2.ui.customview

import android.content.Context
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.felixtp.storyappsub2.R

class CustomPasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        setup()
    }

    private fun setup() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility)
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val compoundDrawables = compoundDrawablesRelative
                val drawableEnd = compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }    private fun togglePasswordVisibility() {
        transformationMethod = if (transformationMethod == PasswordTransformationMethod.getInstance()) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        setSelection(text?.length ?: 0)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        error = if (text != null && text.length < 8) {
            context.getString(R.string.invalid_password_error)
        } else {
            null
        }
    }
}
