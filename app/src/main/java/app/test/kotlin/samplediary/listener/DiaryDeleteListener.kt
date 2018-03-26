package app.test.kotlin.samplediary.listener

interface DiaryDeleteListener {
    fun diaryDelete(diaryId: String, position: Int, adapterPosition: Int)
}