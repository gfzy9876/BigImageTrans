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
            add("http://tr-osdcp.qunarzz.com/tr-osd-tr-space/img/3390287e7516e496018999e9041cda89.jpg")
            add("https://gss1.bdstatic.com/-vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike60%2C5%2C5%2C60%2C20/sign=78bde825a586c9171c0e5a6ba8541baa/63d9f2d3572c11df449bb3fe612762d0f603c2a1.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573320704794&di=d030b261cbcbcb4fbfdd0fa526abeb3e&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F6%2F57cd346131eb8.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573320704785&di=e29668edc0225c4b828868f6e95b1c76&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2018-01-09%2F5a547250e3c16.jpg")
            add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573320704794&di=bdb4888b11a7164d049b8c63d6104619&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-12-06%2F5a27d01d6809a.jpg")
            add("http://t1.hxzdhn.com/uploads/tu/201611/39/20150423H3939_P4i2J.thumb.700_0.jpg")
            add("http://pic1.win4000.com/wallpaper/2019-02-22/5c6f609f750a1.jpg")
            add("http://pic1.win4000.com/wallpaper/2019-04-30/5cc81303217bf.jpg")
            add("https://uploadfile.bizhizu.cn/up/42/41/85/424185ecafdb499f73990f62da62cf5d.jpg")
            add("http://pic1.win4000.com/wallpaper/9/538542b59c78b.jpg")
            add("http://pic1.win4000.com/mobile/2019-01-16/5c3eddd0878df.jpg")
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