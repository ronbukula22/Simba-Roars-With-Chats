package com.example.simbachat.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.simbachat.R

class ResourceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resource_detail)

        val category =
            intent.getStringExtra("category") ?: "Resources"

        val icon =
            intent.getStringExtra("icon") ?: "🔗"

        val description =
            intent.getStringExtra("description")
                ?: "Official South African resources."

        val url =
            intent.getStringExtra("url")
                ?: "https://www.gov.za/"

        findViewById<TextView>(R.id.categoryTitle).text = category

        findViewById<TextView>(R.id.categoryIcon).text = icon

        findViewById<TextView>(R.id.categoryDescription).text =
            description

        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        setupResources(
            category,
            url
        )
    }

    private fun setupResources(
        category: String,
        defaultUrl: String
    ) {

        val container =
            findViewById<LinearLayout>(R.id.resourcesContainer)

        val resources =
            getResourcesForCategory(category, defaultUrl)

        findViewById<TextView>(R.id.resourceCount).text =
            "${resources.size} resources"

        resources.forEach { resource ->

            val card = createResourceCard(
                resource.first,
                resource.second
            )

            container.addView(card)
        }
    }

    private fun createResourceCard(
        name: String,
        url: String
    ): LinearLayout {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(
            14,
            14,
            14,
            14
        )

        val background =
            GradientDrawable()

        background.setColor(
            Color.rgb(20, 20, 20)
        )

        background.setStroke(
            1,
            Color.rgb(55, 48, 25)
        )

        background.cornerRadius = 18f

        card.background = background

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin = 10

        card.layoutParams = params

        // Link icon

        val icon = TextView(this)

        icon.text = "🔗"
        icon.textSize = 19f
        icon.gravity = Gravity.CENTER

        val iconBackground =
            GradientDrawable()

        iconBackground.setColor(
            Color.rgb(28, 26, 16)
        )

        iconBackground.cornerRadius = 14f

        icon.background = iconBackground

        val iconParams =
            LinearLayout.LayoutParams(
                44,
                44
            )

        icon.layoutParams = iconParams

        card.addView(icon)

        // Text container

        val textContainer =
            LinearLayout(this)

        textContainer.orientation =
            LinearLayout.VERTICAL

        val textParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        textParams.marginStart = 12

        textContainer.layoutParams =
            textParams

        // Name

        val nameText =
            TextView(this)

        nameText.text = name
        nameText.textSize = 14f
        nameText.setTextColor(Color.WHITE)
        nameText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        textContainer.addView(nameText)

        // URL

        val urlText =
            TextView(this)

        urlText.text =
            Uri.parse(url).host ?: url

        urlText.textSize = 11f
        urlText.setTextColor(
            Color.rgb(119, 119, 119)
        )

        val urlParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        urlParams.topMargin = 4

        urlText.layoutParams = urlParams

        textContainer.addView(urlText)

        card.addView(textContainer)

        // External link

        val arrow =
            TextView(this)

        arrow.text = "↗"
        arrow.textSize = 18f
        arrow.gravity = Gravity.CENTER
        arrow.setTextColor(Color.WHITE)

        val arrowBackground =
            GradientDrawable()

        arrowBackground.setColor(
            Color.rgb(139, 105, 20)
        )

        arrowBackground.cornerRadius = 14f

        arrow.background = arrowBackground

        arrow.layoutParams =
            LinearLayout.LayoutParams(
                40,
                40
            )

        card.addView(arrow)

        card.setOnClickListener {

            openWebsite(url)
        }

        return card
    }

    private fun getResourcesForCategory(
        category: String,
        defaultUrl: String
    ): List<Pair<String, String>> {

        return when (category) {

            "Government Services" -> listOf(
                "South African Government" to
                        "https://www.gov.za/services",

                "Department of Home Affairs" to
                        "https://www.dha.gov.za/",

                "South African Revenue Service" to
                        "https://www.sars.gov.za/",

                "Government Services" to
                        "https://www.gov.za/services"
            )

            "Education" -> listOf(
                "Department of Basic Education" to
                        "https://www.education.gov.za/",

                "Department of Higher Education" to
                        "https://www.dhet.gov.za/"
            )

            "Jobs & Careers" -> listOf(
                "Department of Employment and Labour" to
                        "https://www.labour.gov.za/",

                "ESSA / Employment Services" to
                        "https://essa.labour.gov.za/"
            )

            "News" -> listOf(
                "SABC News" to
                        "https://www.sabcnews.com/"
            )

            "Sports" -> listOf(
                "Sport and Recreation South Africa" to
                        "https://www.srsa.gov.za/"
            )

            "Weather" -> listOf(
                "South African Weather Service" to
                        "https://www.weathersa.co.za/"
            )

            "Health" -> listOf(
                "National Department of Health" to
                        "https://www.health.gov.za/"
            )

            "Shopping" -> listOf(
                "Takealot" to
                        "https://www.takealot.com/"
            )

            "Banking & Finance" -> listOf(
                "South African Reserve Bank" to
                        "https://www.resbank.co.za/"
            )

            "Insurance" -> listOf(
                "Financial Sector Conduct Authority" to
                        "https://www.fsca.co.za/"
            )

            "Entertainment" -> listOf(
                "National Film and Video Foundation" to
                        "https://www.nfvf.co.za/"
            )

            "Technology" -> listOf(
                "South African Government" to
                        "https://www.gov.za/"
            )

            "Science" -> listOf(
                "Department of Science and Innovation" to
                        "https://www.dsi.gov.za/"
            )

            "Search Engines" -> listOf(
                "Google South Africa" to
                        "https://www.google.co.za/"
            )

            "Email Services" -> listOf(
                "Gmail" to
                        "https://mail.google.com/"
            )

            "Maps & Navigation" -> listOf(
                "Google Maps" to
                        "https://www.google.com/maps"
            )

            "Classifieds" -> listOf(
                "Gumtree South Africa" to
                        "https://www.gumtree.co.za/"
            )

            "Travel & Accommodation" -> listOf(
                "South African Tourism" to
                        "https://www.southafrica.net/"
            )

            "Cryptocurrency" -> listOf(
                "South African Reserve Bank" to
                        "https://www.resbank.co.za/"
            )

            "Information & Knowledge" -> listOf(
                "Wikipedia" to
                        "https://www.wikipedia.org/"
            )

            "Religion" -> listOf(
                "Southern African Catholic Bishops' Conference" to
                        "https://www.sacbc.org.za/"
            )

            "Gaming" -> listOf(
                "Takealot Gaming" to
                        "https://www.takealot.com/gaming"
            )

            else -> listOf(
                category to defaultUrl
            )
        }
    }

    private fun openWebsite(url: String) {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )

        startActivity(intent)
    }
}