package com.example.studyApp.packageManager

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.studyApp.R
import com.example.studyApp.customView.comment.BottomSheetBehavior
import com.example.studyApp.customView.comment.BottomSheetDialog
import com.example.studyApp.customView.comment.CommentActivityViewModel
import com.example.studyApp.utils.AppUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.annotations.Nullable


class OtherAppFragment : BottomSheetDialogFragment() {

    private lateinit var listView: LinearLayout

    private val mCommentActivityViewModel by lazy {
        ViewModelProvider(activity as PKMSActivity).get(CommentActivityViewModel::class.java)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme) //给dialog设置主题为透明背景 不然会有默认的白色背景
        mCommentActivityViewModel.mCommentStatus.value = true

    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        // 在这里将view的高度设置为精确高度，即可屏蔽向上滑动不占全屏的手势。如果不设置高度的话 会默认向上滑动时dialog覆盖全屏
        val view: View = inflater.inflate(R.layout.dialog_fragment_open_other_app_recycle_view, container, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
           600)
        return view
    }

    private fun booleanTransformString(boolean: Boolean) = if (boolean) "" else "(未安装)"

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {


        listView = view.findViewById(R.id.container)

        var result = AppUtils.isInstalledApk("com.autonavi.minimap",this.context)
        Log.d("gzp isInstalledApk",result.toString())

         result = AppUtils.isInstalledApk("com.baidu.BaiduMap",this.context)
        Log.d("gzp isInstalledApk com.baidu.BaiduMap",result.toString())

        val gaoDe = PackageUlti.isInstalled(this.context, "com.autonavi.minimap")
        val gaoDetext = TextView(context,null,0,R.style.FeedAdCardItemOtherLabelStyle).apply {
            textSize = 20f
            text = "高德地图" + booleanTransformString(gaoDe)
            setOnClickListener {
                if (gaoDe) {
                    val intent = Intent();
                    intent.setPackage("com.autonavi.minimap");
                    intent.data = Uri.parse("androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=36.547901&lon=104.258354&dev=1&style=2");
                    startActivity(intent);
                } else {
                    Toast.makeText(it.context, "请先安装", LENGTH_SHORT).show()
                }
            }
        }
        listView.addView(gaoDetext)




        val baidu = PackageUlti.isInstalled(this.context, "com.baidu.BaiduMap")
        val textBaidu = TextView(context,null,0,R.style.FeedAdCardItemOtherLabelStyle).apply {
            textSize = 20f
            this.text = "百度地图" + booleanTransformString(baidu)
            setOnClickListener {
                if (baidu) {
                    val intent = Intent()
                    intent.data = Uri.parse("baidumap://map/geocoder?src=andr.baidu.openAPIdemo&address=北京市海淀区上地信息路9号奎科科技大厦")
                    startActivity(intent);
                } else {
                    Toast.makeText(it.context, "请先安装", LENGTH_SHORT).show()
                }
            }
        }
        listView.addView(textBaidu)



        val bottomSheetDialog = dialog as? BottomSheetDialog


        if (bottomSheetDialog != null) {
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.behavior.isHideable = false //禁止下拉取消弹框
            bottomSheetDialog.behavior.peekHeight = view.measuredHeight //让dialog的内容显示完整

//            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetDialog.behavior.halfExpandedRatio = 0.3f
            bottomSheetDialog.behavior.isFitToContents = false
            bottomSheetDialog.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                var lastState = 0

                override fun onStateChanged(bottomSheet: View, newState: Int) {


                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

            })
        }

    }

    override fun onStart() {
        super.onStart()

    }
    override fun onDestroy() {
        super.onDestroy()
        mCommentActivityViewModel.mCommentStatus.value = false

    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setCanceledOnTouchOutside(true) //设置点击外部可消失
        val window: Window? = dialog.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) //设置使软键盘弹出的时候dialog不会被顶起
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }


    /**
     * 得到屏幕的高
     */
    private fun getScreenHeight(context: Context?): Int {
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.height
    }

    /**
     * 点击蒙层消失，需要上报点击埋点
     * 自定义Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val tContext = context
        return if (tContext != null) {
            com.example.studyApp.customView.comment.BottomSheetDialog(tContext)
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }


}

