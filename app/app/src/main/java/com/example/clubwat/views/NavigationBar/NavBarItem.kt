package com.example.clubwat.views.NavigationBar

import com.example.clubwat.R

sealed class NavBarItem(
    var title: String,
    var icon: Int
) {
    data object Home :
        NavBarItem(
            "Home",
            R.drawable.baseline_home_24
        )

    data object ForYou :
        NavBarItem(
            "For You",
            R.drawable.baseline_auto_awesome_24
        )

    data object Search :
        NavBarItem(
            "Search",
            R.drawable.baseline_find_in_page_24
        )

    data object Profile :
        NavBarItem(
            "Profile",
            R.drawable.baseline_person_24
        )
}
