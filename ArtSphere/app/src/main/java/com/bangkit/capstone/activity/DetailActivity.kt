package com.bangkit.capstone.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bangkit.capstone.response.Feed
import com.bangkit.capstone.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = getParceableData()
        if (story != null) {
            setStoryDetail(story)
        } else {
            Log.e("DetailActivity", "Data story kosong")
            finish()
        }
    }

    private fun setStoryDetail(story: Feed) {
        Glide.with(this@DetailActivity).load(story.mediaUrl).into(binding.ivUserPhoto)
        binding.userName.text = story.id
        binding.userDescription.text = story.description
    }

    private fun getParceableData(): Feed? {
        if (Build.VERSION.SDK_INT >= 33) {
            return intent.getParcelableExtra(EXTRA_STORY, Feed::class.java)
        } else {
            @Suppress("DEPRECATED")
            return intent.getParcelableExtra(EXTRA_STORY)
        }
    }
}