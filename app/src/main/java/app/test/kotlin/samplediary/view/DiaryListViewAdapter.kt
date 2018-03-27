package app.test.kotlin.samplediary.view

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.test.kotlin.samplediary.R
import app.test.kotlin.samplediary.data.Diary
import app.test.kotlin.samplediary.listener.DiaryListListener
import kotlinx.android.synthetic.main.diary_detail_layout.view.*
import java.text.SimpleDateFormat
import java.util.*


class DiaryListViewAdapter(private val diaryList: List<Diary>, private val  listener: DiaryListListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private class DiaryListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return DiaryListViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.diary_detail_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder == null) {
            return
        }
        holder as DiaryListViewHolder
        holder.itemView.diaryTitle.text = diaryList[position].title
        holder.itemView.diaryDate.text = convertUnixTimeToString(diaryList[position].create_at)
        holder.itemView.diaryBody.text = diaryBody(diaryList[position].body)

        holder.itemView.setOnClickListener {
            listener.diaryDetail(diaryList[position].id)
        }

        holder.itemView.deleteDiary.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if(adapterPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            listener.diaryDelete(diaryList[position].id, position, adapterPosition)
        }
    }

    private fun diaryBody(body: String) : String {
        return if (body.length > 140) {
            body.substring(0, 140) + "..."
        } else {
            body
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertUnixTimeToString(t: Int): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd(EEE) HH:mm")
        val date = Date(t.toLong() * 1000)
        return sdf.format(date)
    }

}