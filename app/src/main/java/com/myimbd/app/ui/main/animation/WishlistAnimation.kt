package com.myimbd.app.ui.main.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.myimbd.app.R
import kotlin.math.abs

/**
 * Utility class for creating smooth wishlist animations
 * Animates a heart icon or movie thumbnail from a clicked item to the wishlist icon in the toolbar
 */
class WishlistAnimation {
    
    companion object {
        private const val TAG = "WishlistAnimation"
        private const val ANIMATION_DURATION = 800L
        private const val SCALE_DURATION = 600L
        private const val FADE_DURATION = 300L
        private const val FINAL_SCALE = 0.3f
        private const val INITIAL_SCALE = 1.0f
        private const val ICON_SIZE = 48f // Size of the animated icon
        
        /**
         * Convenience method to start the wishlist animation
         * @param context The context
         * @param startView The view to animate from (usually the wishlist button in the item)
         * @param endView The view to animate to (usually the wishlist button in the toolbar)
         * @param parentView The parent view to add the animated view to (usually the root view of the fragment)
         * @param onAnimationEnd Callback when animation completes
         */
        @JvmStatic
        fun animate(
            context: Context,
            startView: View,
            endView: View,
            parentView: ViewGroup,
            onAnimationEnd: (() -> Unit)? = null
        ) {
            Log.d(TAG, "Starting wishlist animation")
            Log.d(TAG, "Start view: ${startView.id}, End view: ${endView.id}, Parent: ${parentView.id}")
            WishlistAnimation().startWishlistAnimation(context, startView, endView, parentView, onAnimationEnd)
        }
    }
    
    /**
     * Creates and starts the wishlist fly animation
     * @param context The context
     * @param startView The view to animate from (usually the wishlist button in the item)
     * @param endView The view to animate to (usually the wishlist button in the toolbar)
     * @param parentView The parent view to add the animated view to (usually the root view of the fragment)
     * @param onAnimationEnd Callback when animation completes
     */
    fun startWishlistAnimation(
        context: Context,
        startView: View,
        endView: View,
        parentView: ViewGroup,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        try {
            Log.d(TAG, "Calculating positions...")
            
            // Calculate start and end positions
            val startPosition = getViewCenterPosition(startView, parentView)
            val endPosition = getViewCenterPosition(endView, parentView)
            
            Log.d(TAG, "Start position: $startPosition, End position: $endPosition")
            
            // Validate positions
            if (startPosition.x < 0 || startPosition.y < 0 || endPosition.x < 0 || endPosition.y < 0) {
                Log.e(TAG, "Invalid positions calculated")
                onAnimationEnd?.invoke()
                return
            }
            
            // Create the animated view (heart icon)
            val animatedView = createAnimatedView(context, startPosition)
            Log.d(TAG, "Created animated view at position: $startPosition")
            
            // Add the animated view to the parent
            parentView.addView(animatedView)
            Log.d(TAG, "Added animated view to parent")
            
            // Create and start the animation
            val animatorSet = createAnimationSet(animatedView, startPosition, endPosition)
            
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    Log.d(TAG, "Animation started")
                }
                
                override fun onAnimationEnd(animation: Animator) {
                    Log.d(TAG, "Animation ended")
                    try {
                        // Remove the animated view from the parent
                        parentView.removeView(animatedView)
                        Log.d(TAG, "Removed animated view from parent")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error removing animated view: ${e.message}")
                        // View might already be removed
                    }
                    onAnimationEnd?.invoke()
                }
            })
            
            Log.d(TAG, "Starting animator set...")
            animatorSet.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error in wishlist animation: ${e.message}", e)
            // If animation fails, just invoke the callback
            onAnimationEnd?.invoke()
        }
    }
    
    /**
     * Creates the animated view (heart icon)
     */
    private fun createAnimatedView(context: Context, startPosition: PointF): ImageView {
        return ImageView(context).apply {
            setImageResource(R.drawable.ic_wishlist_filled)
            layoutParams = ViewGroup.LayoutParams(
                ICON_SIZE.toInt(),
                ICON_SIZE.toInt()
            )
            
            // Set initial position using absolute coordinates
            x = startPosition.x - ICON_SIZE / 2f
            y = startPosition.y - ICON_SIZE / 2f
            
            // Set initial scale
            scaleX = INITIAL_SCALE
            scaleY = INITIAL_SCALE
            
            // Set initial alpha
            alpha = 1.0f
            
            // Ensure the view is visible and above other views
            visibility = View.VISIBLE
            elevation = 1000f // Ensure it's above other views
            
            Log.d(TAG, "Created ImageView with size: ${ICON_SIZE.toInt()}x${ICON_SIZE.toInt()}")
            Log.d(TAG, "ImageView position: x=$x, y=$y")
        }
    }
    
    /**
     * Creates the complete animation set
     */
    private fun createAnimationSet(
        animatedView: View,
        startPosition: PointF,
        endPosition: PointF
    ): AnimatorSet {
        val animatorSet = AnimatorSet()
        
        // Create path animation (curved path)
        val pathAnimation = createPathAnimation(animatedView, startPosition, endPosition)
        
        // Create scale animation
        val scaleAnimation = createScaleAnimation(animatedView)
        
        // Create fade animation
        val fadeAnimation = createFadeAnimation(animatedView)
        
        // Play all animations together
        animatorSet.playTogether(pathAnimation, scaleAnimation, fadeAnimation)
        animatorSet.duration = ANIMATION_DURATION
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        
        return animatorSet
    }
    
    /**
     * Creates a curved path animation from start to end position
     */
    private fun createPathAnimation(
        animatedView: View,
        startPosition: PointF,
        endPosition: PointF
    ): Animator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ANIMATION_DURATION
            interpolator = OvershootInterpolator(0.8f)
            
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                
                // Create a curved path using quadratic bezier curve
                val controlPoint = PointF(
                    (startPosition.x + endPosition.x) / 2f,
                    startPosition.y - abs(endPosition.y - startPosition.y) * 0.5f
                )
                
                // Calculate position along the curve
                val x = (1 - progress) * (1 - progress) * startPosition.x +
                        2 * (1 - progress) * progress * controlPoint.x +
                        progress * progress * endPosition.x
                
                val y = (1 - progress) * (1 - progress) * startPosition.y +
                        2 * (1 - progress) * progress * controlPoint.y +
                        progress * progress * endPosition.y
                
                // Update the view position using absolute coordinates
                animatedView.x = x - ICON_SIZE / 2f
                animatedView.y = y - ICON_SIZE / 2f
                
                Log.v(TAG, "Animation progress: $progress, Position: x=$x, y=$y")
            }
        }
    }
    
    /**
     * Creates a scale animation that shrinks the view as it moves
     */
    private fun createScaleAnimation(animatedView: View): AnimatorSet {
        val scaleXAnimator = ObjectAnimator.ofFloat(animatedView, "scaleX", INITIAL_SCALE, FINAL_SCALE)
        val scaleYAnimator = ObjectAnimator.ofFloat(animatedView, "scaleY", INITIAL_SCALE, FINAL_SCALE)
        
        return AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            duration = SCALE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
    
    /**
     * Creates a fade animation that fades out the view as it moves
     */
    private fun createFadeAnimation(animatedView: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(animatedView, "alpha", 1.0f, 0.0f).apply {
            duration = FADE_DURATION
            startDelay = ANIMATION_DURATION - FADE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
    
    /**
     * Calculates the center position of a view relative to its parent
     */
    private fun getViewCenterPosition(view: View, parentView: ViewGroup): PointF {
        try {
            val location = IntArray(2)
            view.getLocationInWindow(location)
            
            val parentLocation = IntArray(2)
            parentView.getLocationInWindow(parentLocation)
            
            // Use absolute screen coordinates for better positioning
            val centerX = location[0] + view.width / 2f
            val centerY = location[1] + view.height / 2f
            
            Log.d(TAG, "View location: [${location[0]}, ${location[1]}], size: ${view.width}x${view.height}")
            Log.d(TAG, "Parent location: [${parentLocation[0]}, ${parentLocation[1]}]")
            Log.d(TAG, "Calculated center: [$centerX, $centerY]")
            
            return PointF(centerX, centerY)
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating view position: ${e.message}", e)
            // Return a default position if calculation fails
            return PointF(0f, 0f)
        }
    }
}
