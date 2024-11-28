import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.localdatabase.Homework
import com.example.localdatabase.R
import com.example.localdatabase.databinding.ItemHomeworkBinding
import com.example.localdatabase.helper.HomeworkHelper

class HomeworkAdapter(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {

    var listHomework = ArrayList<Homework>()

    // Gunakan setListHomework dengan lebih hati-hati agar tidak langsung meng-clear data yang sudah ada
    fun setListHomework(listHomework: List<Homework>) {
        this.listHomework.clear()
        this.listHomework.addAll(listHomework)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(selectedHomework: Homework?, position: Int?)
    }

    fun addItem(homework: Homework) {
        this.listHomework.add(homework)
        notifyItemInserted(this.listHomework.size - 1)
    }

    fun updateItem(position: Int, homework: Homework) {
        this.listHomework[position] = homework
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        this.listHomework.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listHomework.size)
    }

    inner class HomeworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHomeworkBinding.bind(itemView)

        fun bind(homework: Homework) {
            binding.tvItemTitle.text = homework.title
            binding.tvItemDate.text = homework.date
            binding.tvItemDescription.text = homework.description

            binding.cvItemHomework.setOnClickListener {
                onItemClickCallback.onItemClicked(homework, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_homework, parent, false)
        return HomeworkViewHolder(view)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        holder.bind(listHomework[position])
    }

    // Mengembalikan jumlah item yang ada
    override fun getItemCount(): Int = listHomework.size
}
