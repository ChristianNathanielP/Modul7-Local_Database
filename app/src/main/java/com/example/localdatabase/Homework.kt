package com.example.localdatabase

import HomeworkAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localdatabase.databinding.ActivityMainBinding
import com.example.localdatabase.helper.HomeworkHelper
import com.example.localdatabase.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class Homework(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var date: String? = null
) : Parcelable

class HomeworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HomeworkAdapter

    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val homework = result.data?.getParcelableExtra<Homework>(AddHomework.EXTRA_HOMEWORK)
            val position = result.data?.getIntExtra(AddHomework.EXTRA_POSITION, -1) ?: -1

            when (result.resultCode) {
                AddHomework.RESULT_ADD -> {
                    if (homework != null) {
                        adapter.addItem(homework)
                        binding.rvHomework.smoothScrollToPosition(adapter.itemCount - 1)
                        showSnackbarMessage("Data berhasil ditambahkan")
                        loadHomeworkAsync()
                    }
                }
                AddHomework.RESULT_UPDATE -> {
                    if (homework != null && position != -1) {
                        adapter.updateItem(position, homework)
                        binding.rvHomework.smoothScrollToPosition(position)
                        showSnackbarMessage("Data berhasil diubah")
                        loadHomeworkAsync()
                    }
                }
                AddHomework.RESULT_DELETE -> {
                    if (position != -1) {
                        adapter.removeItem(position)
                        showSnackbarMessage("Data berhasil dihapus")
                        loadHomeworkAsync()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Homework"
        binding.rvHomework.layoutManager = LinearLayoutManager(this)
        binding.rvHomework.setHasFixedSize(true)

        adapter = HomeworkAdapter(object : HomeworkAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedHomework: Homework?, position: Int?) {
                val intent = Intent(this@HomeworkActivity, AddHomework::class.java)
                intent.putExtra(AddHomework.EXTRA_HOMEWORK, selectedHomework)
                intent.putExtra(AddHomework.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvHomework.adapter = adapter

        // Load homework list from database or another source
        if (savedInstanceState == null) {
            loadHomeworkAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Homework>(EXTRA_STATE)
            if (list != null) {
                adapter.listHomework = list
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddHomework::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun loadHomeworkAsync() {
        lifecycleScope.launch {
            val homeworkHelper = HomeworkHelper.getInstance(applicationContext)
            homeworkHelper.open()

            val deferredHomework = async(Dispatchers.IO) {
                val cursor = homeworkHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            val homework = deferredHomework.await()
            if (homework.isNotEmpty()) {
                adapter.setListHomework(homework)  // Memperbarui data pada adapter
                adapter.notifyDataSetChanged()   // Memberi tahu RecyclerView untuk memperbarui tampilan
            } else {
                adapter.setListHomework(ArrayList())  // Jika tidak ada data
                adapter.notifyDataSetChanged()
                showSnackbarMessage("Data tidak ada")
            }
            homeworkHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listHomework)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvHomework, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}
