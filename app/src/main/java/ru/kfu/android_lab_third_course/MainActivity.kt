package ru.kfu.android_lab_third_course

import android.os.Build
import android.os.Bundle
import android.transition.*
import android.transition.Scene.getSceneForLayout
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.scene_2.*


class MainActivity : AppCompatActivity() {

    lateinit var hiddenButton: Button
    lateinit var sceneRoot: ViewGroup
    lateinit var scene1: Scene
    lateinit var scene2: Scene
    var sceneChangeFlag: Boolean = true

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneRoot = container

        scene1 = getSceneForLayout(sceneRoot, R.layout.scene_1, this)
        scene2 = getSceneForLayout(sceneRoot, R.layout.scene_2, this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onClick(view: View) {
        if (!sceneChangeFlag) {
            hiddenButton = findViewById(R.id.btn2)
            val animationRotate =
                AnimationUtils.loadAnimation(applicationContext, R.anim.rotation_360_to_0).apply {
                    setAnimationListener(animationListener)
                }
            hiddenButton.startAnimation(animationRotate)
            sceneChangeFlag = true
        } else {
            val set = TransitionSet()

            set.addTransition(Fade().apply {
                duration = 600
                addTarget(btn2.id)
            })

            set.addTransition(ChangeBounds().apply {
                duration = 250
                interpolator = AccelerateInterpolator()
                addTarget(btn1.id)
            })

            set.ordering = TransitionSet.ORDERING_TOGETHER
            set.addListener(firstSceneTransitionListener)
            TransitionManager.go(scene2, set)
            sceneChangeFlag = false
        }

    }


    private val firstSceneTransitionListener = @RequiresApi(Build.VERSION_CODES.KITKAT)
    object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition?) {
            hiddenButton = findViewById(R.id.btn2)
            val animationRotate =
                AnimationUtils.loadAnimation(applicationContext, R.anim.rotation_0_to_360)
            hiddenButton.startAnimation(animationRotate)
        }

        override fun onTransitionResume(transition: Transition?) {}

        override fun onTransitionPause(transition: Transition?) {}

        override fun onTransitionCancel(transition: Transition?) {}

        override fun onTransitionStart(transition: Transition?) {}
    }

    private val animationListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onAnimationEnd(animation: Animation?) {
            val reverseSet = TransitionSet()
            reverseSet.addTransition(Fade().apply {
                duration = 500
                addTarget(btn2.id)
            })
            reverseSet.addTransition(ChangeBounds().apply {
                duration = 500
                interpolator = AccelerateInterpolator()
                addTarget(btn1.id)
            })
            reverseSet.ordering = TransitionSet.ORDERING_TOGETHER
            TransitionManager.go(scene1, reverseSet)
        }

        override fun onAnimationStart(animation: Animation?) {}
    }

}