package com.fphoenixcorneae.navigation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val mIconResources = listOf(
        R.mipmap.ic_home_main,
        R.mipmap.ic_home_project,
        R.mipmap.ic_home_square,
        R.mipmap.ic_home_vipcn,
        R.mipmap.ic_home_mine,
    )
    private val mTexts = listOf(
        "首页",
        "项目",
        "广场",
        "公众号",
        "我的",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AnimatedNavigation>(R.id.animatedNavigation).apply {
            // Set background tint
            backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            // Set items
            setImageTextItems(
                mIconResources.flatMapIndexed { index: Int, id: Int ->
                    listOf(ImageTextItem(context).apply {
                        // Set icon
                        setIconResource(id)
                        // Set icon size
                        setIconSize(28f)
                        // Set text size
                        textSize = 16f
                        // Set normal state tint color and selected state tint color
                        setIconTextColor(Color.BLACK, Color.RED)
                        // Set padding between icon and text
                        setIconTextPadding(4f)
                        // Set text
                        text = mTexts[index]
                    })
                }
            )
            // Set animated item size
            setAnimatedItemSize(60f)
            // Set animated item content padding
            setAnimatedItemContentPadding(8f)
            // Set animated item double click
            setOnAnimatedItemDoubleClickListener {
                Toast.makeText(context, "double click position: $it", Toast.LENGTH_SHORT).show()
                Log.d("AnimatedNavigation", "double click position: $it")
            }
            // Set item click
            setOnItemClickListener { itemView, position ->
                Log.d("AnimatedNavigation", "itemView: $itemView, position: $position")
            }
        }
    }
}