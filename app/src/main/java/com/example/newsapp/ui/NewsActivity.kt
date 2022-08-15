package com.example.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsBinding


class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: ArticleViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(this, this)
        )[ArticleViewModel::class.java]


    }
}