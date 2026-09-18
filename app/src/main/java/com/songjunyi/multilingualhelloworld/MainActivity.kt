package com.songjunyi.multilingualhelloworld

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private var selectedLanguage = AppLanguage.CHINESE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedLanguage = savedInstanceState
            ?.getString(KEY_LANGUAGE)
            ?.let { saved -> AppLanguage.entries.firstOrNull { it.name == saved } }
            ?: AppLanguage.CHINESE

        window.statusBarColor = NAVY
        window.navigationBarColor = NAVY
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_LANGUAGE, selectedLanguage.name)
        super.onSaveInstanceState(outState)
    }

    private fun render() {
        val strings = localizedContext(selectedLanguage)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(28), dp(22), dp(28))
            background = verticalGradient(Color.rgb(238, 245, 255), Color.WHITE)
        }

        content.addView(header(strings))
        content.addView(space(26))
        content.addView(greetingCard(strings))
        content.addView(space(28))
        content.addView(sectionLabel(strings.getString(R.string.choose_language)))
        content.addView(space(12))
        content.addView(languageButtons())
        content.addView(space(24))
        content.addView(statusText(strings))
        content.addView(space(34))
        content.addView(footer(strings))

        setContentView(ScrollView(this).apply {
            isFillViewport = true
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        })
    }

    private fun header(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        addView(ImageView(this@MainActivity).apply {
            setImageResource(R.mipmap.ic_launcher)
            contentDescription = strings.getString(R.string.app_icon_description)
        }, LinearLayout.LayoutParams(dp(52), dp(52)))

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.app_name)
            setTextColor(NAVY)
            setTextSize(20f)
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dp(14), 0, 0, 0)
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }

    private fun greetingCard(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(dp(22), dp(36), dp(22), dp(36))
        background = roundedRectangle(WHITE, dp(24).toFloat(), dp(1), BORDER)
        elevation = dp(6).toFloat()

        if (selectedLanguage == AppLanguage.JAPANESE) {
            addView(ImageView(this@MainActivity).apply {
                setImageResource(R.drawable.flag_japan)
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = strings.getString(R.string.flag_description)
            }, LinearLayout.LayoutParams(dp(96), dp(64)).apply {
                bottomMargin = dp(18)
            })
        }

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.greeting)
            gravity = Gravity.CENTER
            setTextColor(NAVY)
            setTextSize(38f)
            setTypeface(typeface, Typeface.BOLD)
        }, matchWrap())

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.introduction)
            gravity = Gravity.CENTER
            setTextColor(SLATE)
            setTextSize(16f)
            setPadding(0, dp(12), 0, 0)
        }, matchWrap())

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.student_identity)
            gravity = Gravity.CENTER
            setTextColor(BLUE)
            setTextSize(15f)
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(20), 0, 0)
        }, matchWrap())
    }

    private fun languageButtons(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        AppLanguage.entries.forEachIndexed { index, language ->
            addView(languageButton(language), matchWrap().apply {
                if (index > 0) topMargin = dp(10)
            })
        }
    }

    private fun languageButton(language: AppLanguage): View = Button(this).apply {
        isAllCaps = false
        text = language.nativeName
        textSize = 17f
        minHeight = dp(54)
        gravity = Gravity.CENTER
        setTypeface(typeface, if (language == selectedLanguage) Typeface.BOLD else Typeface.NORMAL)
        setTextColor(if (language == selectedLanguage) WHITE else NAVY)
        background = roundedRectangle(
            if (language == selectedLanguage) BLUE else Color.rgb(245, 248, 253),
            dp(16).toFloat(),
            dp(1),
            if (language == selectedLanguage) BLUE else BORDER,
        )

        if (language == AppLanguage.JAPANESE) {
            val flag = getDrawable(R.drawable.flag_japan)?.apply {
                setBounds(0, 0, dp(36), dp(24))
            }
            setCompoundDrawablesRelative(flag, null, null, null)
            compoundDrawablePadding = dp(12)
        }

        setOnClickListener {
            if (selectedLanguage != language) {
                selectedLanguage = language
                render()
            }
        }
    }

    private fun statusText(strings: Context): View = TextView(this).apply {
        text = strings.getString(R.string.current_language)
        gravity = Gravity.CENTER
        setTextColor(SLATE)
        setTextSize(15f)
        setPadding(dp(12), dp(12), dp(12), dp(12))
        background = roundedRectangle(Color.rgb(247, 249, 252), dp(14).toFloat(), 0, Color.TRANSPARENT)
    }

    private fun footer(strings: Context): View = TextView(this).apply {
        text = strings.getString(R.string.footer)
        gravity = Gravity.CENTER
        setTextColor(Color.rgb(109, 123, 143))
        setTextSize(13f)
    }

    private fun sectionLabel(value: String): View = TextView(this).apply {
        text = value
        setTextColor(NAVY)
        setTextSize(19f)
        setTypeface(typeface, Typeface.BOLD)
    }

    private fun localizedContext(language: AppLanguage): Context {
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(language.locale())
        return createConfigurationContext(configuration)
    }

    private fun verticalGradient(top: Int, bottom: Int) = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(top, bottom),
    )

    private fun roundedRectangle(fill: Int, radius: Float, strokeWidth: Int, strokeColor: Int) =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(fill)
            cornerRadius = radius
            if (strokeWidth > 0) setStroke(strokeWidth, strokeColor)
        }

    private fun space(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val KEY_LANGUAGE = "selected_language"
        private val NAVY = Color.rgb(20, 39, 67)
        private val BLUE = Color.rgb(32, 99, 220)
        private val SLATE = Color.rgb(76, 93, 118)
        private val BORDER = Color.rgb(216, 226, 240)
        private val WHITE = Color.WHITE
    }
}

