package com.truevalue.dreamappeal.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.truevalue.dreamappeal.R
import java.text.SimpleDateFormat
import java.util.*

fun Int?.value(def: Int = 0) = this ?: def

fun Boolean?.value(def: Boolean = false) = this ?: def

fun String?.value(def: String = "") = this ?: def

fun Float?.value(def: Float = 0f) = this ?: def

fun Double?.value(def: Double = 0.0) = this ?: def

fun Long?.value(def: Long = 0) = this ?: def

fun Array<*>?.isNotNullAndEmpty() = this != null && this.isNotEmpty()

fun <T> List<T>?.value() = this ?: emptyList()

fun <T> MutableList<T>?.toValue() = this ?: arrayListOf()

fun Collection<*>?.isNotNullAndEmpty() = this != null && this.isNotEmpty()

fun String?.isNotNullAndEmpty() = this != null && this.isNotEmpty()

fun ImageView.load(data: Any, @DrawableRes placeholderId: Int? = null) {
    var glide = Glide.with(this).load(data)
    if (placeholderId != null) {
        glide = glide.placeholder(placeholderId)
    }
    glide.centerCrop().into(this)
//    Picasso.get().load(url).fit().centerCrop().into(this)
}

fun ImageView.setTint(@ColorRes color: Int) {
    setColorFilter(getColor(context, color))
}

inline fun <reified T : Activity> Context.openActivity(extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun Int.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun View.gone() {
    if (this.visibility != View.GONE) this.visibility = View.GONE
}

fun View.visible() {
    if (this.visibility != View.VISIBLE) this.visibility = View.VISIBLE
}

fun View.invisible() {
    if (this.visibility != View.INVISIBLE) this.visibility = View.INVISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun SpannableString.spanWith(target: String, @ColorInt color: Int) {
    if (this.contains(target)) {
        val start = this.indexOf(target)
        val end = start + target.length
        setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}

fun EditText.validateEmptyEditText(errorText: String): Boolean {
    if (text.isNullOrEmpty()) {
        error = errorText
        return false
    }
    return true
}

fun EditText.validateEmptyEditText(onResult: (Boolean) -> Unit): Boolean {
    if (text.isNullOrEmpty()) {
        onResult.invoke(false)
        return false
    }
    onResult.invoke(true)
    return true
}

fun TextView.validateEmptyEditText(errorText: String): Boolean {
    if (text.isNullOrEmpty()) {
        error = errorText
        return false
    }
    return true
}

fun EditText.validateValueEditText(isVal: Boolean, errorText: String): Boolean {
    if (!isVal) {
        error = errorText
    }
    return isVal
}


fun View.rotate(deltaAngle: Float, duration: Long = 300) {
    val animator = ObjectAnimator.ofFloat(
        this, "rotation", rotation, rotation + deltaAngle
    )
    animator.duration = duration // miliseconds
    animator.start()
}

fun Group.setAllOnClickListener(listener: (View) -> Unit) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id)?.setOnClickListener(listener)
    }
}

fun TextView.addOnChangeTextDebounce(
    delay: Long = 400,
    onTextChanged: (((String) -> Unit))? = null,
    onTextChangedDebounce: ((String) -> Unit)
) {

    val handler = Handler()
    var runnable: Runnable? = null

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            runnable = Runnable {
                onTextChangedDebounce(s.toString())
            }
            handler.postDelayed(runnable, delay)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s.toString())
            if (runnable != null) {
                handler.removeCallbacks(runnable)
            }
        }
    })
}

fun Context.checkPermission(vararg permissions: String, onGranted: () -> Unit) {
    val neededPermissionsCheck = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
    }.toTypedArray()

    if (neededPermissionsCheck.isNotEmpty()) {
        val listener = object : PermissionListener {
            override fun onPermissionGranted() {
                onGranted.invoke()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

            }
        }

        TedPermission.with(this)
            .setPermissionListener(listener)
            .setPermissions(*neededPermissionsCheck)
            .check()
    } else {
        onGranted.invoke()
    }
}

fun RecyclerView.setLoadMore(loadMore: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val pastVisiblesItems =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loadMore.invoke()
                }
            }
        }
    })
}

fun TextView.setDrawableColor(@ColorRes color: Int) {
    compoundDrawables.filterNotNull().forEach {
        it.colorFilter = PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT, delayDuration: Long = 500) {
    val toast = Toast.makeText(this, msg, duration)
    toast.show()
    delay(delayDuration) {
        toast.cancel()
    }
}

fun TextView.setNonBlankText(mText: String?) {
    if (mText.isNullOrEmpty()) {
        gone()
    } else {
        text = mText
    }
}

fun delay(duration: Long, run: () -> Unit) {
    Handler().postDelayed({
        run.invoke()
    }, duration)
}

fun View.changeRatioHeightView(parentLayout: ConstraintLayout, ratio: String) {
    if (this is ImageView) {
        val type =
            if (ratio == "1:1") ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_XY
        scaleType = type
    }
    val set = ConstraintSet()
    set.clone(parentLayout)
    set.setDimensionRatio(id, ratio)
    set.applyTo(parentLayout)
}

fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

fun Activity.enableLightStatusBar(enable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (enable) {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.statusBarColor = getColor(R.color.white).value()
        } else {
            window?.decorView?.systemUiVisibility = 0
            window?.statusBarColor = getColor(R.color.colorPrimary).value()
        }
    }
}

fun TextView.changeFontFromAsset(path: String) {
    typeface = Typeface.createFromAsset(context.assets, path)
}


fun TabLayout.getTextViewAt(position: Int): TextView {
    val tabLayout = (getChildAt(0) as ViewGroup).getChildAt(position) as ViewGroup
    return tabLayout.getChildAt(1) as TextView
}

fun TabLayout.setBoldFontWhenSelected() {
    getTextViewAt(0).changeFontFromAsset("fonts/SpoqaHanSansBold.ttf")
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            if (p0 != null) {
                getTextViewAt(p0.position)
                    .changeFontFromAsset("fonts/SpoqaHanSansRegular.ttf")
            }
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            if (p0 != null) {
                getTextViewAt(p0.position)
                    .changeFontFromAsset("fonts/SpoqaHanSansBold.ttf")
            }
        }
    })
}

@SuppressLint("SimpleDateFormat")
fun String.formatDate(inputPattern: String, outPutPattern: String): String {
    val date = SimpleDateFormat(inputPattern).parse(this)
    return SimpleDateFormat(outPutPattern).format(date)
}



