package com.bangkit.capstone.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.databinding.ActivityScanBinding
import com.bangkit.capstone.uriToFile
import com.bangkit.capstone.response.ScanResponse
import com.bangkit.capstone.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            launcherIntentGallery.launch(intent)
        }

        binding.scanButton.setOnClickListener {
            upload()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@ScanActivity)
                getFile = myFile
                binding.preview.setImageURI(uri)
            }
        }
    }

    private fun upload() {
        if (getFile != null) {
            val file = getFile as File

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )

            val apiService = ApiConfig.getApiService("")
            val uploadImageRequest = apiService.predictImage(imageMultipart)

            uploadImageRequest.enqueue(object : Callback<ScanResponse> {
                override fun onResponse(
                    call: Call<ScanResponse>,
                    response: Response<ScanResponse>
                ) {
                    if (response.isSuccessful) {
                        val scanResponse = response.body()
                        val finishIntent =
                            Intent(this@ScanActivity, ResultActivity::class.java).apply {
                                if (scanResponse != null) {
                                    putExtra(
                                        "IMAGE_URL",
                                        "https://artsphere-3hlzeoqudq-as.a.run.app/predict/${scanResponse.label}"
                                    )
                                }
                                putExtra("LABEL", scanResponse?.label)
                            }
                        startActivity(finishIntent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ScanActivity,
                            "Failed to predict image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                    Toast.makeText(this@ScanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

}
