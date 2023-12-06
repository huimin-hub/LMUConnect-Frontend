package de.lmu.lmuconnect.social.group

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import de.lmu.lmuconnect.R
import de.lmu.lmuconnect.general.api.SessionManager
import de.lmu.lmuconnect.general.matrix.MatrixSessionHolder
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams

class AddGroupActivity : AppCompatActivity() {

    // Components from UI social_add_friend_activity.xml
    private lateinit var nameEditText: EditText
    private lateinit var typeEditText: EditText
    private var lastCheckedTypePos = 0

    private lateinit var sessionManager: SessionManager
    private lateinit var matrixSession: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_group)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sessionManager = SessionManager(this)
        matrixSession = MatrixSessionHolder.getCurrentSession()!!

        // Initialize Views
        nameEditText = findViewById(R.id.et_create_group_input_name)
        typeEditText = findViewById(R.id.et_create_group_input_typ)

        val types = arrayListOf<CharSequence>(
            "LECTURE",
            "INTERNSHIP",
            "SEMINAR",
            "TUTORIAL",
            "EXERCISE",
            "OTHERS"
        )
        lastCheckedTypePos = types.size - 1

        nameEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.contains("！"))
                    nameEditText.error = "No exclamation mark allowed."
                else if (text.length <= 3)
                    nameEditText.error = getString(R.string.menu_min_3_letters)
                else nameEditText.error = null
            }
        }

        // handle typ button press to select group type
        typeEditText.setOnClickListener{
            AlertDialog.Builder(this@AddGroupActivity)
                .setTitle("Select Group Type: ")
                .setNeutralButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("OK") { dialog, which ->
                    lastCheckedTypePos = (dialog as AlertDialog).listView.checkedItemPosition

                    typeEditText.setText(types[lastCheckedTypePos].toString())
                }
                .setSingleChoiceItems(types.toTypedArray(), lastCheckedTypePos, null)

                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_addchat_top_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            // handle finish icon press to create new group room
            R.id.action_finish -> {
                if (nameEditText.error != null || nameEditText.text.isEmpty()) {
                    Toast.makeText(this, getString(R.string.menu_resolve_errors), Toast.LENGTH_SHORT).show()
                    return false
                }

                try {
                    val createRoomParams = CreateRoomParams()
                    createRoomParams.topic = typeEditText.text.toString()
                    createRoomParams.name = nameEditText.text.toString()
                    lifecycleScope.launch{
                        MatrixSessionHolder.currentRoomID = matrixSession.roomService().createRoom(createRoomParams)
                    }
                } catch (_: Error) { }
                //startActivity(Intent(this@, SocialProfileActivity::class.java))
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}