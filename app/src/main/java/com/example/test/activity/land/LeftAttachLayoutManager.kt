import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 自定义布局管理器，实现吸附效果
 * 使得Item总是吸附到距离左侧100px的位置
 */
class LeftAttachLayoutManager(
    context: Context,
    private val attachDistancePx: Int = 100 // 吸附距离，默认100px
) : LinearLayoutManager(context, HORIZONTAL, false) {

    private var pendingScrollPosition = RecyclerView.NO_POSITION
    private var pendingScrollOffset = 0
    private val targetOffset = attachDistancePx // 目标吸附位置

    // 保存RecyclerView的引用
    private var recyclerViewRef: RecyclerView? = null

    // 自定义平滑滚动器
    private inner class AttachSmoothScroller(
        context: Context
    ) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
        ): Int {
            // 计算目标位置与当前位置的偏移量
            return targetOffset - viewStart
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            // 调整每像素滑动速度，使滑动更加平滑
            return 100f / displayMetrics.densityDpi
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_ANY // 允许滑动到任意位置
        }
    }

    /**
     * 当布局管理器附加到RecyclerView时调用
     */
    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        recyclerViewRef = view
    }

    /**
     * 当布局管理器从RecyclerView分离时调用
     */
    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)
        recyclerViewRef = null
    }

    /**
     * 滚动结束后，检查是否需要吸附
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            // 滚动停止后，计算并执行吸附
            snapToTargetPosition()
        }
    }

    /**
     * 滚动时实时计算应该吸附到哪个Item
     */
    private fun snapToTargetPosition() {
        val recyclerView = recyclerViewRef ?: return

        // 找到最接近目标位置的Item
        val closestChild = findClosestChildToPosition()
        if (closestChild != null) {
            val closestPosition = getPosition(closestChild)
            val closestLeft = getDecoratedLeft(closestChild)
            val offsetToTarget = targetOffset - closestLeft

            if (abs(offsetToTarget) > 10) { // 添加一个阈值，避免小距离抖动
                // 直接使用recyclerView的smoothScrollToPosition方法
                recyclerView.smoothScrollToPosition(closestPosition)
            }
        }
    }

    /**
     * 找到最接近目标位置的子视图
     */
    private fun findClosestChildToPosition(): View? {
        if (childCount == 0) return null

        var closestChild: View? = null
        var closestDistance = Int.MAX_VALUE

        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childLeft = getDecoratedLeft(child)
            val distance = abs(childLeft - targetOffset)

            if (distance < closestDistance) {
                closestDistance = distance
                closestChild = child
            }
        }

        return closestChild
    }

    /**
     * 重写此方法确保使用我们的自定义SmoothScroller
     */
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?, position: Int
    ) {
        val scroller = AttachSmoothScroller(recyclerView.context)
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    /**
     * 当布局完成后检查是否需要吸附
     */
    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        if (pendingScrollPosition != RecyclerView.NO_POSITION) {
            pendingScrollPosition = RecyclerView.NO_POSITION
            pendingScrollOffset = 0
            // 布局完成后，尝试吸附
            snapToTargetPosition()
        }
    }
}