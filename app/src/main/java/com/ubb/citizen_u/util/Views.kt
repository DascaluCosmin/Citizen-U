package com.ubb.citizen_u.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.fadeOut() {
    animate()
        .alpha(0.0f)
        .setDuration(1000L)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
        })
}

fun View.fadeIn() {
    apply {
        alpha = 0.0f
        visibility = View.VISIBLE
        animate()
            .alpha(1.0f)
            .setDuration(2000L)
            .setListener(null)
    }
}