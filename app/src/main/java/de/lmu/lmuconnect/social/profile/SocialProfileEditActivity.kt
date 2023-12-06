package de.lmu.lmuconnect.social.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.Constants
import de.lmu.lmuconnect.general.api.ApiClient
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.social.api.ProfileInfoGetResponse
import de.lmu.lmuconnect.social.api.ProfileInfoPatchRequest
import de.lmu.lmuconnect.social.data.ProfileInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ProfileFragment"

class SocialProfileEditActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var majorEditText: EditText
    private lateinit var degreeEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var schoolEditText: EditText
    private lateinit var pictureImageView: ShapeableImageView
    private lateinit var phoneEditText: EditText
    private lateinit var discordEditText: EditText
    private lateinit var githubEditText: EditText
    private lateinit var insEditText: EditText
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 200
    private val IMAGE_PICKGALLERY_REQUEST = 300
    private val IMAGE_PICKCAMERA_REQUEST = 400

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = ""

        sessionManager = SessionManager(this)

        // Initialize Views from layout activity_profile.xml
        nameEditText = findViewById(R.id.profile_name_edit)
        majorEditText = findViewById(R.id.profile_major_edit)
        degreeEditText = findViewById(R.id.profile_degree_edit)
        emailTextView = findViewById(R.id.tv_profile_email_content)
        schoolEditText = findViewById(R.id.profile_school_edit)
        phoneEditText = findViewById(R.id.et_profile_tel_content)
        pictureImageView = findViewById(R.id.profile_image_edit)
        discordEditText = findViewById(R.id.et_profile_discord_content)
        githubEditText = findViewById(R.id.et_profile_github_content)
        insEditText = findViewById(R.id.et_profile_ins_content)

        val schools = arrayListOf<CharSequence>(
            "LMU",
            "TUM"
        )
        val degree = arrayListOf<CharSequence>(
            "Bachelor",
            "Master",
            "PhD"
        )
        val checkedItem = 0

        // Initialize Permissions
        cameraPermission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        dataInitialize()

        pictureImageView.setOnClickListener{
            pictureDialog()
            println("Clicked Image Button")
        }

        // set click events if click EditText of school
        schoolEditText.setOnClickListener{
            android.app.AlertDialog.Builder(this@SocialProfileEditActivity)
                .setTitle("Select your School: ")
                .setNeutralButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("OK") { dialog, _ ->
                    val checked = (dialog as android.app.AlertDialog).listView.checkedItemPosition

                    schoolEditText.setText(schools[checked].toString())
                }
                .setSingleChoiceItems(schools.toTypedArray(), checkedItem, null)

                .show()
        }
        // set click events if click EditText of degree
        degreeEditText.setOnClickListener{
            android.app.AlertDialog.Builder(this@SocialProfileEditActivity)
                .setTitle("Select your Major: ")
                .setNeutralButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("OK") { dialog, _ ->
                    val checked = (dialog as android.app.AlertDialog).listView.checkedItemPosition

                    degreeEditText.setText(degree[checked].toString())
                }
                .setSingleChoiceItems(degree.toTypedArray(), checkedItem, null)

                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Init top app bar
        menuInflater.inflate(R.menu.profile_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Init icon in top app bar
        val edit = menu!!.findItem(R.id.profile_edit)
        val logout = menu.findItem(R.id.profile_logout)
        val finish = menu.findItem(R.id.profile_edit_finish)

        edit.isVisible = false
        logout.isVisible = false
        finish.isVisible = true

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            // handle home icon press in top app bar
            android.R.id.home -> {
                finish()
            }
            // handle edit finish icon press in top app bar
            R.id.profile_edit_finish -> {
                ApiClient.getApiService().profileInfoPatch(ProfileInfoPatchRequest(nameEditText.text.toString(),
                    schoolEditText.text.toString(), majorEditText.text.toString(), degreeEditText.text.toString(),
                    phoneEditText.text.toString(),discordEditText.text.toString(), githubEditText.text.toString(),
                    insEditText.text.toString()),sessionManager.fetchAuthToken()).enqueue(object: Callback<Void>{

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        println("Fail")
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when (response.code()) {
                            200 -> {
                            }
                            else -> print("Failed")
                        }
                    }
                })

                finish()
                startActivity(Intent(this@SocialProfileEditActivity, SocialProfileActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * upload profile image via dialog
     */
    private fun pictureDialog() {
        val options = arrayOf("Camera","Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image from")
        builder.setItems(options) { _, which ->
            if (which == 0) {
                if (!checkCameraPermission()) {
                    println("need camera permission")
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    println("need gallery permission")
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    private fun dataInitialize() {
        // get profile information
        ApiClient.getApiService().profileInfoGet(sessionManager.fetchAuthToken()).enqueue(object :
            Callback<ProfileInfoGetResponse> {
            override fun onResponse(call: Call<ProfileInfoGetResponse>, response: Response<ProfileInfoGetResponse>) {
                when (response.code()) {
                    200 -> {
                        // OK
                        val responseBody = response.body()
                        if (responseBody == null) {
                            Log.e(TAG, "Response body not existent!")
                            //Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                            return
                        }

                        val profileInfo = ProfileInfo(responseBody.name, responseBody.email, responseBody.degree,
                            responseBody.school, responseBody.picture, responseBody.major, responseBody.phone,
                            responseBody.social)

                        Log.d(TAG, profileInfo.toString())

                        nameEditText.setText(profileInfo.name)
                        majorEditText.setText(profileInfo.major)
                        degreeEditText.setText(profileInfo.degree)
                        emailTextView.text = profileInfo.email
                        schoolEditText.setText(profileInfo.school)
                        phoneEditText.setText(profileInfo.phone)
                        discordEditText.setText(profileInfo.social.discord)
                        githubEditText.setText(profileInfo.social.github)
                        insEditText.setText(profileInfo.social.instagram)

                        val path = Constants.IMAGE_URL + profileInfo.email
                        Picasso.get().load(path).into(pictureImageView)
                    }
                    else -> println("Error")
                }
            }

            override fun onFailure(call: Call<ProfileInfoGetResponse>, t: Throwable) {
                println("Error")
                Log.e(TAG, t.message!!)
            }
        })
    }

    /**
     * Check permissions of Camera
     */
    private fun checkCameraPermission() : Boolean{
        return ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this@SocialProfileEditActivity,
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST)
    }

    /**
     * Check permissions of Gallery
     */
    private fun checkStoragePermission(): Boolean{
        return ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Request permissions of Storage
     */
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this@SocialProfileEditActivity,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_REQUEST)
    }

    /**
     * Pick image from gallery
     */
    private fun pickFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICKGALLERY_REQUEST)
    }

    /**
     * Pick image from camera
     */
    private fun pickFromCamera() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        startActivityForResult(intent, IMAGE_PICKCAMERA_REQUEST)
    }

    /**
     * Update imageView
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                IMAGE_PICKCAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    pictureImageView.load(bitmap)
                }
                IMAGE_PICKGALLERY_REQUEST -> {
                    pictureImageView.load(data?.data)
                }
            }
        }
    }

}
