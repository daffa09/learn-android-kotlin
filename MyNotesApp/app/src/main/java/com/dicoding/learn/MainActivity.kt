package com.dicoding.learn

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.learn.adapter.NoteAdapter
import com.dicoding.learn.adapter.OnItemClickCallback
import com.dicoding.learn.databinding.ActivityMainBinding
import com.dicoding.learn.db.NoteHelper
import com.dicoding.learn.entity.Note
import com.dicoding.learn.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    val resultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if(result.data != null) {
            // akan dipanggil jika request code ny ADD
            when(result.resultCode) {
                NoteAddUpdateActivity.RESULT_ADD -> {
                    val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount -1)
                    showSnackbarMessage("Satu item berhasil di tambahkan")
                }
                NoteAddUpdateActivity.RESULT_UPDATE -> {
                    val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                    adapter.updateItem(position, note)
                    binding.rvNotes.smoothScrollToPosition(position)
                    showSnackbarMessage("Satu item berhasil di ubah")
                }
                NoteAddUpdateActivity.RESULT_DELETE -> {
                    val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Satu item berhasil di tambahkan")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)

        adapter = NoteAdapter(object: OnItemClickCallback{
            override fun onItemClickClicked(selectedNote: Note?, position: Int?) {
                val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, selectedNote)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener{
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            resultLauncher.launch(intent)
        }

        loadNoteAsync()

        if (savedInstanceState == null ) {
            // proses ambil data
            loadNoteAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    private fun onSaveStateInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    private fun loadNoteAsync() {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            val notehelper = NoteHelper.getInstance(applicationContext)
            notehelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = notehelper.queryAll()
                val mappingHelper = MappingHelper()
                mappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
            notehelper.close()
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }



}