package app.test.kotlin.samplediary.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import app.test.kotlin.samplediary.R
import app.test.kotlin.samplediary.data.Diary
import app.test.kotlin.sampletango.db.DiaryHelper
import kotlinx.android.synthetic.main.diary_detail_layout.*
import kotlinx.android.synthetic.main.diary_detail_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class DiaryDetailFragment: Fragment() {
    private val layout = R.layout.diary_detail_layout
    private var diaryId = ""

    companion object {
        fun newInstance(diaryId: String): DiaryDetailFragment {
            val selfFragment = DiaryDetailFragment()
            if(selfFragment.arguments == null) {
                val args = Bundle()
                args.putString("diaryId", diaryId)
                selfFragment.arguments = args
            }
            return selfFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            diaryId = arguments!!.getString("diaryId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(layout, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.diary_editing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.diaryEditing -> {
                activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.mainLayout, DiaryEditFragment.newInstance(diaryId), DiaryEditFragment::class.simpleName)
                        ?.addToBackStack(DiaryEditFragment::class.simpleName)
                        ?.commit()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        intView()
    }

    private fun intView() {
        val diary = readDiary(diaryId)

        diaryTitle.text = diary.title
        diaryDate.text = convertUnixTimeToString(diary.create_at)
        diaryBody.text = diary.body

        deleteDiary.visibility = View.GONE
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertUnixTimeToString(t: Int): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd(EEE) HH:mm")
        val date = Date(t.toLong() * 1000)
        return sdf.format(date)
    }

    private fun readDiary(diaryId: String) : Diary {
        val diaryDb = DiaryHelper(activity).readableDatabase
        var diary = Diary()
        val cursor = diaryDb.let {
            it?.query("diary", null, "id=$diaryId", null, null, null, null, null)
        }
        cursor.let {
            if(it?.moveToFirst() == false) {
                it.close()
            }
            try {
                do {
                    diary = (Diary(cursor?.getInt(0).toString(),
                            cursor?.getString(1) ?: "",
                            cursor?.getString(2) ?: "",
                            cursor?.getInt(3) ?: 0,
                            cursor?.getInt(4) ?: 0,
                            cursor?.getInt(5) ?: 0
                    ))
                } while (cursor?.moveToNext() == true)

            } catch (e: Exception) {
            }
            cursor?.close()
            return diary
        }
    }
}