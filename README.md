# AnimatedNavigation

## 酷炫的底部导航栏

[![](https://jitpack.io/v/FPhoenixCorneaE/AnimatedNavigation.svg)](https://jitpack.io/#FPhoenixCorneaE/AnimatedNavigation)

![](https://github.com/FPhoenixCorneaE/AnimatedNavigation/blob/main/screenshots/animated-navigation.gif)

### How to include it in your project:

**Step 1.** Add the JitPack repository to your build file.

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url "https://jitpack.io"
        }
    }
}
```

**Step 2.** Add the dependency.

```groovy
dependencies {
    implementation("com.github.FPhoenixCorneaE:AnimatedNavigation:$latest")
}
```

### xml中使用

```xml
<com.fphoenixcorneae.navigation.AnimatedNavigation 
    android:id="@+id/animatedNavigation"
    android:layout_width="wrap_content" 
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent" 
    app:layout_constraintStart_toStartOf="parent" />
```

### 代码中使用

```kotlin
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
```
