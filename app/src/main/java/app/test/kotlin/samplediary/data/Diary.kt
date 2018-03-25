package app.test.kotlin.samplediary.data

import java.io.Serializable

data class Diary(
        val id: String = "",
        val title: String = "",
        val body: String = "",
        val create_at: Int = 0,
        val update_at: Int = 0,
        val is_delete: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}