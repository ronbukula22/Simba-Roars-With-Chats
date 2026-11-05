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
import android.app.AlertDialog
import androidx.core.net.toUri


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

    private fun setupSALinks(view: View) {

        val resources = mapOf(
            R.id.categoryGovernment to (
                    "Government Services" to listOf(
                        "SASSA" to "https://www.sassa.gov.za",
                        "South African Government" to "https://www.gov.za",
                        "Department of Home Affairs" to "https://www.dha.gov.za",
                        "SARS" to "https://www.sars.gov.za",
                        "UIF uFiling" to "https://www.ufiling.co.za",
                        "NSFAS" to "https://www.nsfas.org.za",
                        "SAPS" to "https://www.saps.gov.za",
                        "Department of Labour" to "https://www.labour.gov.za",
                        "Department of Health" to "https://www.health.gov.za",
                        "GCIS" to "https://www.gcis.gov.za",
                        "Department of Higher Education" to "https://www.dhet.gov.za"
                    )
                    ),

            R.id.categoryEducation to (
                    "Education" to listOf(
                        "Department of Basic Education" to "https://www.education.gov.za",
                        "UNISA" to "https://www.unisa.ac.za",
                        "TVET Colleges SA" to "https://www.tvetcolleges.co.za",
                        "SA Study" to "https://www.sastudy.co.za",
                        "Skills Portal" to "https://www.skillsportal.co.za",
                        "Varsity Wise" to "https://www.varsitywise.com",
                        "Matric Results" to "https://www.education.gov.za"
                    )
                    ),

            R.id.categoryJobs to (
                    "Jobs & Careers" to listOf(
                        "Careers24" to "https://www.careers24.com",
                        "Indeed SA" to "https://za.indeed.com",
                        "PNet" to "https://www.pnet.co.za",
                        "Job Mail" to "https://www.jobmail.co.za",
                        "Careers Portal" to "https://www.careersportal.co.za",
                        "Career Junction" to "https://www.careerjunction.co.za",
                        "Gumtree Jobs" to "https://www.gumtree.co.za/jobs"
                    )
                    ),

            R.id.categoryNews to (
                    "News" to listOf(
                        "News24" to "https://www.news24.com",
                        "SABC News" to "https://www.sabcnews.com",
                        "eNCA" to "https://www.enca.com",
                        "IOL" to "https://www.iol.co.za",
                        "TimesLIVE" to "https://www.timeslive.co.za",
                        "Daily Maverick" to "https://www.dailymaverick.co.za",
                        "The Citizen" to "https://www.citizen.co.za",
                        "BusinessTech" to "https://businesstech.co.za",
                        "MyBroadband" to "https://mybroadband.co.za"
                    )
                    ),

            R.id.categorySports to (
                    "Sports" to listOf(
                        "SuperSport" to "https://supersport.com",
                        "Soccer Laduma" to "https://www.soccerladuma.co.za",
                        "LiveScore" to "https://www.livescore.com",
                        "ESPN" to "https://www.espn.com",
                        "Cricbuzz" to "https://www.cricbuzz.com"
                    )
                    ),

            R.id.categoryWeather to (
                    "Weather" to listOf(
                        "SA Weather Service" to "https://www.weathersa.co.za",
                        "AccuWeather" to "https://www.accuweather.com",
                        "Weather.com" to "https://weather.com",
                        "Windy" to "https://www.windy.com"
                    )
                    ),

            R.id.categoryHealth to (
                    "Health" to listOf(
                        "Department of Health" to "https://www.health.gov.za",
                        "WebMD" to "https://www.webmd.com",
                        "Mayo Clinic" to "https://www.mayoclinic.org",
                        "Health24" to "https://www.health24.com",
                        "WHO" to "https://www.who.int"
                    )
                    ),

            R.id.categoryShopping to (
                    "Shopping" to listOf(
                        "Takealot" to "https://www.takealot.com",
                        "Makro" to "https://www.makro.co.za",
                        "Game" to "https://www.game.co.za",
                        "Checkers" to "https://www.checkers.co.za",
                        "Shoprite" to "https://www.shoprite.co.za",
                        "Pick n Pay" to "https://www.pnp.co.za",
                        "Woolworths" to "https://www.woolworths.co.za",
                        "Mr Price" to "https://www.mrp.com",
                        "Ackermans" to "https://www.ackermans.co.za"
                    )
                    ),

            R.id.categoryBanking to (
                    "Banking & Finance" to listOf(
                        "FNB" to "https://www.fnb.co.za",
                        "Standard Bank" to "https://www.standardbank.co.za",
                        "Capitec" to "https://www.capitecbank.co.za",
                        "Nedbank" to "https://www.nedbank.co.za",
                        "Absa" to "https://www.absa.co.za",
                        "Old Mutual" to "https://www.oldmutual.co.za",
                        "Sanlam" to "https://www.sanlam.co.za"
                    )
                    ),

            R.id.categoryInsurance to (
                    "Insurance" to listOf(
                        "Hippo" to "https://www.hippo.co.za",
                        "Momentum" to "https://www.momentum.co.za",
                        "Discovery" to "https://www.discovery.co.za",
                        "OUTsurance" to "https://www.outsurance.co.za"
                    )
                    ),

            R.id.categoryEntertainment to (
                    "Entertainment" to listOf(
                        "YouTube" to "https://www.youtube.com",
                        "TikTok" to "https://www.tiktok.com",
                        "Facebook" to "https://www.facebook.com",
                        "Instagram" to "https://www.instagram.com",
                        "X (Twitter)" to "https://x.com",
                        "Spotify" to "https://www.spotify.com",
                        "Netflix" to "https://www.netflix.com"
                    )
                    ),

            R.id.categoryTechnology to (
                    "Technology" to listOf(
                        "MyBroadband" to "https://mybroadband.co.za",
                        "TechCentral" to "https://techcentral.co.za",
                        "CNET" to "https://www.cnet.com",
                        "TechCrunch" to "https://techcrunch.com"
                    )
                    ),

            R.id.categoryScience to (
                    "Science" to listOf(
                        "National Geographic" to "https://www.nationalgeographic.com",
                        "Scientific American" to "https://www.scientificamerican.com",
                        "ScienceDaily" to "https://www.sciencedaily.com"
                    )
                    ),

            R.id.categorySearch to (
                    "Search Engines" to listOf(
                        "Google" to "https://www.google.com",
                        "Bing" to "https://www.bing.com",
                        "Yahoo" to "https://www.yahoo.com"
                    )
                    ),

            R.id.categoryEmail to (
                    "Email Services" to listOf(
                        "Gmail" to "https://mail.google.com",
                        "Outlook" to "https://outlook.live.com",
                        "Yahoo Mail" to "https://mail.yahoo.com"
                    )
                    ),

            R.id.categoryMaps to (
                    "Maps & Navigation" to listOf(
                        "Google Maps" to "https://maps.google.com",
                        "Waze" to "https://www.waze.com",
                        "OpenStreetMap" to "https://www.openstreetmap.org"
                    )
                    ),

            R.id.categoryClassifieds to (
                    "Classifieds" to listOf(
                        "Gumtree SA" to "https://www.gumtree.co.za",
                        "Junk Mail" to "https://www.junkmail.co.za",
                        "AutoTrader" to "https://www.autotrader.co.za"
                    )
                    ),

            R.id.categoryTravel to (
                    "Travel & Accommodation" to listOf(
                        "Booking.com" to "https://www.booking.com",
                        "Busbud" to "https://www.busbud.com",
                        "Travelstart" to "https://www.travelstart.co.za",
                        "Airbnb" to "https://www.airbnb.com"
                    )
                    ),

            R.id.categoryCrypto to (
                    "Cryptocurrency" to listOf(
                        "Luno" to "https://www.luno.com",
                        "VALR" to "https://www.valr.com",
                        "Binance" to "https://www.binance.com"
                    )
                    ),

            R.id.categoryInformation to (
                    "Information & Knowledge" to listOf(
                        "Wikipedia" to "https://www.wikipedia.org",
                        "Britannica" to "https://www.britannica.com"
                    )
                    ),

            R.id.categoryReligion to (
                    "Religion" to listOf(
                        "Bible Gateway" to "https://www.biblegateway.com",
                        "YouVersion Bible" to "https://www.bible.com",
                        "Islamic Finder" to "https://www.islamicfinder.org"
                    )
                    ),

            R.id.categoryGaming to (
                    "Gaming" to listOf(
                        "CrazyGames" to "https://www.crazygames.com",
                        "Poki" to "https://poki.com",
                        "GamePix" to "https://www.gamepix.com",
                        "Y8 Games" to "https://www.y8.com",
                        "Miniclip" to "https://www.miniclip.com",
                        "Coolmath Games" to "https://www.coolmathgames.com",
                        "Friv" to "https://friv.com",
                        "Agame" to "https://www.agame.com",
                        "Addicting Games" to "https://www.addictinggames.com",
                        "Kongregate" to "https://www.kongregate.com",
                        "Armor Games" to "https://armorgames.com",
                        "Newgrounds" to "https://www.newgrounds.com"
                    )
                    )
        )

        resources.forEach { (categoryId, resource) ->

            val (categoryName, links) = resource

            view.findViewById<View>(categoryId)
                .setOnClickListener {
                    showResourceLinks(categoryName, links)
                }
        }
    }

    /*
     * DISPLAY CATEGORY WEBSITES
     */

    private fun showResourceLinks(
        category: String,
        links: List<Pair<String, String>>
    ) {

        val websiteNames = links.map { it.first }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(category)
            .setItems(websiteNames) { _, which ->

                val url = links[which].second

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    url.toUri()
                )

                startActivity(intent)
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
