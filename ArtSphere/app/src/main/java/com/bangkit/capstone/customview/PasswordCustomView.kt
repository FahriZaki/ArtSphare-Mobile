package com.bangkit.capstone.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.capstone.R

class PasswordCustomView : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private var isPasswordVisible: Boolean = false
    private val showPassword: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.show
    )
    private val hidePassword: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.hide
    )

    var errorMessage = ""
    var errorCode = 0

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.length < 8) {
                    errorCode = 1
                    errorMessage = "password must be at least 8 characters"
                } else {
                    errorCode = 0
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val showIcon = s?.isNotEmpty() ?: false
                val endDrawable = if (showIcon) showPassword else null
                setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, endDrawable, null)
            }
        })

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= right - compoundPaddingEnd) {
                togglePasswordVisibility()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        val inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setInputType(inputType)

        val endDrawable = if (isPasswordVisible) hidePassword else showPassword
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, endDrawable, null)
    }
}