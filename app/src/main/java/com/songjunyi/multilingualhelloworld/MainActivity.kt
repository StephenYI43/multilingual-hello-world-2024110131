package com.songjunyi.multilingualhelloworld

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var selectedLanguage: AppLanguage
    private var followsSystem = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLanguagePreference()
        configureSystemBars()
        updateLauncherIcon(selectedLanguage)
        render()
    }

    override fun onResume() {
        super.onResume()
        if (followsSystem) {
            val systemLanguage = AppLanguage.fromSystemLocale(currentSystemLocale())
            if (systemLanguage != selectedLanguage) {
                selectedLanguage = systemLanguage
                updateLauncherIcon(selectedLanguage)
                render()
            }
        }
    }

    private fun loadLanguagePreference() {
        val saved = preferences().getString(KEY_MANUAL_LANGUAGE, null)
        followsSystem = saved == null
        selectedLanguage = saved
            ?.let { value -> AppLanguage.entries.firstOrNull { it.name == value } }
            ?: AppLanguage.fromSystemLocale(currentSystemLocale())
    }

    private fun selectLanguage(language: AppLanguage) {
        selectedLanguage = language
        followsSystem = false
        preferences().edit().putString(KEY_MANUAL_LANGUAGE, language.name).apply()
        updateLauncherIcon(language)
        render()
    }

    private fun followSystemLanguage() {
        preferences().edit().remove(KEY_MANUAL_LANGUAGE).apply()
        followsSystem = true
        selectedLanguage = AppLanguage.fromSystemLocale(currentSystemLocale())
        updateLauncherIcon(selectedLanguage)
        render()
    }

    private fun render() {
        val strings = localizedContext(selectedLanguage)
        val root = FrameLayout(this).apply {
            background = gradient(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(3, 10, 22), Color.rgb(7, 25, 42), Color.rgb(3, 12, 26)),
                0f,
            )
        }

        root.addView(ambientOrb(CYAN, 0.16f), FrameLayout.LayoutParams(dp(260), dp(260)).apply {
            gravity = Gravity.TOP or Gravity.END
            topMargin = -dp(95)
            marginEnd = -dp(75)
        })
        root.addView(ambientOrb(PURPLE, 0.12f), FrameLayout.LayoutParams(dp(220), dp(220)).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
            marginStart = -dp(110)
        })

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(34))
        }
        content.addView(header(strings))
        content.addView(space(24))
        content.addView(heroCard(strings))
        content.addView(space(28))
        content.addView(sectionTitle(strings.getString(R.string.language_hub), "03 / LIVE"))
        content.addView(space(14))
        AppLanguage.entries.forEach { language ->
            content.addView(languageOption(strings, language), matchWrap().apply { bottomMargin = dp(10) })
        }
        content.addView(followSystemOption(strings), matchWrap().apply { topMargin = dp(2) })
        content.addView(space(22))
        content.addView(systemStatusCard(strings))
        content.addView(space(28))
        content.addView(footer(strings))

        root.addView(ScrollView(this).apply {
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_NEVER
            addView(content, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        setContentView(root)
    }

    private fun header(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val icon = ImageView(this@MainActivity).apply {
            setImageResource(iconFor(selectedLanguage))
            contentDescription = strings.getString(R.string.app_icon_description)
            background = rounded(Color.argb(150, 13, 39, 58), dp(18).toFloat(), dp(1), Color.argb(150, 65, 220, 255))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        addView(icon, LinearLayout.LayoutParams(dp(58), dp(58)))
        pulse(icon)

        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
            addView(TextView(this@MainActivity).apply {
                text = strings.getString(R.string.app_name)
                setTextColor(WHITE)
                setTextSize(19f)
                setTypeface(typeface, Typeface.BOLD)
                letterSpacing = 0.03f
            })
            addView(TextView(this@MainActivity).apply {
                text = strings.getString(R.string.system_eyebrow)
                setTextColor(CYAN)
                setTextSize(10f)
                letterSpacing = 0.14f
                setPadding(0, dp(4), 0, 0)
            })
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        addView(TextView(this@MainActivity).apply {
            text = if (followsSystem) "AUTO" else "MANUAL"
            gravity = Gravity.CENTER
            setTextColor(if (followsSystem) CYAN else LIME)
            setTextSize(10f)
            setTypeface(typeface, Typeface.BOLD)
            letterSpacing = 0.1f
            setPadding(dp(12), dp(7), dp(12), dp(7))
            background = rounded(Color.argb(150, 9, 31, 46), dp(20).toFloat(), dp(1), if (followsSystem) CYAN else LIME)
        })
    }

    private fun heroCard(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(dp(22), dp(26), dp(22), dp(26))
        background = gradientCard()
        elevation = dp(10).toFloat()

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.online_status, strings.getString(R.string.connection_ready))
            setTextColor(LIME)
            setTextSize(11f)
            letterSpacing = 0.13f
        })

        val flag = ImageView(this@MainActivity).apply {
            setImageResource(flagFor(selectedLanguage))
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = strings.getString(flagDescriptionFor(selectedLanguage))
            background = rounded(Color.argb(120, 255, 255, 255), dp(12).toFloat(), dp(1), Color.argb(110, 255, 255, 255))
            clipToOutline = true
        }
        addView(flag, LinearLayout.LayoutParams(dp(90), dp(60)).apply {
            topMargin = dp(22)
            bottomMargin = dp(20)
        })
        floatAnimation(flag)

        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.greeting)
            gravity = Gravity.CENTER
            setTextColor(WHITE)
            setTextSize(38f)
            setTypeface(typeface, Typeface.BOLD)
        }, matchWrap())
        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.introduction)
            gravity = Gravity.CENTER
            setTextColor(TEXT_SECONDARY)
            setTextSize(15f)
            setPadding(0, dp(10), 0, 0)
        }, matchWrap())
        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.student_identity)
            gravity = Gravity.CENTER
            setTextColor(CYAN)
            setTextSize(14f)
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(18), 0, 0)
        }, matchWrap())
        addView(techMetrics(strings), matchWrap().apply { topMargin = dp(24) })
    }

    private fun techMetrics(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        weightSum = 3f
        addView(metric("LANG", selectedLanguage.localeTag.uppercase()), weighted())
        addView(metric("MODE", if (followsSystem) strings.getString(R.string.auto_short) else strings.getString(R.string.manual_short)), weighted())
        addView(metric("SDK", "API 35"), weighted())
    }

    private fun metric(label: String, value: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(dp(5), dp(10), dp(5), dp(10))
        background = rounded(Color.argb(115, 7, 24, 39), dp(12).toFloat(), dp(1), Color.argb(70, 74, 204, 255))
        addView(TextView(this@MainActivity).apply {
            text = label
            setTextColor(Color.rgb(91, 129, 155))
            setTextSize(9f)
            letterSpacing = 0.12f
            gravity = Gravity.CENTER
        })
        addView(TextView(this@MainActivity).apply {
            text = value
            setTextColor(WHITE)
            setTextSize(12f)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, dp(4), 0, 0)
        })
    }

    private fun languageOption(strings: Context, language: AppLanguage): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true
        setPadding(dp(16), dp(13), dp(16), dp(13))
        val active = language == selectedLanguage
        background = rounded(
            if (active) Color.argb(225, 13, 50, 69) else Color.argb(165, 8, 26, 41),
            dp(18).toFloat(),
            dp(if (active) 2 else 1),
            if (active) CYAN else Color.argb(90, 76, 122, 149),
        )

        addView(ImageView(this@MainActivity).apply {
            setImageResource(flagFor(language))
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
            background = rounded(Color.DKGRAY, dp(8).toFloat(), 0, Color.TRANSPARENT)
            contentDescription = strings.getString(flagDescriptionFor(language))
        }, LinearLayout.LayoutParams(dp(48), dp(32)))

        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
            addView(TextView(this@MainActivity).apply {
                text = language.nativeName
                setTextColor(WHITE)
                setTextSize(17f)
                setTypeface(typeface, Typeface.BOLD)
            })
            addView(TextView(this@MainActivity).apply {
                text = strings.getString(languageNameFor(language))
                setTextColor(TEXT_SECONDARY)
                setTextSize(12f)
                setPadding(0, dp(2), 0, 0)
            })
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        addView(TextView(this@MainActivity).apply {
            text = if (active) "✓" else "›"
            gravity = Gravity.CENTER
            setTextColor(if (active) LIME else TEXT_SECONDARY)
            setTextSize(if (active) 17f else 25f)
            if (active) background = rounded(Color.argb(45, 99, 255, 189), dp(18).toFloat(), dp(1), LIME)
        }, LinearLayout.LayoutParams(dp(36), dp(36)))

        setOnClickListener { selectLanguage(language) }
    }

    private fun followSystemOption(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true
        setPadding(dp(16), dp(13), dp(16), dp(13))
        background = rounded(
            Color.argb(125, 31, 28, 69),
            dp(18).toFloat(),
            dp(if (followsSystem) 2 else 1),
            if (followsSystem) PURPLE else Color.argb(90, 107, 93, 160),
        )
        addView(TextView(this@MainActivity).apply {
            text = "◎"
            gravity = Gravity.CENTER
            setTextColor(PURPLE)
            setTextSize(25f)
        }, LinearLayout.LayoutParams(dp(48), dp(40)))
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
            addView(TextView(this@MainActivity).apply {
                text = strings.getString(R.string.follow_system)
                setTextColor(WHITE)
                setTextSize(16f)
                setTypeface(typeface, Typeface.BOLD)
            })
            addView(TextView(this@MainActivity).apply {
                text = strings.getString(R.string.follow_system_description)
                setTextColor(TEXT_SECONDARY)
                setTextSize(12f)
                setPadding(0, dp(2), 0, 0)
            })
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(TextView(this@MainActivity).apply {
            text = if (followsSystem) "ON" else "OFF"
            setTextColor(if (followsSystem) LIME else TEXT_SECONDARY)
            setTextSize(11f)
            setTypeface(typeface, Typeface.BOLD)
        })
        setOnClickListener { followSystemLanguage() }
    }

    private fun systemStatusCard(strings: Context): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(16), dp(18), dp(16))
        background = rounded(Color.argb(135, 7, 25, 39), dp(18).toFloat(), dp(1), Color.argb(90, 63, 205, 255))
        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.language_status, strings.getString(R.string.current_language))
            setTextColor(CYAN)
            setTextSize(14f)
            setTypeface(typeface, Typeface.BOLD)
        })
        addView(TextView(this@MainActivity).apply {
            text = strings.getString(R.string.icon_switch_hint)
            setTextColor(TEXT_SECONDARY)
            setTextSize(12f)
            setPadding(0, dp(7), 0, 0)
        })
    }

    private fun footer(strings: Context): View = TextView(this).apply {
        text = strings.getString(R.string.footer)
        gravity = Gravity.CENTER
        setTextColor(Color.rgb(84, 116, 139))
        setTextSize(11f)
        letterSpacing = 0.08f
    }

    private fun sectionTitle(title: String, code: String): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(TextView(this@MainActivity).apply {
            text = title
            setTextColor(WHITE)
            setTextSize(19f)
            setTypeface(typeface, Typeface.BOLD)
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        addView(TextView(this@MainActivity).apply {
            text = code
            setTextColor(CYAN)
            setTextSize(10f)
            letterSpacing = 0.1f
        })
    }

    private fun updateLauncherIcon(language: AppLanguage) {
        val aliases = mapOf(
            AppLanguage.CHINESE to "$packageName.launcher.Chinese",
            AppLanguage.ENGLISH to "$packageName.launcher.English",
            AppLanguage.FRENCH to "$packageName.launcher.French",
        )
        aliases.forEach { (candidate, aliasName) ->
            val state = if (candidate == language) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            packageManager.setComponentEnabledSetting(ComponentName(this, aliasName), state, PackageManager.DONT_KILL_APP)
        }
    }

    private fun localizedContext(language: AppLanguage): Context {
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(language.locale())
        return createConfigurationContext(configuration)
    }

    private fun currentSystemLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale
    }

    @Suppress("DEPRECATION")
    private fun configureSystemBars() {
        window.statusBarColor = Color.rgb(3, 10, 22)
        window.navigationBarColor = Color.rgb(3, 10, 22)
    }

    private fun preferences() = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private fun flagFor(language: AppLanguage) = when (language) {
        AppLanguage.CHINESE -> R.drawable.flag_china
        AppLanguage.ENGLISH -> R.drawable.flag_usa
        AppLanguage.FRENCH -> R.drawable.flag_france
    }

    private fun iconFor(language: AppLanguage) = when (language) {
        AppLanguage.CHINESE -> R.mipmap.ic_launcher_zh
        AppLanguage.ENGLISH -> R.mipmap.ic_launcher_en
        AppLanguage.FRENCH -> R.mipmap.ic_launcher_fr
    }

    private fun flagDescriptionFor(language: AppLanguage) = when (language) {
        AppLanguage.CHINESE -> R.string.flag_china_description
        AppLanguage.ENGLISH -> R.string.flag_usa_description
        AppLanguage.FRENCH -> R.string.flag_france_description
    }

    private fun languageNameFor(language: AppLanguage) = when (language) {
        AppLanguage.CHINESE -> R.string.language_chinese
        AppLanguage.ENGLISH -> R.string.language_english
        AppLanguage.FRENCH -> R.string.language_french
    }

    private fun gradientCard() = GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        intArrayOf(Color.argb(238, 12, 40, 59), Color.argb(230, 11, 28, 51), Color.argb(238, 26, 20, 56)),
    ).apply {
        cornerRadius = dp(26).toFloat()
        setStroke(dp(1), Color.argb(160, 58, 210, 255))
    }

    private fun ambientOrb(color: Int, alpha: Float) = View(this).apply {
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
        this.alpha = alpha
    }

    private fun gradient(orientation: GradientDrawable.Orientation, colors: IntArray, radius: Float) =
        GradientDrawable(orientation, colors).apply { cornerRadius = radius }

    private fun rounded(fill: Int, radius: Float, strokeWidth: Int, strokeColor: Int) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(fill)
        cornerRadius = radius
        if (strokeWidth > 0) setStroke(strokeWidth, strokeColor)
    }

    private fun pulse(view: View) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 0.68f, 1f).apply {
            duration = 1800
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun floatAnimation(view: View) {
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -dp(4).toFloat(), dp(4).toFloat()).apply {
            duration = 2100
            interpolator = AccelerateDecelerateInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun space(height: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(height)) }
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun weighted() = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
        marginStart = dp(3)
        marginEnd = dp(3)
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val PREFERENCES_NAME = "language_preferences"
        private const val KEY_MANUAL_LANGUAGE = "manual_language"
        private val WHITE = Color.rgb(241, 249, 255)
        private val CYAN = Color.rgb(73, 222, 255)
        private val LIME = Color.rgb(99, 255, 189)
        private val PURPLE = Color.rgb(165, 118, 255)
        private val TEXT_SECONDARY = Color.rgb(157, 183, 201)
    }
}
