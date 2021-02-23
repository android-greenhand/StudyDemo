package com.example.studyApp.behavior

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyApp.R
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource

class BehaviorActivity() : AppCompatActivity() {

    private val data: List<ItemDataBean> = initData()
    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }
    lateinit var recycler: Recycler<ItemDataBean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_behavior)

        recyclerView?.apply { this.layoutManager = LinearLayoutManager(context) }
        recycler = Recycler.adopt(recyclerView) {
            row<ItemDataBean, TextView> {
                create { context: Context ->
                    view = TextView(context).apply {
                        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                        lp.bottomMargin = 10;
                        this.layoutParams = lp
                        gravity= Gravity.CENTER
                        textSize = 40f
                        setBackgroundColor(Color.argb(100,230, 245, 255))

                    }
                    bind { itemDataBean -> view.text = itemDataBean.string }
                }
            }

        }

        recycler.update { data = this@BehaviorActivity.data.toDataSource() }

    }


    fun initData() = listOf(
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("1"),
            ItemDataBean("2"),
            ItemDataBean("3")
    )
}