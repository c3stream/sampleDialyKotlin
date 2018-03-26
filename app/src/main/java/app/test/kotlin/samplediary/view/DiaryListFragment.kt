package app.test.kotlin.samplediary.view

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import app.test.kotlin.samplediary.R
import app.test.kotlin.samplediary.data.Diary
import app.test.kotlin.samplediary.listener.DiaryListListener
import app.test.kotlin.sampletango.db.DiaryHelper
import kotlinx.android.synthetic.main.diary_list_layout.*

class DiaryListFragment: Fragment(), DiaryListListener {
    private val layout = R.layout.diary_list_layout
    private var diaryList = mutableListOf<Diary>()

    companion object {
        fun newInstance(): DiaryListFragment {
            val selfFragment = DiaryListFragment()
            if(selfFragment.arguments == null) {
                val args = Bundle()
                selfFragment.arguments = args
            }
            return selfFragment
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
                        ?.replace(R.id.mainLayout, DiaryEditFragment.newInstance(), DiaryEditFragment::class.simpleName)
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
        Log.e("data", "-----")
        val diaryList = readDiaryList()

        if (diaryList.isEmpty()) {
            diaryEmpty.visibility = View.VISIBLE
            diaryRecyclerView.visibility = View.GONE

            Log.e("data", "empty")
        } else {
            diaryEmpty.visibility = View.GONE
            diaryRecyclerView.visibility = View.VISIBLE

            Log.e("data", "${diaryList}")

            // アダプター呼び出し
            diaryRecyclerView.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(context)
            diaryRecyclerView.layoutManager = linearLayoutManager
            diaryRecyclerView.adapter = DiaryListViewAdapter(diaryList, this)
            val dividerItemDecoration = DividerItemDecoration(diaryRecyclerView.context, linearLayoutManager.orientation)
//        activity?.let { ContextCompat.getDrawable(it, R.drawable.notification_list_divider)?.let { dividerItemDecoration.setDrawable(it) } }
            diaryRecyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    override fun diaryDetail(diaryId: String) {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.mainLayout, DiaryDetailFragment.newInstance(diaryId), DiaryDetailFragment::class.simpleName)
                ?.addToBackStack(DiaryDetailFragment::class.simpleName)
                ?.commit()
    }

    override fun diaryDelete(diaryId: String, position: Int,  adapterPosition: Int) {
        deleteDiary(diaryId, position, adapterPosition)
    }

    private fun readDiaryList() : List<Diary> {
        val diaryDb = DiaryHelper(activity).readableDatabase
        diaryList = mutableListOf()
        val cursor = diaryDb.let {
            it?.query("diary", null, "is_delete=0", null, null, null, "id DESC", null)
        }
        cursor.let {
            if(it?.moveToFirst() == false) {
                it.close()
            }
            try {
                do {
                    diaryList.add(Diary(cursor?.getInt(0).toString(),
                            cursor?.getString(1) ?: "",
                            cursor?.getString(2) ?: "",
                            cursor?.getInt(3) ?: 0,
                            cursor?.getInt(4) ?: 0,
                            cursor?.getInt(5) ?: 0
                            ))
                } while (cursor?.moveToNext() == true)

            } catch (e: Exception) {
                Log.e("data", "${diaryList}", e)
            }
            cursor?.close()
            return diaryList
        }
    }

    private fun deleteDiary(diaryId: String, position: Int, adapterPosition: Int) {
        val diary = ContentValues()
        diary.put("is_delete", 1)
        val diaryDb = DiaryHelper(activity).writableDatabase
        diaryDb.let{
            it?.update("diary", diary, "id=$diaryId", null)
            diaryList.removeAt(position)
            diaryRecyclerView.removeViewAt(adapterPosition)
            diaryRecyclerView.adapter.notifyItemRemoved(adapterPosition)
            diaryRecyclerView.adapter.notifyItemRangeChanged(adapterPosition, diaryList.size)
        }
    }
}