package app.test.kotlin.samplediary.view

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import app.test.kotlin.samplediary.R
import app.test.kotlin.samplediary.data.Diary
import app.test.kotlin.sampletango.db.DiaryHelper
import kotlinx.android.synthetic.main.diary_edit_layout.*
import java.util.*

class DiaryEditFragment: Fragment() {
    private val layout = R.layout.diary_edit_layout
    private var diaryId = ""


    companion object {
        fun newInstance(): DiaryEditFragment {
            val selfFragment = DiaryEditFragment()
            if(selfFragment.arguments == null) {
                val args = Bundle()
                selfFragment.arguments = args
            }
            return selfFragment
        }

        fun newInstance(diaryId: String): DiaryEditFragment {
            val selfFragment = DiaryEditFragment()
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
            if (arguments!!.containsKey("diaryId")) {
                diaryId = arguments.getString("diaryId")!!
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(layout, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.diary_edited, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.diaryEdited -> {
                writeDiary()
                activity.supportFragmentManager.popBackStack()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView(){
        if(diaryId.isEmpty()) {
            return
        }
        val diary = readDiary(diaryId)
        diaryTitleEditText.setText(diary.title)
        diaryBodyEditText.setText(diary.body)
    }

    private fun writeDiary() {
        val diary = ContentValues()

        if (diaryId.isEmpty()) {
            diary.put("title", diaryTitleEditText.text.toString())
            diary.put("`body`", diaryBodyEditText.text.toString())
            diary.put("create_at", (System.currentTimeMillis() / 1000L).toInt())
            diary.put("update_at", (System.currentTimeMillis() / 1000L).toInt())
            diary.put("is_delete", 0)
            val diaryDb = DiaryHelper(activity).writableDatabase
            diaryDb.let{
                it?.insert("diary", null, diary)
                activity?.supportFragmentManager?.popBackStack()
            }
        } else {
            diary.put("title", diaryTitleEditText.text.toString())
            diary.put("`body`", diaryBodyEditText.text.toString())
            diary.put("update_at", (System.currentTimeMillis() / 1000L).toInt())
            val diaryDb = DiaryHelper(activity).writableDatabase
            diaryDb.let{
                it?.update("diary", diary, "id=$diaryId", null)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
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
                Log.e("data", "${diary}", e)
            }
            cursor?.close()
            return diary
        }
    }

}