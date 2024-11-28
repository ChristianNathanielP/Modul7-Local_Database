package com.example.localdatabase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.localdatabase.adapter.HomeworkAdapter

class HomeworkActivity : AppCompatActivity() {

    // Deklarasi variabel binding dan adapter untuk digunakan dalam activity ini
    private lateinit var binding: HomeworkActivityBinding
    private lateinit var adapter: HomeworkAdapter

    // Membuat object resultLauncher untuk menangani hasil dari activity lain (AddHomeworkActivity)
    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Jika ada data yang dikembalikan dari activity lain
        if (result.data != null) {
            // Memeriksa apakah result code sesuai dengan yang diharapkan (RESULT_ADD)
            when (result.resultCode) {
                AddHomework.RESULT_ADD -> {
                    // Mengambil data homework dari intent
                    val homework = result.data?.getParcelableExtra<Homework>(AddHomework.EXTRA_HOMEWORK) as Homework

                    // Menambahkan data homework ke adapter
                    adapter.addItem(homework)

                    // Menggulir RecyclerView ke posisi terakhir
                    binding.rvHomework.smoothScrollToPosition(adapter.itemCount - 1)

                    // Menampilkan pesan snackbar untuk konfirmasi
                    showSnackbarMessage("Data berhasil ditambahkan")
                }
                AddHomeworkActivity.RESULT_UPDATE -> {
                    val homework = result.data?.getParcelableExtra<Homework>(AddHomeworkActivity.EXTRA_HOMEWORK) as Homework
                    val position = result.data?.getIntExtra(AddHomeworkActivity.EXTRA_POSITION, defaultValue = 0) as Int
                    adapter.updateItem(position, homework)
                    binding.rvHomework.smoothScrollToPosition(position)
                    showSnackbarMessage("Data berhasil diubah")

                }
                AddHomeworkActivity.RESULT_DELETE -> {
                    val position = result.data?.getIntExtra(AddHomeworkActivity.EXTRA_POSITION, defaultValue = 0) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Data berhasil dihapus")
                }

// ... (bagian kode lainnya)

                override fun onCreate(savedInstanceState: Bundle?) {
                // ... (kode onCreate lainnya)

                binding.rvHomework.layoutManager = LinearLayoutManager(context = this)
                binding.rvHomework.setHasFixedSize(true)

                adapter = HomeworkAdapter(object : HomeworkAdapter.OnItemClickCallback {
                    override fun onItemClicked(selectedHomework: Homework?, position: Int?) {
                        val intent = Intent(packageContext
                        = this@HomeworkActivity, AddHomeworkActivity::class.java)
                        intent.putExtra(AddHomeworkActivity.EXTRA_HOMEWORK, selectedHomework)
                        intent.putExtra(AddHomeworkActivity.EXTRA_POSITION, position)
                        resultLauncher.launch(intent)
                    }
                })
                binding.rvHomework.adapter = adapter
            }
            }
        }
    }
}