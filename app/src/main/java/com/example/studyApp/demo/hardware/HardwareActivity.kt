package com.example.studyApp.demo.hardware

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log

import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_hardware.*
import java.lang.StringBuilder

const val TAG = "HardwareActivity_TAG"

class HardwareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hardware)

        btn.setOnClickListener {
            text.animate()
                .translationX(90F)
                .setDuration(5000)
//                .withLayer()
        }
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {
        spTest()
        super.onDestroy()
    }


    fun spTest() {
        val startTime =  System.currentTimeMillis()
        for (i in 0..1000) {
            val editor = getSharedPreferences("sp_gzp", Context.MODE_PRIVATE).edit()
            val sb = StringBuilder(textContext)
            for (j in 0..10) {
                sb.append(sb.toString())
            }
            editor.putString("key", sb.toString())
            editor.apply()
        }
        Log.d(TAG, "apply end!!! ${  System.currentTimeMillis() - startTime}")
    }

    val textContext = """
        周报 

1.【首页卡片样式适配折叠屏】进度：100%;
    1.1 需求背景: 同镇首页Feed流不同帖子图片在折叠屏手机展示效果不一致;
    1.2 问题原因: 部分卡片图片高度固定，在折叠屏手机上展示异常;
    1.3 详细进度: 重新修改XMl布局，将高度按照指定的宽高比进行约束;
    
2.【本地版oppo折叠屏样式适配】进度：100%;
    2.1 需求描述: 涉及到native需要适配的部分，首页运营位适配和分屏按钮适配;
    2.2 问题原因: 2.2.1 首页运营位宽度没有动态适配屏幕宽度;
                 2.2.2 新折叠屏机型oppo Find N3，分屏应用打开的顺序不同，左右分屏按钮的显示高度是不同的，导致与首页搜索框重叠;
    2.3 详细进度: 问题1已经修复集成;
                 问题2跟oppo同学沟通后，确定为新机型bug,已用oppo提供的云真机验证;

3. 【bug-首页AppbarLayout抖动】进度：80%;
     3.1问题描述: AppbarLayout 在Fling时，滑动下部分RecyclerView卡片，造成抖动；
     3.2详细进度: 代码评审提测产出技术文档;

4. 【需求——同城县域招聘帖上报修改uploadUrl为同城】进度：20%;
    4.1需求背景：hrg招聘详情页上报落地页相关数据时，pid是同城的，简历串联数据时，落地页上报的pid和归属的slot不符合，所以串联失败，数据丢失
    4.2详细进度：需求评审；

5.【bug- 本地版金刚区在折叠状态下展示异常】进度：20%;
    5.1 问题描述：屏幕展开状态下，切换底部tab到【我的】折叠屏幕，然后切换底部tab到【首页】
    5.2 详细进度：涉及到埋点相关的变动，和产品正在沟通、产出方案文档;；
    """.trimIndent()
}


class CustomHardwareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defaultStyle) {

    init {
        Log.d(TAG, "init");
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw")
    }


}

