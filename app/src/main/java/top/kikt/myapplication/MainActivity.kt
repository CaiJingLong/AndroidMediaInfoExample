package top.kikt.myapplication

import android.Manifest
import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var recyclerView: RecyclerView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            setupRecyclerView()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = FrameLayout(this).apply {
            recyclerView = RecyclerView(this@MainActivity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            addView(recyclerView)
        }

        setContentView(contentView)
        requestPermission()
    }

    private fun requestPermission() {
        val permissionList = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionList.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionList.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        }

        requestPermissionLauncher.launch(permissionList.toTypedArray())
    }

    private val originList = mutableListOf<MediaEntity>()

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 4) // 3 columns in grid
        originList.addAll(loadMedia())
        mediaAdapter = MediaAdapter(originList)
        recyclerView.adapter = mediaAdapter
    }

    data class MediaEntity(
        val id: Long,  // 图片ID
        val path: String,  // 图片的路径
        val map: Map<String, String?>  // 图片的其他信息
    )

    private fun loadMedia(): List<MediaEntity> {
        val mediaList = mutableListOf<MediaEntity>()
        val contentResolver: ContentResolver = this.contentResolver

        // Query images from MediaStore
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            val idIndex = it.getColumnIndex(MediaStore.Images.Media._ID)
            val keys = it.columnNames
            println("keys: ${keys.toList().joinToString(", ")}")
            val errorKeys = ArrayList<String>()
            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val path = it.getString(columnIndex)
                val map = mutableMapOf<String, String?>()
                keys.forEach { key ->
                    val keyIndex = it.getColumnIndex(key)
                    if (keyIndex < 0) return@forEach
                    if (errorKeys.contains(key)) return@forEach
                    try {
                        val value = it.getStringOrNull(keyIndex)
                        map[key] = value
                    } catch (e: Exception) {
                        errorKeys.add(key)
                    }
                }
                mediaList.add(MediaEntity(id, path, map))
            }
        }

        return mediaList
    }

    // RecyclerView Adapter for displaying media
    inner class MediaAdapter(private val mediaList: List<MediaEntity>) :
        RecyclerView.Adapter<MediaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
            // 获取屏幕宽度
            val displayMetrics = parent.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val spanCount = 4 // 3 columns in grid
            val itemWidth = screenWidth / spanCount
            val itemHeight = itemWidth

            // 创建ImageView并设置它为正方形
            val imageView = ImageView(parent.context)

            // 设置宽高相等，确保每个图片都是正方形
            imageView.layoutParams = ViewGroup.LayoutParams(itemWidth, itemHeight)

            // 可选：设置图片的缩放类型，保持图片的比例
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            return MediaViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
            val mediaEntity = mediaList[position]
            val mediaPath = mediaEntity.path
            Picasso.get().load("file://$mediaPath")
                .resize(200, 200)
                .centerCrop()
                .into(holder.imageView)

            // 配置点击事件
            holder.imageView.setOnClickListener {
                // 点击事件
                showDialog(mediaEntity)
            }
        }

        override fun getItemCount(): Int = mediaList.size
    }

    class MediaViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    private fun showDialog(mediaEntity: MediaEntity) {
        // 显示dialog
        val dialog = MediaDialogFragment(mediaEntity)
        supportFragmentManager.beginTransaction().add(dialog, "MediaDialogFragment").commit()
    }

}