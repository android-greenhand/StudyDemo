package com.example.studyApp.comment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyApp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.annotations.Nullable


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var listView: View
    private lateinit var commentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var editTextBtn: TextView

    private val mInputManager: InputMethodManager? by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme) //给dialog设置主题为透明背景 不然会有默认的白色背景
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        // 在这里将view的高度设置为精确高度，即可屏蔽向上滑动不占全屏的手势。如果不设置高度的话 会默认向上滑动时dialog覆盖全屏
        val view: View = inflater.inflate(R.layout.bottom_sheet_dialog_fragment_recycle_view, container, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getScreenHeight(activity) * 2 / 3)
        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        listView = view.findViewById(R.id.list_layout)
        commentView = view.findViewById(R.id.comment_layout)
        editText = view.findViewById(R.id.edit_text_view)
        recyclerView = view.findViewById(R.id.recycle_view)
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        val itemAdapter = ItemAdapter()
        recyclerView.adapter = itemAdapter

        editTextBtn = view.findViewById<TextView>(R.id.edit_text_btn)
        editTextBtn.setOnClickListener {
            editText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            editTextBtn.visibility = View.GONE
            showSoftInputFromWindow(editText)
        }

        val bottomSheetDialog = dialog as? BottomSheetDialog
        if (bottomSheetDialog != null) {
            bottomSheetDialog.behavior.isHideable = false //禁止下拉取消弹框
            bottomSheetDialog.behavior.peekHeight = view.measuredHeight //让dialog的内容显示完整
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onStop() {
        super.onStop()
        // onDestroy() 有可能延迟执行
        if (activity != null) {
            ViewModelProvider(activity!!, ViewModelProvider.NewInstanceFactory())
                    .get(CommentActivityViewModel::class.java)
                    .mCommentStatus.value = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideSoftInputFromWindow(editText)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setCanceledOnTouchOutside(true) //设置点击外部可消失
        val window: Window? = dialog.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) //设置使软键盘弹出的时候dialog不会被顶起
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
        val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.height
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


