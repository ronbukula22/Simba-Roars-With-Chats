package com.example.simbachat.util

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.simbachat.R
import org.xmlpull.v1.XmlPullParser
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

data class NewsArticle(
    val title: String,
    val date: String,
    val source: String,
    val link: String
)

class NewsFragment : Fragment() {

    private lateinit var newsContainer: LinearLayout
    private lateinit var loading: ProgressBar
    private lateinit var errorText: TextView

    private var currentRegion = "sa"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_news,
            container,
            false
        )

        newsContainer =
            view.findViewById(R.id.newsContainer)

        loading =
            view.findViewById(R.id.newsLoading)

        errorText =
            view.findViewById(R.id.newsError)

        val saTab =
            view.findViewById<TextView>(R.id.saNewsTab)

        val globalTab =
            view.findViewById<TextView>(R.id.globalNewsTab)

        val dismissBreaking =
            view.findViewById<TextView>(R.id.dismissBreaking)

        dismissBreaking.setOnClickListener {
            view.findViewById<View>(
                R.id.breakingBanner
            ).visibility = View.GONE
        }

        saTab.setOnClickListener {

            currentRegion = "sa"

            setActiveTab(
                saTab,
                globalTab
            )

            loadNews("South Africa")
        }

        globalTab.setOnClickListener {

            currentRegion = "global"

            setActiveTab(
                globalTab,
                saTab
            )

            loadNews("world")
        }

        setActiveTab(
            saTab,
            globalTab
        )

        loadNews("South Africa")

        return view
    }

    private fun setActiveTab(
        active: TextView,
        inactive: TextView
    ) {

        active.setBackgroundResource(
            R.drawable.discover_tab_active
        )

        active.setTextColor(
            Color.rgb(17, 17, 17)
        )

        inactive.setBackgroundResource(
            R.drawable.discover_tab_inactive
        )

        inactive.setTextColor(
            Color.rgb(175, 175, 175)
        )
    }

    private fun loadNews(region: String) {

        loading.visibility = View.VISIBLE

        errorText.visibility = View.GONE

        newsContainer.removeAllViews()

        thread {

            try {

                val articles =
                    fetchNews(region)

                activity?.runOnUiThread {

                    if (!isAdded) {
                        return@runOnUiThread
                    }

                    loading.visibility =
                        View.GONE

                    displayNews(articles)
                }

            } catch (e: Exception) {

                activity?.runOnUiThread {

                    if (!isAdded) {
                        return@runOnUiThread
                    }

                    loading.visibility =
                        View.GONE

                    errorText.text =
                        "Unable to load the latest news. Check your internet connection."

                    errorText.visibility =
                        View.VISIBLE
                }
            }
        }
    }

    private fun fetchNews(
        region: String
    ): List<NewsArticle> {

        val encodedRegion =
            java.net.URLEncoder.encode(
                region,
                "UTF-8"
            )

        val url = URL(
            "https://news.google.com/rss/search" +
                    "?q=$encodedRegion" +
                    "&hl=en-ZA" +
                    "&gl=ZA" +
                    "&ceid=ZA:en"
        )

        val connection =
            url.openConnection()

        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val parser =
            android.util.Xml.newPullParser()

        parser.setInput(
            connection.getInputStream(),
            "UTF-8"
        )

        val articles =
            mutableListOf<NewsArticle>()

        var eventType =
            parser.eventType

        var insideItem = false

        var title = ""
        var date = ""
        var source = ""
        var link = ""

        while (
            eventType != XmlPullParser.END_DOCUMENT &&
            articles.size < 10
        ) {

            if (
                eventType ==
                XmlPullParser.START_TAG
            ) {

                when (parser.name) {

                    "item" -> {

                        insideItem = true

                        title = ""
                        date = ""
                        source = ""
                        link = ""
                    }

                    "title" -> {

                        if (insideItem) {

                            title =
                                parser.nextText()
                        }
                    }

                    "pubDate" -> {

                        if (insideItem) {

                            date =
                                parser.nextText()
                        }
                    }

                    "source" -> {

                        if (insideItem) {

                            source =
                                parser.nextText()
                        }
                    }

                    "link" -> {

                        if (insideItem) {

                            link =
                                parser.nextText()
                        }
                    }
                }
            }

            if (
                eventType ==
                XmlPullParser.END_TAG &&
                parser.name == "item"
            ) {

                if (title.isNotBlank()) {

                    articles.add(
                        NewsArticle(
                            title = title,
                            date = formatDate(date),
                            source = source,
                            link = link
                        )
                    )
                }

                insideItem = false
            }

            eventType =
                parser.next()
        }

        return articles
    }

    private fun formatDate(
        dateString: String
    ): String {

        return try {

            val input =
                SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z",
                    Locale.ENGLISH
                )

            val date =
                input.parse(dateString)

            if (date == null) {
                "Today"
            } else {
                relativeTime(date)
            }

        } catch (e: Exception) {

            "Today"
        }
    }

    private fun relativeTime(
        date: Date
    ): String {

        val difference =
            System.currentTimeMillis() -
                    date.time

        val minutes =
            difference / 60000

        return when {

            minutes < 1 ->
                "Just now"

            minutes < 60 ->
                "${minutes}m ago"

            minutes < 1440 ->
                "${minutes / 60}h ago"

            else ->
                "${minutes / 1440}d ago"
        }
    }

    private fun displayNews(
        articles: List<NewsArticle>
    ) {

        newsContainer.removeAllViews()

        if (articles.isEmpty()) {

            errorText.text =
                "No news stories were found."

            errorText.visibility =
                View.VISIBLE

            return
        }

        articles.forEachIndexed { index, article ->

            val card =
                createNewsCard(
                    article,
                    index
                )

            newsContainer.addView(card)
        }
    }

    private fun createNewsCard(
        article: NewsArticle,
        position: Int
    ): View {

        val card =
            LinearLayout(requireContext())

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            15,
            15,
            15,
            15
        )

        val background =
            GradientDrawable()

        background.setColor(
            Color.rgb(17, 17, 17)
        )

        background.setStroke(
            1,
            Color.rgb(81, 69, 31)
        )

        background.cornerRadius =
            18f

        card.background =
            background

        card.isClickable = true
        card.isFocusable = true

        card.setOnClickListener {

            if (article.link.isNotBlank()) {

                try {

                    val intent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(article.link)
                        )

                    startActivity(intent)

                } catch (e: Exception) {

                    // No browser available.
                }
            }
        }

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.topMargin =
            if (position == 0) 18 else 10

        card.layoutParams =
            params

        val topRow =
            LinearLayout(requireContext())

        topRow.orientation =
            LinearLayout.HORIZONTAL

        val category =
            TextView(requireContext())

        category.text =
            getCategory(article.title)

        category.textSize =
            11f

        category.setTextColor(
            getCategoryColor(article.title)
        )

        category.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        topRow.addView(category)

        val time =
            TextView(requireContext())

        time.text =
            article.date

        time.textSize =
            11f

        time.setTextColor(
            Color.rgb(119, 119, 119)
        )

        time.gravity =
            Gravity.RIGHT

        val timeParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        time.layoutParams =
            timeParams

        topRow.addView(time)

        card.addView(topRow)

        val headline =
            TextView(requireContext())

        headline.text =
            article.title

        headline.textSize =
            15f

        headline.setTextColor(
            Color.WHITE
        )

        headline.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val headlineParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        headlineParams.topMargin =
            9

        headline.layoutParams =
            headlineParams

        card.addView(headline)

        if (article.source.isNotBlank()) {

            val source =
                TextView(requireContext())

            source.text =
                article.source

            source.textSize =
                11f

            source.setTextColor(
                Color.rgb(130, 130, 130)
            )

            val sourceParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            sourceParams.topMargin =
                7

            source.layoutParams =
                sourceParams

            card.addView(source)
        }

        return card
    }

    private fun getCategory(
        title: String
    ): String {

        val text =
            title.lowercase()

        return when {

            text.contains("sport") ||
                    text.contains("football") ||
                    text.contains("bafana") ->
                "SPORT"

            text.contains("business") ||
                    text.contains("economy") ||
                    text.contains("market") ||
                    text.contains("rand") ->
                "ECONOMY"

            text.contains("government") ||
                    text.contains("president") ||
                    text.contains("politic") ->
                "POLITICS"

            text.contains("technology") ||
                    text.contains("ai") ||
                    text.contains("tech") ->
                "TECH"

            else ->
                "NEWS"
        }
    }

    private fun getCategoryColor(
        title: String
    ): Int {

        return when (
            getCategory(title)
        ) {

            "SPORT" ->
                Color.rgb(230, 154, 69)

            "ECONOMY" ->
                Color.rgb(105, 185, 109)

            "POLITICS" ->
                Color.rgb(93, 169, 233)

            "TECH" ->
                Color.rgb(168, 117, 214)

            else ->
                Color.rgb(201, 168, 76)
        }
    }
}