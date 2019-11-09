package pers.zy.bigimagetrans

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import pers.zy.bigimagetrans.view.RoundImageView

class MainActivity : AppCompatActivity() {

    lateinit var layoutManager: GridLayoutManager
    lateinit var adapter: ImageAdapter
    private var mTranState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mImageUriList = mutableListOf<String>().apply {
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/760bc068bcad4a27942a57c5fe53896c.jpg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/fc041cb7ec874980b69bd681b39fe82e.jpeg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/924a6a55b44741fd88044a7a952a45be.jpeg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/57a407c6994646f4ae568cc49de384e7.jpeg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/05bbf99f082e45428b05167dae0af28a.jpeg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/1be7c3f092044e7f90e75b691e33ab3e.jpeg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/71186a77227f4268ba63fc76b1b876cb.webp")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/0d670d33cdb6423fa8454bd3a3785490.jpg")
            add("https://oss.laoyouzhibo.com/square/release/moment_images/image/c1f5b45dfef046bcac97d9bb79451d1d.jpg")
            add("https://oss.laoyouzhibo.com/square/release/users/photo/dfc6aded30b340dfb0fb484a57d36324.jpg")
            add("https://oss.laoyouzhibo.com/square/release/users/photo/b782af9d42554e6080f33985676d210a.jpg")
        }

        layoutManager = GridLayoutManager(this, 3)
        rv_image.layoutManager = layoutManager
        adapter = ImageAdapter(mImageUriList)
        rv_image.adapter = adapter

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (mTranState != null) {
                    val newPosition = mTranState!!.getInt("position", 0)
                    names?.let {
                        it.clear()
                        it.add("${ImageDetailActivity.EXTRA_SHARED_TRANS_NAME}$newPosition")
                    }
                    sharedElements?.let {
                        it.clear()
                        it["${ImageDetailActivity.EXTRA_SHARED_TRANS_NAME}$newPosition"] = layoutManager.getChildAt(newPosition)!!.findViewById(R.id.iv_main)
                    }
                    mTranState = null
                }
            }
        })
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            mTranState = data.extras
        }
    }

    inner class ImageAdapter(private val mImageList: List<String>) :
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))
        }

        override fun getItemCount(): Int = mImageList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(this@MainActivity)
                .load(mImageList[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : DrawableImageViewTarget(holder.mIvPreview) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        holder.mIvMain.setImageDrawable(resource)
                        super.onResourceReady(resource, transition)
                    }
                })

            ViewCompat.setTransitionName(holder.mIvMain, "${ImageDetailActivity.EXTRA_SHARED_TRANS_NAME}$position")
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mIvMain: RoundImageView = itemView.findViewById(R.id.iv_main)
            var mIvPreview: RoundImageView = itemView.findViewById(R.id.iv_preview)

            init {

                mIvMain.setOnClickListener {
                    ImageDetailActivity.start(
                        itemView.context as Activity,
                        mIvMain,
                        this@ImageAdapter.mImageList,
                        adapterPosition
                    )
                }
            }
        }
    }
}