package com.example.simbachat.util

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.simbachat.R

class DiscoverFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_discover,
            container,
            false
        )

        val exploreTab =
            view.findViewById<TextView>(R.id.exploreTab)

        val saLinksTab =
            view.findViewById<TextView>(R.id.saLinksTab)

        val exploreContent =
            view.findViewById<View>(R.id.exploreContent)

        val saLinksContent =
            view.findViewById<View>(R.id.saLinksContent)

        /*
         * EXPLORE TAB
         */

        exploreTab.setOnClickListener {

            exploreContent.visibility = View.VISIBLE
            saLinksContent.visibility = View.GONE

            setActiveTab(
                exploreTab,
                saLinksTab
            )

            animateExploreContent(exploreContent)
        }

        /*
         * SA LINKS TAB
         */

        saLinksTab.setOnClickListener {

            exploreContent.visibility = View.GONE
            saLinksContent.visibility = View.VISIBLE

            setActiveTab(
                saLinksTab,
                exploreTab
            )

            animateSALinksContent(saLinksContent)
        }

        /*
         * Initial state
         */

        setActiveTab(
            exploreTab,
            saLinksTab
        )

        /*
         * Buttons
         */

        setupToggleButtons(view)

        /*
         * SA Links categories
         */

        setupSALinks(view)

        /*
         * Initial animation
         */

        animateExploreContent(exploreContent)

        return view
    }

    /*
     * TAB STYLING
     */

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

    /*
     * EXPLORE ANIMATION
     */

    private fun animateExploreContent(
        content: View
    ) {

        content.alpha = 0f
        content.translationX = 30f

        content.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(280)
            .start()
    }

    /*
     * SA LINKS ANIMATION
     */

    private fun animateSALinksContent(
        content: View
    ) {

        content.alpha = 0f
        content.translationY = 25f

        content.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(280)
            .start()
    }

    /*
     *  FOLLOW BUTTONS
     */

    private fun setupToggleButtons(
        view: View
    ) {

        setupToggle(
            view.findViewById(R.id.joinCommunity1),
            "Join",
            "Joined"
        )

        setupToggle(
            view.findViewById(R.id.joinCommunity2),
            "Join",
            "Joined"
        )

        setupToggle(
            view.findViewById(R.id.joinCommunity3),
            "Join",
            "Joined"
        )

        setupToggle(
            view.findViewById(R.id.followPerson1),
            "Follow",
            "Following"
        )

        setupToggle(
            view.findViewById(R.id.followPerson2),
            "Follow",
            "Following"
        )

        setupToggle(
            view.findViewById(R.id.followPerson3),
            "Follow",
            "Following"
        )
    }

    private fun setupToggle(
        button: TextView,
        normalText: String,
        selectedText: String
    ) {

        button.setOnClickListener {

            if (button.text.toString() == normalText) {

                button.text = selectedText

                button.setBackgroundColor(
                    Color.TRANSPARENT
                )

                button.setTextColor(
                    Color.rgb(201, 168, 76)
                )

            } else {

                button.text = normalText

                button.setBackgroundColor(
                    Color.rgb(201, 168, 76)
                )

                button.setTextColor(
                    Color.rgb(17, 17, 17)
                )
            }
        }
    }

    /*
     * SA LINKS
     */

    private fun setupSALinks(
        view: View
    ) {

        view.findViewById<View>(R.id.categoryGovernment)
            .setOnClickListener {
                openResource(
                    "Government Services",
                    "🏛️",
                    "Official South African government services and information.",
                    "https://www.gov.za/services"
                )
            }

        view.findViewById<View>(R.id.categoryEducation)
            .setOnClickListener {
                openResource(
                    "Education",
                    "🎓",
                    "Education, schools, training and higher education resources.",
                    "https://www.education.gov.za/"
                )
            }

        view.findViewById<View>(R.id.categoryJobs)
            .setOnClickListener {
                openResource(
                    "Jobs & Careers",
                    "💼",
                    "Employment opportunities, career information and government job resources.",
                    "https://www.gov.za/jobs"
                )
            }

        view.findViewById<View>(R.id.categoryNews)
            .setOnClickListener {
                openResource(
                    "News",
                    "📰",
                    "News and current affairs resources.",
                    "https://www.sabcnews.com/"
                )
            }

        view.findViewById<View>(R.id.categorySports)
            .setOnClickListener {
                openResource(
                    "Sports",
                    "⚽",
                    "South African sport and recreation information.",
                    "https://www.srsa.gov.za/"
                )
            }

        view.findViewById<View>(R.id.categoryWeather)
            .setOnClickListener {
                openResource(
                    "Weather",
                    "🌦️",
                    "Weather forecasts and warnings for South Africa.",
                    "https://www.weathersa.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryHealth)
            .setOnClickListener {
                openResource(
                    "Health",
                    "🏥",
                    "Public health information and services.",
                    "https://www.health.gov.za/"
                )
            }

        view.findViewById<View>(R.id.categoryShopping)
            .setOnClickListener {
                openResource(
                    "Shopping",
                    "🛒",
                    "Online shopping and retail resources.",
                    "https://www.takealot.com/"
                )
            }

        view.findViewById<View>(R.id.categoryBanking)
            .setOnClickListener {
                openResource(
                    "Banking & Finance",
                    "💳",
                    "Banking, financial information and economic resources.",
                    "https://www.resbank.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryInsurance)
            .setOnClickListener {
                openResource(
                    "Insurance",
                    "🛡️",
                    "Financial sector and insurance information.",
                    "https://www.fsca.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryEntertainment)
            .setOnClickListener {
                openResource(
                    "Entertainment",
                    "🎬",
                    "Film, television, arts and entertainment resources.",
                    "https://www.nfvf.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryTechnology)
            .setOnClickListener {
                openResource(
                    "Technology",
                    "💻",
                    "Technology and digital resources.",
                    "https://www.gov.za/"
                )
            }

        view.findViewById<View>(R.id.categoryScience)
            .setOnClickListener {
                openResource(
                    "Science",
                    "🔬",
                    "Science, technology and innovation resources.",
                    "https://www.dsi.gov.za/"
                )
            }

        view.findViewById<View>(R.id.categorySearch)
            .setOnClickListener {
                openResource(
                    "Search Engines",
                    "🔎",
                    "Search and information discovery tools.",
                    "https://www.google.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryEmail)
            .setOnClickListener {
                openResource(
                    "Email Services",
                    "✉️",
                    "Online email and communication services.",
                    "https://mail.google.com/"
                )
            }

        view.findViewById<View>(R.id.categoryMaps)
            .setOnClickListener {
                openResource(
                    "Maps & Navigation",
                    "🗺️",
                    "Maps, directions and navigation services.",
                    "https://www.google.com/maps"
                )
            }

        view.findViewById<View>(R.id.categoryClassifieds)
            .setOnClickListener {
                openResource(
                    "Classifieds",
                    "📋",
                    "Online classified listings and marketplaces.",
                    "https://www.gumtree.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryTravel)
            .setOnClickListener {
                openResource(
                    "Travel & Accommodation",
                    "✈️",
                    "Travel information, destinations and tourism resources.",
                    "https://www.southafrica.net/"
                )
            }

        view.findViewById<View>(R.id.categoryCrypto)
            .setOnClickListener {
                openResource(
                    "Cryptocurrency",
                    "₿",
                    "Financial and cryptocurrency information.",
                    "https://www.resbank.co.za/"
                )
            }

        view.findViewById<View>(R.id.categoryInformation)
            .setOnClickListener {
                openResource(
                    "Information & Knowledge",
                    "📚",
                    "General knowledge and information resources.",
                    "https://www.wikipedia.org/"
                )
            }

        view.findViewById<View>(R.id.categoryReligion)
            .setOnClickListener {
                openResource(
                    "Religion",
                    "⛪",
                    "Religious organisations and information resources.",
                    "https://www.sacbc.org.za/"
                )
            }

        view.findViewById<View>(R.id.categoryGaming)
            .setOnClickListener {
                openResource(
                    "Gaming",
                    "🎮",
                    "Gaming, consoles, accessories and games.",
                    "https://www.takealot.com/gaming"
                )
            }
    }

    /*
     * OPEN RESOURCE DETAIL
     */

    private fun openResource(
        category: String,
        icon: String,
        description: String,
        url: String
    ) {

        val intent = Intent(
            requireContext(),
            ResourceDetailActivity::class.java
        )

        intent.putExtra(
            "category",
            category
        )

        intent.putExtra(
            "icon",
            icon
        )

        intent.putExtra(
            "description",
            description
        )

        intent.putExtra(
            "url",
            url
        )

        startActivity(intent)
    }
}