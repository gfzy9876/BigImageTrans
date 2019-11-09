# BigImageTrans
仿微信图片图片拖动关闭效果实现

这个是本人根据[chrisbanes](https://github.com/chrisbanes)大神的[PhotoView](https://github.com/chrisbanes/PhotoView)为图片基础做的拖动缩放效果实现，使用如下：

在ViewPager下外层添加ImageDetailFrameLayout（DetailImageViewPager忽略，这个是[PhotoView](https://github.com/chrisbanes/PhotoView)中提到的framework中的一个问题，详系的可以去看他的博客哦。）

DetailImageViewPager里面就是放的就是一张张图片。

本人的实现是通过外部拦截，直接对ViewPager做移动操作。

* 包裹ImageDetailFrameLayout

```xml
<pers.zy.image_trans_lib.ImageDetailFrameLayout
        android:id="@+id/fl_image_detail"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <pers.zy.bigimagetrans.view.DetailImageViewPager
            android:id="@+id/vp_image_detail"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </pers.zy.image_trans_lib.ImageDetailFrameLayout>
```

* 创建回调

  ```kotlin
  fl_image_detail.setOnMoveExitListener(object : ImageDetailFrameLayout.OnMoveExitListener {
      override fun onMove(fraction: Float) {
        //onMove是在手指拖动时候拖动进度监听回调
        bg_mask.alpha = fraction
      }
  
      override fun restore() {
        //restore在拖动释放时，拖动距离未超出退出长度，恢复时的回调
        bg_mask.alpha = 1f
      }
  
      override fun onExit() {
        //onExit在是拖动超出退出长度，关闭的回调
        checkFinish()
      }
  })
  ```

* 此外可以自己设置拖动退出长度和拖动状态图片的最小缩放值（拖动退出长度默认为屏幕1/4，最小缩放值为0.4f）

```kotlin
fl_image_detail.maxMoveExitLength = 400//设置拖动退出长度为400px
fl_image_detail.minScale = 0.5f//设置最小缩放值为0.5f
```

### 预览如下：

![img](https://github.com/gfzy9876/BigImageTrans/blob/master/preview/preview.png)

![img](https://github.com/gfzy9876/BigImageTrans/blob/master/preview/preview2.png)