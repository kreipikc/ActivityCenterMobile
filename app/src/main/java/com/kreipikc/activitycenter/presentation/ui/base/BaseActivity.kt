package com.kreipikc.activitycenter.presentation.ui.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.navigation.NavigationView
import com.kreipikc.activitycenter.R

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: T
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    abstract fun getLayoutRes(): Int
    abstract fun setupContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())

        setupNavigationDrawer()
        setupContent()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.main)
        navigationView = findViewById(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.menuItemHome -> navigateToHome()
                R.id.menuItemStats -> navigateToStatistics()
                R.id.menuItemSettings -> navigateToSettings()
                R.id.menuItemAbout -> navigateToAbout()
                R.id.menuItemExit -> exitApp()
            }
            true
        }
    }

    // Общие методы навигации
    protected open fun navigateToHome() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    protected open fun navigateToStatistics() {
        Toast.makeText(this, "Statistics", Toast.LENGTH_SHORT).show()
    }

    protected open fun navigateToSettings() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    protected open fun navigateToAbout() {
        Toast.makeText(this, "About the app", Toast.LENGTH_SHORT).show()
    }

    protected open fun exitApp() {
        finishAffinity()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}