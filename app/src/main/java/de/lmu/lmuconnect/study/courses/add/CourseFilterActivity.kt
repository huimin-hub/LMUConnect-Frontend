package de.lmu.lmuconnect.study.courses.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import de.lmu.lmuconnect.R

private const val TAG = "CourseFilterActivity"

class CourseFilterActivity : AppCompatActivity() {

    // View
    private lateinit var semesterChips : ChipGroup
    private lateinit var departmentChips : ChipGroup
    private lateinit var showResultButton : Button

    // Data
    private lateinit var semesterArrayList: MutableList<String>
    private lateinit var departmentArrayList : MutableList<String>
    private lateinit var filterList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_filter)

        // Init action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.app_cancel)

        // Initialization
        semesterChips = findViewById(R.id.chips_group)
        departmentChips = findViewById(R.id.chips_group2)
        showResultButton = findViewById(R.id.btn_study_course_filter)
        filterList = arrayListOf()
        //if (savedInstanceState?.getStringArrayList("data") != null)
        //    filterList = savedInstanceState.getStringArrayList("data") as ArrayList<String>

        chipsInitialize()

        showResultButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra("data", filterList))
            finish()
        }
    }

    /**override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putStringArrayList("data", filterList)
    }**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.study_course_filtering, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_reset -> {
                // reset the chips to unchecked state
                semesterChips.clearCheck()
                filterList.clear()
                setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra("data", filterList))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun chipsInitialize() {
        semesterArrayList = arrayListOf()
        semesterArrayList.add("WiSe2223")
        semesterArrayList.add("SoSe2022")

        setTag(semesterArrayList, semesterChips)
    }

    private fun setTag(tagList: MutableList<String>, chipGroup: ChipGroup) {
        for (index in tagList.indices) {
            val tagName = tagList[index]
            val chip = Chip(this)
            val chipDrawable = ChipDrawable.createFromAttributes(
                this,
                null,
                0,
                R.style.ChipFilteringStyle
            )
            chip.setChipDrawable(chipDrawable)
            chip.text = tagName

            // Responds to chip checked/unchecked
            chip.setOnCheckedChangeListener { clickableChip, isChecked ->
                if (isChecked) {
                    filterList.add(clickableChip.text.toString())
                    Log.i(TAG, clickableChip.text.toString() + " is selected.")
                } else {
                    filterList.remove(clickableChip.text.toString())
                    Log.i(TAG, clickableChip.text.toString() + " is unselected.")
                }
            }

            chipGroup.addView(chip)
        }
    }
}