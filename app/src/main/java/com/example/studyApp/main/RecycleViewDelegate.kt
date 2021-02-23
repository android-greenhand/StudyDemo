package com.example.studyApp.main


import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyApp.R
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource

class RecycleViewDelegate(var view: View) {

    private var recycler: Recycler<ActivityBean>

    private val recyclerView = view.findViewById<RecyclerView>(R.id.main_recycle_view).apply {
        this.layoutManager = LinearLayoutManager(view.context)
        this.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))
    }

    init {
        recycler = Recycler.adopt(recyclerView) {
            row<ActivityBean, ConstraintLayout> {
                create(R.layout.starred_discount) {
                    val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
                    view.layoutParams = lp;
                    val textView = view.findViewById<TextView>(R.id.main_activity_recycle_view_item_name)
                    val image = view.findViewById<ImageView>(R.id.main_activity_recycle_view_item_image)
                    bind { activityBean ->
                        image.visibility = if (activityBean.multiDirectory) View.VISIBLE else View.INVISIBLE
                        textView.text = activityBean.activityName
                        view.setOnClickListener { view.context.startActivity(activityBean.nextIntent)
                        }
                    }
                }
            }
            extraItem<String,TextView> {
                create { context -> view = TextView(context).apply {
                    this.setBackgroundColor(Color.argb(100,230, 245, 255))
                    this.textSize=10.0f
                    gravity =Gravity.RIGHT
                }
                    bind { s -> view.text= s
                    }
                }
            }
        }
    }

    fun recycleViewUpdate(list: List<ActivityBean>) {
        recycler.update {
            data = list.toDataSource()
            extraItem = "提示： 🌟代表多级目录"
        }
    }
}