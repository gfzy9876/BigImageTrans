package pers.zy.bigimagetrans

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image_detail.*
import pers.zy.bigimagetrans.translib.ImageDetailFrameLayout

open class ImageDetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_IMG_RES_IDS = "arg_img_res_ids"
        const val ARG_POSITION = "arg_position"

        fun start(context: Activity, view: View, resId: List<String>, position: Int) {
            context.startActivity(
                Intent(context, ImageDetailActivity::class.java).apply {
                    putExtra(ARG_IMG_RES_IDS, resId.toTypedArray())
                    putExtra(ARG_POSITION, position)
                }, ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context, view,
                    ViewCompat.getTransitionName(view) ?: return
                ).toBundle()
            )
        }
    }

    private lateinit var adapter: DetailImageAdapter
    protected lateinit var imageListArray: Array<String>
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        setContentView(R.layout.activity_image_detail)
        imageListArray = intent.getStringArrayExtra(ARG_IMG_RES_IDS) ?: return
        position = intent.getIntExtra(ARG_POSITION, 0)
        initViewPager()
    }

    private fun initViewPager() {
        adapter = DetailImageAdapter()
        vp_image_detail.adapter = adapter
        vp_image_detail.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                this@ImageDetailActivity.position = position
            }
        })
        vp_image_detail.currentItem = position
        fl_image_detail.setOnMoveExitListener(object : ImageDetailFrameLayout.OnMoveExitListener {
            override fun onMove(fraction: Float) {
                bg_mask.alpha = fraction
            }

            override fun restore() {
                bg_mask.alpha = 1f
            }

            override fun onExit() {
                checkFinish()
            }
        })
    }

    override fun onBackPressed() {
        checkFinish()
    }

    private fun checkFinish() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                val mCurrentView = adapter.mCurrentView ?: return
                val transitionName = ViewCompat.getTransitionName(mCurrentView) ?: return
                sharedElements?.let {
                    it.clear()
                    it[transitionName] = mCurrentView
                }
                names?.let {
                    it.clear()
                    it.add(transitionName)
                }
            }
        })

        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("position", position)
        })
        supportFinishAfterTransition()
    }

    inner class DetailImageAdapter : PagerAdapter() {
        var mCurrentView: View? = null

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun getCount(): Int = this@ImageDetailActivity.imageListArray.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = PhotoView(this@ImageDetailActivity).apply {
                ViewCompat.setTransitionName(this, "share$position")
                if (position == this@ImageDetailActivity.position) {
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                            this@ImageDetailActivity.supportStartPostponedEnterTransition()
                        }
                    })
                }
                setOnClickListener {
                    checkFinish()
                }
            }
            Glide.with(this@ImageDetailActivity)

                .load(imageListArray[position]).diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        imageView.setImageDrawable(resource)
                    }
                })
            container.addView(imageView)
            return imageView
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            mCurrentView = `object` as View
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }
    }
}