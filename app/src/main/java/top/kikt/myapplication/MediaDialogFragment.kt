package top.kikt.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment

class MediaDialogFragment(val mediaEntity: MainActivity.MediaEntity) : DialogFragment() {

    private lateinit var root: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            root = LinearLayout(context)
            root.setPadding(16)
            root.orientation = LinearLayout.VERTICAL
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(root)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 添加内容

        for ((key, value) in mediaEntity.map) {
            if (value == null) {
                continue
            }
            val item = TextView(context)
            item.text = "$key: $value"
            root.addView(item)
        }
    }
}
