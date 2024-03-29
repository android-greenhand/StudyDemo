package com.example.studyApp.customView.comment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyApp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.annotations.Nullable


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var listView: View
    private lateinit var commentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var editTextBtn: TextView
    private val mCommentActivityViewModel by lazy{
        ViewModelProvider(activity as CommentActivity).get(CommentActivityViewModel::class.java)
    }

    private val mInputManager: InputMethodManager? by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme) //给dialog设置主题为透明背景 不然会有默认的白色背景
        mCommentActivityViewModel.mCommentStatus.value = true
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        // 在这里将view的高度设置为精确高度，即可屏蔽向上滑动不占全屏的手势。如果不设置高度的话 会默认向上滑动时dialog覆盖全屏
        val view: View = inflater.inflate(R.layout.bottom_sheet_dialog_fragment_recycle_view, container, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getScreenHeight(activity) )
        return view
    }

    private lateinit var web_view: CustomWebView
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        web_view = view.findViewById<CustomWebView>(R.id.web_view)
        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        web_view.settings.javaScriptEnabled = true


        val url = "http://www.baidu.com"
        web_view.loadUrl(url)
//
//        listView = view.findViewById(R.id.list_layout)
//        commentView = view.findViewById(R.id.comment_layout)
//        editText = view.findViewById(R.id.edit_text_view)
//        recyclerView = view.findViewById(R.id.recycle_view)
//        recyclerView.layoutManager = LinearLayoutManager(context).apply {
//            orientation = LinearLayoutManager.VERTICAL
//        }
//        val itemAdapter = ItemAdapter()
//        recyclerView.adapter = itemAdapter
//
//        editTextBtn = view.findViewById<TextView>(R.id.edit_text_btn)
//        editTextBtn.setOnClickListener {
//            editText.visibility = View.VISIBLE
//            recyclerView.visibility = View.GONE
//            editTextBtn.visibility = View.GONE
//            showSoftInputFromWindow(editText)
//        }

        val bottomSheetDialog = dialog as? BottomSheetDialog


        if (bottomSheetDialog != null) {
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.behavior.isHideable = false //禁止下拉取消弹框
            bottomSheetDialog.behavior.peekHeight = view.measuredHeight //让dialog的内容显示完整

//            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetDialog.behavior.halfExpandedRatio = 0.7f
            bottomSheetDialog.behavior.isFitToContents = false
            bottomSheetDialog.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                var lastState = 0

                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    if (lastState == BottomSheetBehavior.STATE_EXPANDED) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING && web_view.canScrollVertically(
                                -1
                            )
                        ) {//判断为向下拖动行为时，则强制设定状态为展开
                            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED;
                            return
                        }
                    }
                    lastState = newState

                    when(newState){
                        BottomSheetBehavior.STATE_EXPANDED -> bottomSheetDialog.behavior.isFitToContents = true
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

            })
        }

    }

    override fun onPause() {
        super.onPause()
        web_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        web_view.onResume()
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog =  CustomBottomSheetDialog(requireContext(), getTheme())
//        dialog.setContentView(TextView(context).apply {
//            text = "CustomBottomSheetDialog"
//            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,500)
//            this.layoutParams = lp
//        })
//        return dialog
//    }

    override fun onStop() {
        super.onStop()
        // onDestroy() 有可能延迟执行
    }

    override fun onDestroy() {
        super.onDestroy()
        //hideSoftInputFromWindow(editText)
        mCommentActivityViewModel.mCommentStatus.value = false
        web_view.destroy()

    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setCanceledOnTouchOutside(true) //设置点击外部可消失
        val window: Window? = dialog.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) //设置使软键盘弹出的时候dialog不会被顶起
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    private fun showSoftInputFromWindow(editText: EditText?) {
        editText?.let {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.requestFocus()
            mInputManager?.showSoftInput(it, 0)
        }
    }

    private fun hideSoftInputFromWindow(editText: EditText?) {
        editText?.let {
            mInputManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
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

class ItemAdapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_starred_discount, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = holder.itemView.findViewById<TextView>(R.id.main_activity_recycle_view_item_name)
        comment.text = "评论:$position "
    }

    override fun getItemCount(): Int {
        return 30
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)



class WebDetailDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {

    private fun ensureContainerAndBehavior() {

    }


    override fun setContentView(view: View) {
        super.setContentView(view)
    }


}
