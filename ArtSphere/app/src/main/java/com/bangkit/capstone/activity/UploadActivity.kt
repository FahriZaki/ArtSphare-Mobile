package com.bangkit.capstone.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.capstone.databinding.ActivityUploadBinding
import com.bangkit.capstone.reduceFileImage
import com.bangkit.capstone.response.AddFeeds
import com.bangkit.capstone.retrofit.ApiConfig
import com.bangkit.capstone.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            launcherIntentGallery.launch(intent)
        }

        binding.uploadButton.setOnClickListener {
            upload()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.preview.setImageURI(uri)
            }
        }
    }

    private fun upload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val desc = binding.edAddDescription.text.toString()
            val description = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "media",
                file.name,
                requestImageFile
            )
            val token = getSharedPreferences("LoginSession", Context.MODE_PRIVATE).getString("token", "")

            if (token.isNullOrEmpty()) {
                Toast.makeText(
                    this@UploadActivity,
                    "Token not available",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (desc.isNullOrEmpty()) {
                Toast.makeText(
                    this@UploadActivity,
                    "Add a description first!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            showLoading(true)
            val call = ApiConfig.getApiService(token!!).upload(imageMultipart, description)
            call.enqueue(object : Callback<AddFeeds> {
                override fun onResponse(
                    call: Call<AddFeeds>,
                    response: Response<AddFeeds>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            Toast.makeText(
                                this@UploadActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@UploadActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } ?: run {
                            Toast.makeText(
                                this@UploadActivity,
                                "Failed to upload",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@UploadActivity,
                            "Failed to upload: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AddFeeds>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@UploadActivity, "Failed to upload: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(
                this@UploadActivity,
                "Please select a file first!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.uploadButton.isEnabled = !isLoading
        if (isLoading) {
            binding.uploadButton.alpha = 0.5f
            binding.addStoryProgressbar.visibility = View.VISIBLE
        } else {
            binding.uploadButton.alpha = 1f
            binding.addStoryProgressbar.visibility = View.GONE
        }
    }
}
