package app.test.kotlin.samplediary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import app.test.kotlin.samplediary.view.DiaryListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainLayout, DiaryListFragment.newInstance(), DiaryListFragment::class.simpleName)
                    .commit()
        }
    }
}
