package com.bangkit.capstone.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.R
import com.bangkit.capstone.adapter.ListAdapter
import com.bangkit.capstone.databinding.ActivityMainBinding
import com.bangkit.capstone.response.Feed
import com.bangkit.capstone.viewmodel.MainViewModel

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        val token =
            getSharedPreferences("LoginSession", Context.MODE_PRIVATE).getString("token", "")
        if (!token.isNullOrEmpty()) {
            viewModel.getData(token!!)
        } else {
            Log.e(TAG, "Token kosong atau tidak valid")
        }

        viewModel.isLoading.observe(this) { isLoading -> showLoading(isLoading) }
        viewModel.feedsLiveData.observe(this) { feeds -> setFeeds(feeds) }

        // Set up the toolbar
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.title = getString(R.string.app_name)

        // Set up swipe-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!token.isNullOrEmpty()) {
                viewModel.getData(token)
            } else {
                Log.e(TAG, "Token kosong atau tidak valid")
            }
        }
    }

    private fun setupButton() {
        binding.scanBatik.setOnClickListener {
            val scanIntent = Intent(this@MainActivity, ScanActivity::class.java)
            startActivity(scanIntent)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.m_profil -> {
                    startActivity(Intent(this@MainActivity, ProfilActivity::class.java))
                    true
                }

                R.id.m_logout -> {
                    getSharedPreferences("LoginSession", Context.MODE_PRIVATE).edit().clear()
                        .apply()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finishAffinity()
                    true
                }

                else -> false
            }
        }

        binding.addStory.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun setFeeds(feeds: List<Feed>) {
        val listStory = ListAdapter(feeds)
        binding.rvStory.adapter = listStory

        listStory.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Feed) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, data)
                startActivity(intent)
            }
        })

        // Stop the refreshing animation once data is loaded
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
