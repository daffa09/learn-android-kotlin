package com.dicoding.learn.ui.customs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.dicoding.learn.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : TextInputEditText(context, attributeSet) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parentLayout = parent.parent as TextInputLayout

                if (s.toString().length < 8) {
                    parentLayout.helperText = context.getString(R.string.password_warning)
                    parentLayout.setHelperTextColor(
                        ContextCompat.getColorStateList(context, R.color.error)
                    )
                } else {
                    parentLayout.helperText = null
                }
            }
        })
    }
}