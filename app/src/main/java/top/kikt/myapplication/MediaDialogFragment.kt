package top.kikt.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment

class MediaDialogFragment(val mediaEntity: MainActivity.MediaEntity) : DialogFragment() {

    private lateinit var root: LinearLayout
    private lateinit var checkbox: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = LinearLayout(context)
        rootView.orientation = LinearLayout.VERTICAL
        rootView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
            }

            root = LinearLayout(context)
            root.setPadding(16)
            root.orientation = LinearLayout.VERTICAL
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(root)
        }

        checkbox = CheckBox(context)
        checkbox.isChecked = true
        checkbox.text = "Show null values"
        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            root.removeAllViews()
            onCreateContentView()
        }

        rootView.addView(checkbox)
        rootView.addView(scrollView)

        return rootView
    }

    private fun onCreateContentView() {
        root.removeAllViews()
        val entities = mediaEntity.map.entries.sortedBy {
            it.key
        }
        for ((key, value) in entities) {
            val isShowNull = checkbox.isChecked
            if (value == null && !isShowNull) {
                continue
            }
            val item = TextView(context)
            item.text = "$key: $value"
            if (value == null) {
                item.setTextColor(0xffff0000.toInt())
            }
            root.addView(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateContentView()
    }
}
